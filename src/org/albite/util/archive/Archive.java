/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import javax.microedition.io.*;
import javax.microedition.io.file.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Albus Dumbledore
 */
public class Archive {

    public static final int MAGIC_NUMBER_ALBR = 1095516754;
    public static final String FILE_EXTENSION = ".alb";

    private FileConnection      file;
    private Hashtable           files;
    private int                 crc32;
    protected DataInputStream   fileData;

    public Archive() {
        file        = null;
        files       = null;
    }

    public void open(String fname) throws ArchiveException, IOException {
        if (files != null)
            files.clear();

        try {
            file = (FileConnection)Connector.open(fname, Connector.READ);
            if (!file.exists())
                throw new IOException("File does not exist!");
            
            fileData = new DataInputStream(file.openDataInputStream());
            fileData.mark(Integer.MAX_VALUE); //mark start of archive

            String filename;

            int size, compressedSize;
            boolean compressed = false;

            //Check magic number
            if (fileData.readInt() != MAGIC_NUMBER_ALBR)
                throw new ArchiveException("The file is not an ALB archive at all.");

            this.crc32 = fileData.readInt();
            
            //Scan archive for files
            int expectedCount = fileData.readInt();

            if (files == null)
                files = new Hashtable(expectedCount);

            int position = 12; //Magic number, CRC32, expectedCount
            
            for (int i=0; i < expectedCount; i++) {
                filename = fileData.readUTF();
                position += filename.length() + 3; //only 1-bit ASCII chars supported

                compressed = fileData.readBoolean();
                if (compressed) {
                    size = fileData.readInt();
                    compressedSize = fileData.readInt();
                    position += 8;
//                    System.out.println(filename + "(" + (size / 1024) +  "KB, compression: " + compressed + ") @ " + position);
                    files.put(filename, new ArchivedFile(this, filename, position, size, compressed));
                    fileData.skipBytes(compressedSize);
                    position += compressedSize;
                } else {
                    size = fileData.readInt();
                    position += 4;
//                    System.out.println(filename + "(" + (size / 1024) +  "KB, compression: " + compressed + ") @ " + position);
                    files.put(filename, new ArchivedFile(this, filename, position, size, compressed));
                    fileData.skipBytes(size);
                    position += size;
                }
            }

            if (fileData.available() != 0) { //more/less files than archive says there should be
                close();
                throw new ArchiveException("Archive is corrupted. (Free space after last file)");
            }

        } catch(EOFException eofe) {
            close();
            throw new ArchiveException("Archive is corrupted.");

        } catch(UTFDataFormatException udfe) {
            close();
            throw new ArchiveException("Archive is corrupted.");

        } catch(IllegalArgumentException iae) {
            close();
            throw new ArchiveException("Archive is corrupted.");

        } catch(IOException ioe) {
            close();
            throw ioe;
        }
    }

    public void close()  throws IOException {
        fileData.close();
        file.close();
        fileData = null;
        file = null;
        files = null;
    }

    public boolean isOpen() {
        return file != null;
    }
    
    public void listFiles() {
        for (Enumeration e = files.elements() ; e.hasMoreElements() ;) {
            System.out.println(e.nextElement());
        }
    }

    public ArchivedFile getFile(String filename) {
        Object o = files.get(filename);

        if (o == null)
            return null; //file not found

        return (ArchivedFile)o;
    }

    public String getFileURL() {
        return file.getURL();
    }

    public int getCRC() {
        return crc32;
    }
}
