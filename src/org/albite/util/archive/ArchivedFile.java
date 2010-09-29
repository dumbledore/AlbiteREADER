/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import com.tinyline.util.GZIPInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.io.InputConnection;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Albus Dumbledore
 */
public class ArchivedFile implements InputConnection {
    protected Archive archive;

    protected String filename;
    protected int position;
    protected int size;
    protected boolean compressed;

    public ArchivedFile(
            final Archive file,
            final String filename,
            final int position,
            final int size,
            final boolean compressed) {

        this.archive = file;
        this.filename = filename;
        this.position = position;
        this.size = size;
        this.compressed = compressed;
    }

    public final int getSize() {
        return size;
    }

    public final int getPosition() {
        return position;
    }

    public final String getFileName() {
        return filename;
    }

    /**
     * Opens an input stream to the file.
     * Note that because of the underlying FileConnection,
     * only <b>one</b> input stream is allowed to be open at a time!
     *
     * @return  InputStream to the ArchivedFile.
     *          It will be decompressed, if necessary.
     *
     * @throws IOException
     */
    public final InputStream openInputStream() throws IOException {
        if (compressed) {
            return new GZIPInputStream(new ArchiveInputStream(this));
        } else {
            return new ArchiveInputStream(this);
        }
    }

    public final DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    public final byte[] getAsBytes() throws IOException {
        final byte[] buf = new byte[size];
        DataInputStream data = new DataInputStream(openInputStream());
        data.readFully(buf);
        data.close();
        return buf;
    }

    public final char[] getAsChars() throws IOException {

        InputStreamReader isr =
                new InputStreamReader(openInputStream(), "UTF-8");

        /*
         * Create the buffer. It may be larger than necessary
         * as 3 bytes might fit into a single char (2 bytes).
         */
        final char[] buf = new char[size];

        /*
         * Read everything and store the number of chars read.
         */
        final int realSize = isr.read(buf);

        /*
         * Close the stream
         */
        isr.close();

        /*
         * If read less chars than "size", return a "trimmed" char[].
         */
        if (realSize == size) {
            return buf;
        } else {
            final char[] result = new char[realSize];
            System.arraycopy(buf, 0, result, 0, realSize);
            return result;
        }
    }

    public final Image getAsImage() throws IOException {
        return Image.createImage(openInputStream());
    }

    public final int[] getAsImageDimensions() throws IOException {
        DataInputStream din = new DataInputStream(openInputStream());

        /*
         * skipping PNG header
         */
        din.skipBytes(16);

        final int[] result = {0, 0};

        result[0] = din.readInt();
        result[1] = din.readInt();

        din.close();

        return result;
    }

    /*
     * This doesn't do anything for it MUST NOT close the underlying
     * FileConnection which may be reaused across other ArchivedFile objects.
     */
    public final void close() {}
}