/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model;

import java.io.IOException;
import org.albite.book.parser.HTMLTextParser;
import org.albite.io.AlbiteStreamReader;
import org.albite.util.archive.zip.ArchiveZip;
import org.albite.util.archive.zip.ArchiveZipEntry;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class EPubBook extends Book {

    /*
     * Book file
     */
    private ArchiveZip bookArchive;

    public EPubBook(final String filename)
            throws IOException, BookException {

        this.parser = new HTMLTextParser();

        bookArchive = new ArchiveZip(filename);

        language = Languages.LANG_EN;

        try {
            /*
             * load chapters info (filename + title)
             */
            final ArchiveZipEntry chapterFile
                    = bookArchive.getEntry("text.xhtml");

            final Chapter chapter =
                new Chapter(
                        chapterFile,
                        chapterFile.fileSize(),
                        AlbiteStreamReader.DEFAULT_ENCODING,
                        "Chapter #1", 0);
            chapters = new Chapter[] {chapter};
            currentChapter = chapter;
            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    public String getURL() {
        return bookArchive.getURL();
    }
}