/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import org.albite.book.model.parser.TextParser;
import org.albite.util.archive.Archive;

/**
 *
 * @author albus
 */
public class FileBook extends Book {

    /*
     * Book file
     */
    private final FileConnection bookFile;
    private final Archive archive;
    private final boolean processHtmlEntities;

    public FileBook(
            final String filename,
            final Archive archive,
            final TextParser parser,
            final boolean processHhtmlEntities)
            throws IOException, BookException {

        this.bookURL = filename;
        this.archive = archive;
        this.parser = parser;
        this.processHtmlEntities = processHhtmlEntities;

        bookFile = (FileConnection) Connector.open(filename, Connector.READ);
        language = null;

        try {
            /*
             * load chapters info (filename + title)
             */
            chapters = loadChaptersDescriptor();
            linkChapters();

            loadUserFiles(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    final Chapter[] loadChaptersDescriptor()
            throws BookException, IOException {

        Vector chaps = new Vector();

        splitChapterIntoPieces(
                bookFile,
                (int) bookFile.fileSize(),
                getArchive(),
                (processHtmlEntities
                ? MAXIMUM_HTML_FILESIZE
                : MAXIMUM_TXT_FILESIZE),
                0, processHtmlEntities, chaps);

        Chapter[] res = new Chapter[chaps.size()];
        chaps.copyInto(res);
        return res;
    }

    public final void close() throws IOException {
        bookFile.close();
        closeUserFiles();
    }

    public Archive getArchive() {
        return archive;
    }
}