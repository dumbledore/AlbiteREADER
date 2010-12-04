/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive.folder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.file.FileConnection;
import org.albite.util.archive.ArchiveEntry;

/**
 *
 * @author albus
 */
public class ArchiveFileEntry implements ArchiveEntry {
    private final FileConnection file;

    public ArchiveFileEntry(final FileConnection file) {
        this.file = file;
    }

    public DataInputStream openDataInputStream() throws IOException {
        return file.openDataInputStream();
    }

    public InputStream openInputStream() throws IOException {
        return file.openInputStream();
    }

    public void close() throws IOException {
        file.close();
    }

    public int fileSize() throws IOException {
        return (int) file.fileSize();
    }

    public String getURL() {
        return file.getURL();
    }
}