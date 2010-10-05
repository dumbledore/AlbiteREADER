/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import org.albite.lang.TextTools;

/**
 *
 * @author Albus Dumbledore
 */
public class Archive implements Connection {

    public static final int     MAGIC_NUMBER_ALBR   = 1095516754;
    public static final String  FILE_EXTENSION      = ".alb";

    protected   FileConnection  file;
    private     Hashtable       files;
    private     int             crc32;

    public Archive(final String fname) throws ArchiveException, IOException {

        try {
            file = (FileConnection)Connector.open(fname, Connector.READ);
            if (!file.exists())
                throw new IOException("File does not exist!");

            final DataInputStream fileData =
                    new DataInputStream(file.openDataInputStream());

            String filename;

            int size, compressedSize;
            boolean compressed = false;

            //Check magic number
            if (fileData.readInt() != MAGIC_NUMBER_ALBR)
                throw new ArchiveException(
                        "The file is not an ALB archive at all.");

            this.crc32 = fileData.readInt();

            //Scan archive for files
            int expectedCount = fileData.readInt();

            files = new Hashtable(expectedCount);

            int position = 12; //Magic number, CRC32, expectedCount
            int filenameLength;
            for (int i=0; i < expectedCount; i++) {
                filenameLength = fileData.readUnsignedShort();
                filename =
                        new String(TextTools.readUTF(fileData, filenameLength));

                position += filenameLength + 3;
//                filename = fileData.readUTF();
//
//                /*
//                 * only 1-bit ASCII chars supported
//                 */
//                position += filename.length() + 3;


                compressed = fileData.readBoolean();
                if (compressed) {
                    size = fileData.readInt();
                    compressedSize = fileData.readInt();
                    position += 8;
                    files.put(filename,
                            new ArchivedFile(
                            this, filename, position, size, compressed));
                    fileData.skipBytes(compressedSize);
                    position += compressedSize;
                } else {
                    size = fileData.readInt();
                    position += 4;
                    files.put(filename,
                            new ArchivedFile(
                            this, filename, position, size, compressed));
                    fileData.skipBytes(size);
                    position += size;
                }
            }

            if (fileData.available() != 0) {
                /*
                 * more/less files than archive says there should be
                 */
                close();
                throw new ArchiveException(
                        "Archive is corrupted. (Free space after last file)");
            }

            fileData.close();

        } catch(EOFException e) {
            close();
            throw new ArchiveException("Archive is corrupted.");

        } catch(UTFDataFormatException e) {
            close();
            throw new ArchiveException("Archive is corrupted.");

        } catch(IllegalArgumentException e) {
            close();
            throw new ArchiveException("Archive is corrupted.");

        } catch(IOException e) {
            close();
            throw e;
        }
    }

    public final void close() throws IOException {
        if (file != null) {
            file.close();
        }
    }

    public final ArchivedFile getFile(final String filename) {
        final Object o = files.get(filename);

        if (o == null) {
            /*
             *  file not found
             */
            return null;
        }

        return (ArchivedFile) o;
    }

    public final String getURL() {
        return file.getURL();
    }

    public final int getCRC() {
        return crc32;
    }
}