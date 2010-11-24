package org.albite.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;
import org.albite.lang.TextTools;

/**
 * Implements random reading from a
 * {@link javax.microedition.io.file.FileConnection FileConnection}
 *
 * The essential methods are {@link RandomReadingFile#seek(int)
 * seek(int position)}
 * and {@link RandomReadingFile#getPointer() getPointer()}.
 *
 * This class also re-implements all the methods from
 * {@link java.io.InputStream}
 *
 * All other methods are only wrappings around
 * the underlying {@link java.io.DataInputStream} or <code>FileConnection</code>
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 * @version 1.0.0
 */
public class RandomReadingFile extends InputStream
        implements DataInput, Connection {

    private FileConnection      file;
    private DataInputStream     in;

    /**
     * The pointer is set to {@link java.lang.Integer#MAX_VALUE} in order to
     * be able to initialize the {@link java.io.DataInputStream} simply by
     * using {@link RandomReadingFile#seek(int) seek(0)}
     */
    private int                 pointer = Integer.MAX_VALUE;

    private RandomReadingFile() {}

    /**
     * Creates a new RandomReadingFile from a valid file URL.
     *
     * @param url The URL of the file that is being opened.
     * @throws IOException
     */
    public RandomReadingFile(String url) throws IOException {
        System.out.println("Opening: [" + url + "]");
        file = (FileConnection) Connector.open(url, Connector.READ);

        if (file.isDirectory() || !file.exists()) {
            throw new IOException("File not found");
        }

        seek(0);
    }

    /**
     * @return the size of the file
     * @throws ConnectionClosedException    if the file is closed.
     * @throws java.lang.SecurityException  if the security of the
     *                                      application does not have read
     *                                      access for the file
     */
    public final int length() throws IOException {
        return (int) file.fileSize();
    }

    public final String getName() {
        return file.getName();
    }

    public final String getURL() {
        return file.getURL();
    }

    public final String getPath() {
        return file.getPath();
    }

    /**
     * Seeks to the specified position
     *
     * @param position
     * @throws IllegalArgumentException if the <code>position</code> argument
     * is wrong, i.e.:<p />
     * <code>position &lt; 0</code><p />
     * or<p />
     * {@link RandomReadingFile#length()} &lt;= position</code>
     * @throws IOException if an IOException occurred
     */
    public final void seek(final int position) throws IOException {

        if (position == pointer) {
            return;
        }

        if (position < 0 || position >= length()) {
            throw new IllegalArgumentException(
                    "Trying to seek outside file's contents");
        }

        final int pos;

        if (position < pointer) {
            /*
             * Must go back, i.e. it's necessary to reopen the input stream
             */

            /*
             * Close input before reopening it
             */
            if (in != null) {
                in.close();
            }

            in = file.openDataInputStream();
            pos = position;
        } else {
            pos = position - pointer;
        }

        skipBytes(pos);

        pointer = position;
    }

    public final long skip(long n) throws IOException {
        return skipBytes((int) n);
    }

    public final int skipBytes(final int n) throws IOException {
        int skipped = in.skipBytes(n);
        pointer += skipped;
        return skipped;
    }

    public final boolean readBoolean() throws IOException {
        pointer++;
        return in.readBoolean();
    }

    public final byte readByte() throws IOException {
        pointer++;
        return in.readByte();
    }

    public final char readChar() throws IOException {
        pointer += 2;
        return in.readChar();
    }

    public final double readDouble() throws IOException {
	pointer += 8;
        return in.readDouble();
    }

    public final float readFloat() throws IOException {
	pointer +=4;
        return in.readFloat();
    }

    public final int readInt() throws IOException {
	pointer += 4;
	return in.readInt();
    }

    public final int readUnsignedByte() throws IOException {
        pointer++;
        return in.readUnsignedByte();
    }

    public final int readUnsignedShort() throws IOException {
	pointer += 2;
	return in.readUnsignedShort();
    }

    public final String readUTF() throws IOException {
        return new String(readUTFchars());
    }

    public final char[] readUTFchars() throws IOException {

        /*
         * There is no need to advance the pointer manually,
         * as the pointer is advanced automatically by
         * readUnsignedShort() and readFully methods
         * and this method interacts with the input stream
         * only through them
         */

        int utflen = readUnsignedShort();

        StringBuffer str = new StringBuffer(utflen);
        byte bytearr[] = new byte[utflen];
        int c, char2, char3;
	int count = 0;

 	readFully(bytearr, 0, utflen);

	while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		    /* 0xxxxxxx*/
		    count++;
                    str.append( (char)c);
		    break;
                case 12: case 13:
		    /* 110x xxxx   10xx xxxx*/
		    count += 2;

                    if (count > utflen) {
			throw new UTFDataFormatException();
                    }

                    char2 = (int) bytearr[count-1];

                    if ((char2 & 0xC0) != 0x80) {
			throw new UTFDataFormatException();
                    }

                    str.append((char)(((c & 0x1F) << 6) | (char2 & 0x3F)));
		    break;

                case 14:
		    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen) {
			throw new UTFDataFormatException();
                    }
                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
			throw new UTFDataFormatException();
                    }

                    str.append((char)(((c     & 0x0F) << 12) |
                                      ((char2 & 0x3F) << 6)  |
                                      ( char3 & 0x3F      )));
		    break;

	        default:
		    /* 10xx xxxx,  1111 xxxx */
		    throw new UTFDataFormatException();
		}
	}

        // The number of chars produced may be less than utflen
        final char[] result = new char[str.length()];
        str.getChars(0, str.length(), result, 0);
        return result;
    }

    public final long readLong() throws IOException {
	pointer += 8;
        return in.readLong();
    }

    public final short readShort() throws IOException {
        pointer += 2;
        return in.readShort();
    }

    public final int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public final int read(byte[] b, int off, int len) throws IOException {
        int count = in.read(b, off, len);

        if (count > 0) {
            pointer += count;
        }

        return count;
    }

    public final int read() throws IOException {
        int read = in.read();

        if (read >= 0) {
            pointer++;
        }

        return read;
    }

    public final void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    public final void readFully(byte b[], int off, int len)
            throws IOException {

        pointer += len;
        in.readFully(b, off, len);
    }

    /**
     * Closes the file and the underlying
     * {@link javax.microedition.io.file.FileConnection}
     * @throws IOException
     */
    public final void close() throws IOException {
        in.close();
        file.close();
    }

    /**
     * Returns the current position of the reading pointer in the file
     * @return the current position of the reading pointer in the file
     */
    public final int getPointer() {
        return pointer;
    }

    public static String getPathFromURL(final String url) {
        int i = url.lastIndexOf('/');

        if (i == -1) {
            i = url.lastIndexOf('\\');
        }

        if (i >= 0) {
            return url.substring(0, i + 1);
        }

        return "";
    }

    public static String relativeToAbsoluteURL(final String url) {
        final Vector split = TextTools.split(url, new char[] { '\\', '/'});
        int size = split.size();
        String s;
        for (int i = 0; i < split.size();) {
            s = (String) split.elementAt(i);

            if (s.equals(".")) {
                split.removeElementAt(i);
                continue;
            }

            if (s.equals("..")) {
                split.removeElementAt(i);
                if (i > 0) {
                    i--;
                    split.removeElementAt(i);
                }
                continue;
            }

            i++;
        }

        if (split.isEmpty()) {
            return "";
        }

        final StringBuffer newUrl =
                new StringBuffer((String) split.elementAt(0));

        for (int i = 1; i < split.size(); i++) {
            newUrl.append('/');
            newUrl.append((String) split.elementAt(i));
        }

        return newUrl.toString();
    }

    /**
     * Changes the extension of a URL
     *
     * Note: the new extension may be with or without a preceding dot.
     *
     * @param url           The original URL
     * @param newExtension
     * @return
     */
    public static String changeExtension(
            final String url, String newExtension) {

        if (!newExtension.startsWith(".")) {
            newExtension = "." + newExtension;
        }

        int dotpos = url.lastIndexOf('.');

        if (dotpos == -1 ) {
            /*
             * Original URL doesn't have an extension
             */
            return url + newExtension;
        } else {
            return url.substring(0, dotpos) + newExtension;
        }
    }
}