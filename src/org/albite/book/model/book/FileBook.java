/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import org.albite.book.processor.MarkupProcessor;
import org.albite.book.processor.PlainTextProcessor;

/**
 *
 * @author albus
 */
public class FileBook extends Book {

    /*
     * Book file
     */
    private FileConnection bookFile;

    public FileBook(
            final String filename,
            final MarkupProcessor processor,
            final String encoding)
            throws IOException, BookException {

        this.processor = processor;

        bookFile = (FileConnection) Connector.open(filename, Connector.READ);
        language = Languages.LANG_EN;

        try {
            /*
             * load chapters info (filename + title)
             */
            chapters = loadChaptersDescriptor(encoding);
            currentChapter = chapters[0];
            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    protected Chapter[] loadChaptersDescriptor(final String encoding)
            throws BookException, IOException {

        return new Chapter[] {
            new Chapter(this, "Chapter #1",
                    bookFile, (int) bookFile.fileSize(),
                    encoding,
                    processor
                    , 0)
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