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
import org.albite.io.PartitionedConnection;
import org.albite.util.archive.zip.ArchiveZip;

/**
 *
 * @author albus
 */
public class FileBook extends Book {

    /**
     * The maximum file size after which the Filebook is split
     * forcefully into chapters. The split is a dumb one, for it splits
     * on bytes, not characters or tags, i.e. it may split a utf-8 character
     * in two halves, making it unreadable (so that it would be visible as a
     * question mark) or it may split an HTML tag (so that it would become
     * useless and be shown in the text of the chapter)
     */
    public static final int MAX_FILESIZE = 64 * 1024;

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
        language = null;

        try {
            /*
             * load chapters info (filename + title)
             */
            chapters = loadChaptersDescriptor();
            linkChapters();

            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    protected Chapter[] loadChaptersDescriptor()
            throws BookException, IOException {

        final int size = (int) bookFile.fileSize();

        if (size <= MAX_FILESIZE) {
            return new Chapter[] {
                new Chapter(
                        bookFile, size, "Chapter #1", processHtmlEntities, 0)
            };
        } else {

            int kMax = size / MAX_FILESIZE;
            if (size % MAX_FILESIZE > 0) {
                kMax++;
            }

            Vector chaps = new Vector(kMax);

            int left = size;
            int chapSize;

            for (int k = 0; k < kMax; k++) {
                chapSize = (left > MAX_FILESIZE ? MAX_FILESIZE : left);
                chaps.addElement(new Chapter(
                        new PartitionedConnection(
                            bookFile, k * MAX_FILESIZE, chapSize),
                            chapSize,
                            "Chapter #" + (k + 1),
                            processHtmlEntities,
                            k
                        ));
                left -= MAX_FILESIZE;
            }

            Chapter[] res = new Chapter[chaps.size()];
            chaps.copyInto(res);
            return res;
        }
    }

    public final String getURL() {
        return bookFile.getURL();
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