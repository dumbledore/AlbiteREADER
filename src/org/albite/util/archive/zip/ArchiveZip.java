/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive.zip;

import gnu.zip.ZipEntry;
import gnu.zip.ZipFile;
import java.io.IOException;
import org.albite.io.RandomReadingFile;
import org.albite.util.archive.Archive;
import org.albite.util.archive.ArchiveEntry;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class ArchiveZip implements Archive {
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

    public final ArchiveEntry getEntry(final String name) {
        ZipEntry entry = zip.getEntry(name);

        if (entry == null) {
            return null;
        }

        return new ArchiveZipEntry(zip, entry);
    }

    public final String getURL() {
        return file.getURL();
    }

    public final int fileSize() throws IOException {
        return file.length();
    }
}