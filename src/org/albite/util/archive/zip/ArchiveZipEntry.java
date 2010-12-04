/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive.zip;

import gnu.zip.ZipEntry;
import gnu.zip.ZipFile;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.albite.util.archive.ArchiveEntry;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class ArchiveZipEntry implements ArchiveEntry {

    private final ZipFile zipfile;
    private final ZipEntry zipentry;

    protected ArchiveZipEntry(
            final ZipFile zipfile, final ZipEntry zipentry) {

        if (zipfile == null || zipentry == null) {
            throw new NullPointerException("null input parameters");
        }
        this.zipfile = zipfile;
        this.zipentry = zipentry;
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    public InputStream openInputStream() throws IOException {
        return zipfile.getInputStream(zipentry);
    }

    public int fileSize() {
        return (int) zipentry.getSize();
    }

    public String getURL() {
        return zipentry.getName();
    }

    public void close() throws IOException {
        /*
         * Does nothing
         */
    }
}