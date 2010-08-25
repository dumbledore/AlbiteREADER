package org.albite.book.model;

import java.util.Vector;
import org.albite.util.archive.ArchivedFile;

public class Chapter {
    private String                title;
    private ArchivedFile          file;
    private Chapter           prevChapter;
    private Chapter           nextChapter;
    private short                 chapterNo;

    //Data
    private char[]                textBuffer;
    private int                   textBufferSize;
    private Vector                images;

    public Chapter(ArchivedFile af, String title, int chapterNo) {
        this.file = af;
        this.title = title;
        this.chapterNo = (short)chapterNo;
    }

    protected void close() {
        file = null;
        prevChapter = null;
        nextChapter = null;
        
        images.removeAllElements();
        images = null;
    }
    
    public String getTitle() {
        return title;
    }

    public Chapter getPrevChapter() {
        return prevChapter;
    }

    public void setPrevChapter(Chapter bc) {
        prevChapter = bc;
    }

    public Chapter getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(Chapter bc) {
        nextChapter = bc;
    }

    public int getChapterNo() {
        return chapterNo;
    }
    
    public int getTextBufferSize() {
        getTextBuffer();
        return textBufferSize;
    }

    public char[] getTextBuffer() {
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

    public void unload() {
        textBuffer = null;
        textBufferSize = 0;
    }
}