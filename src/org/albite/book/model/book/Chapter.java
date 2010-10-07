package org.albite.book.model.book;

import java.io.IOException;
import javax.microedition.io.InputConnection;
import org.albite.io.AlbiteStreamReader;

public class Chapter {

    private String                  encoding =
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

    private int                     currentPosition = 0;

    private final int               number;

    public Chapter(
            final InputConnection file,
            final int fileSize,
            final String encoding,
            final String title,
            final int number) {

        this.file = file;
        this.fileSize = fileSize;
        this.encoding = encoding;
        this.title = title;
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
                AlbiteStreamReader r = new AlbiteStreamReader(
                        file.openInputStream(), encoding);

                try {
                    textBuffer = r.read(fileSize);
                } catch (IOException e) {
                    textBuffer = new char[0];
                } finally {
                    r.close();
                }
            } catch (Exception e) {
                /*
                 * couldn't load the chapter,
                 * it will be rendered as "empty chapter"
                 */
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