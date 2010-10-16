/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive.zip;

import gnu.zip.ZipEntry;
import gnu.zip.ZipFile;
import java.io.IOException;
import javax.microedition.io.Connection;
import org.albite.io.RandomReadingFile;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class ArchiveZip implements Connection {
    private RandomReadingFile file;
    private ZipFile zip;

    public ArchiveZip(final String filename)
            throws IOException {

        this.file = new RandomReadingFile(filename);
        this.zip = new ZipFile(file);
    }

    public final void close() throws IOException {
        zip.close();
    }

    public final ArchiveZipEntry getEntry(final String name) {
        ZipEntry entry = zip.getEntry(name);

        if (entry == null) {
            return null;
        }

        return new ArchiveZipEntry(zip, entry);
    }

    public final String getURL() {
        return file.getURL();
    }

    public final int fileSize() {
        return file.length();
    }
}