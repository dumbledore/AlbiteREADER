package org.albite.book.model;

import java.util.Vector;
import javax.microedition.io.InputConnection;
import org.albite.util.archive.ArchivedFile;
import org.albite.util.text.decoder.AlbiteCharacterDecoder;
import org.albite.util.text.decoder.DecoderException;

public class Chapter {

    private static final char[]     TEXT_BUFFER_ERROR
            = "Couldn't load chapter.".toCharArray();

    private String                  encoding =
            AlbiteCharacterDecoder.DEFAULT_ENCODING; /* TODO: overwrite it! */


    private final String            title;

    /*
     * Chapter's file & its size
     */
    private final InputConnection   file;
    private final int               fileSize;

    private Chapter                 prevChapter;
    private Chapter                 nextChapter;

    private char[]                  textBuffer;
//    private Vector                  images;

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
                textBuffer = ArchivedFile.getAsChars(
                        file, fileSize, encoding);
//                        file, fileSize, encoding);
            } catch (DecoderException e) {
                if (e.getBuffer() != null) {
                    textBuffer = e.getBuffer();
                    /*
                     * TODO: Inform the user that the encoding
                     * is not the right one.
                     */
                } else {
                    textBuffer = TEXT_BUFFER_ERROR;
                }
            } catch (Exception e) {
                /*
                 * couldn't load the chapter
                 */
                textBuffer = TEXT_BUFFER_ERROR;
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