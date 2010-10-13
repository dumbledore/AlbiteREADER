/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import org.albite.book.model.parser.TextParser;

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
            final boolean processHhtmlEntities)
            throws IOException, BookException {

        this.parser = parser;
        this.processHtmlEntities = processHhtmlEntities;

        bookFile = (FileConnection) Connector.open(filename, Connector.READ);
        language = Languages.LANG_EN;

        try {
            /*
             * load chapters info (filename + title)
             */
            chapters = loadChaptersDescriptor();
            currentChapter = chapters[0];
            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    protected Chapter[] loadChaptersDescriptor()
            throws BookException, IOException {

        return new Chapter[] {
            new Chapter(bookFile, (int) bookFile.fileSize(),
                    "Chapter #1", processHtmlEntities, 0)
        };
    }

    public final String getURL() {
        return bookFile.getURL();
    }

    public final void close() throws IOException {
        bookFile.close();
        super.close();
    }
}