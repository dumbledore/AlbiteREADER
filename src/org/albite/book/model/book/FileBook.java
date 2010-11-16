/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import org.albite.book.model.parser.TextParser;
import org.albite.util.archive.zip.ArchiveZip;

/**
 *
 * @author albus
 */
public class FileBook extends Book {

    /*
     * Book file
     */
    private final FileConnection bookFile;
    private final boolean processHtmlEntities;

    public FileBook(
            final String filename,
            final TextParser parser,
            final boolean processHhtmlEntities,
            final boolean lightMode)
            throws IOException, BookException {

        this.bookURL = filename;
        this.parser = parser;
        this.processHtmlEntities = processHhtmlEntities;

        bookFile = (FileConnection) Connector.open(filename, Connector.READ);
        language = null;

        try {
            /*
             * load chapters info (filename + title)
             */
            chapters = loadChaptersDescriptor(lightMode);
            linkChapters();

            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    protected Chapter[] loadChaptersDescriptor(final boolean lightMode)
            throws BookException, IOException {

        Vector chaps = new Vector();

        splitChapterIntoPieces(
                bookFile,
                (int) bookFile.fileSize(),
                (processHtmlEntities
                    ? getMaximumHtmlFilesize(lightMode)
                    : getMaximumTxtFilesize(lightMode)),
                0,
                processHtmlEntities,
                chaps);

        Chapter[] res = new Chapter[chaps.size()];
        chaps.copyInto(res);
        return res;
    }

    public final void close() throws IOException {
        bookFile.close();
        closeUserFile();
    }

    public Hashtable getMeta() {
        return null;
    }

    public int fileSize() {
        try {
            return (int) bookFile.fileSize();
        }   catch (IOException e) {
            return -1;
        }
    }

    public ArchiveZip getArchive() {
        return null;
    }
}