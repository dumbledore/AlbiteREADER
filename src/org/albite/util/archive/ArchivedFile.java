/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import com.tinyline.util.GZIPInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Albus Dumbledore
 */
public class ArchivedFile {
    protected Archive archive;

    protected String filename;
    protected int position;
    protected int size;
    protected boolean compressed;

    public ArchivedFile() {
        archive = null;
        filename = null;
        position = -1;
        size = -1;
        compressed = false;
    }

    public ArchivedFile(Archive file, String filename, int position, int size, boolean compressed) {
        this.archive = file;
        this.filename = filename;
        this.position = position;
        this.size = size;
        this.compressed = compressed;
    }

    public int getSize() {
        return size;
    }

    public int getPosition() {
        return position;
    }

    public String getFileName() {
        return filename;
    }

    public InputStream openInputStream() throws IOException {
        if (compressed) {
//            System.out.println("Fetchig compressed data (" + filename + ")");
            return new GZIPInputStream(new ArchiveInputStream(this));
        } else {
//            System.out.println("Fetchig uncompressed data (" + filename + ")");
            return new ArchiveInputStream(this);
        }
    }

    public int getFileContentsAsChars(char[] textBuffer) throws IOException, UnsupportedEncodingException {
        InputStreamReader isr = new InputStreamReader(openInputStream(), "UTF-8");
        int cn = isr.read(textBuffer);
        isr.close();
        return cn;
    }
}