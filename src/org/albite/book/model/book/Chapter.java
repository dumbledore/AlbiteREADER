package org.albite.book.model.book;

import java.io.IOException;
import java.io.Reader;
import javax.microedition.io.InputConnection;
import org.albite.io.AlbiteStreamReader;
import org.albite.io.XHTMLStreamReader;

public class Chapter {

    /*
     * Set by default (by xml doc or something else)
     */
    private String                  encoding =
            AlbiteStreamReader.DEFAULT_ENCODING; /* TODO: overwrite it! */

    /*
     * Can be overwritten by the user
     */
    private String                  currentEncoding =
            AlbiteStreamReader.DEFAULT_ENCODING; /* TODO: overwrite it! */


    private final String            title;

    /*
     * Chapter's file & its size
     */
    private final InputConnection   file;
    private final int               fileSize;

    private Chapter                 prevChapter;
    private Chapter                 nextChapter;

    private char[]                  textBuffer;

    private final boolean           processHtmlEntities;

    private int                     currentPosition = 0;

    private final int               number;

    public Chapter(
            final InputConnection file,
            final int fileSize,
            final String title,
            final boolean processHtmlEntities,
            final int number) {

        this.file = file;
        this.fileSize = fileSize;
        this.title = title;
        this.processHtmlEntities = processHtmlEntities;
        this.number = number;
    }

    public final String getTitle() {
        return title;
    }

    public final Chapter getPrevChapter() {
        return prevChapter;
    }

    public final void setPrevChapter(final Chapter bc) {
        prevChapter = bc;
    }

    public final Chapter getNextChapter() {
        return nextChapter;
    }

    public final void setNextChapter(final Chapter bc) {
        nextChapter = bc;
    }

    public final char[] getTextBuffer() {
        if (textBuffer == null) {
            try {
                Reader r = new AlbiteStreamReader(
                        file.openInputStream(), encoding);

                if (processHtmlEntities && r.markSupported()) {
                    r = new XHTMLStreamReader((AlbiteStreamReader) r);
                }

                try {
                    textBuffer = new char[fileSize];
                    int read = r.read(textBuffer);

                    if (read == -1) {
                        System.out.println("Empty chapter");
                        return new char[0];
                    }

                    if (read < fileSize) {
                        char[] res = new char[read];
                        System.arraycopy(textBuffer, 0, res, 0, read);
                        textBuffer = res;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    textBuffer = new char[0];
                } finally {
                    r.close();
                }
            } catch (Exception e) {
                /*
                 * couldn't load the chapter,
                 * it will be rendered as "empty chapter"
                 */
                e.printStackTrace();
                textBuffer = new char[0];
            }
        }
        return textBuffer;
    }

    public final void unload() {
        textBuffer = null;
    }

    public final int getCurrentPosition() {
        return currentPosition;
    }

    public final void setCurrentPosition(final int pos) {
        if (pos < 0) {
            currentPosition = 0;
        }

        currentPosition = pos;
    }

    public final int getNumber() {
        return number;
    }

    public final String getEncoding() {
        return encoding;
    }

    public final void setEncoding(final String encoding) {
        //TODO: Check encoding validity
        this.encoding = encoding;
    }
}