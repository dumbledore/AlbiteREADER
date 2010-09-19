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
    private int                         textBufferSize;
    private Vector                      images;

    private int currentPosition = 0;

    public Chapter(final ArchivedFile af, final String title) {
        this.file = af;
        this.title = title;
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
    
    public int getTextBufferSize() {
        getTextBuffer();
        return textBufferSize;
    }

    public final char[] getTextBuffer() {
        if (textBuffer == null) {
            textBuffer = new char[file.getSize()];
            try {
                textBufferSize = file.getFileContentsAsChars(textBuffer);
//                System.out.println("Loaded chapter: " + textBufferSize);
            } catch (Exception e) {
                e.printStackTrace();
                //something went wrong with the archive?!
            }
        }
        return textBuffer;
    }

    public final void unload() {
        textBuffer = null;
        textBufferSize = 0;
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
}