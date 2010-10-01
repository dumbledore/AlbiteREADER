package org.albite.book.model;

import java.util.Vector;
import org.albite.util.archive.ArchivedFile;

public class Chapter {
    private final String                title;
    private final ArchivedFile          file;

    private Chapter                     prevChapter;
    private Chapter                     nextChapter;

    //Data
    private char[]                      textBuffer;
    private Vector                      images;

    private int currentPosition = 0;

    private final int number;

    public Chapter(final ArchivedFile af,
            final String title, final int number) {
        
        this.file = af;
        this.title = title;
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public Chapter getPrevChapter() {
        return prevChapter;
    }

    public void setPrevChapter(final Chapter bc) {
        prevChapter = bc;
    }

    public Chapter getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(final Chapter bc) {
        nextChapter = bc;
    }

    public final char[] getTextBuffer() {
        if (textBuffer == null) {
            try {
                textBuffer = file.getAsChars();
            } catch (Exception e) {
                e.printStackTrace();
                //something went wrong with the archive?!
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
}