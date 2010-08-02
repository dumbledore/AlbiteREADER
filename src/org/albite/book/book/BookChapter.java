package org.albite.book.book;

import java.util.Vector;
import org.albite.util.archive.ArchivedFile;

public class BookChapter {
    private String                title;
    private ArchivedFile          file;
    private BookChapter           prevChapter;
    private BookChapter           nextChapter;
    private short                 chapterNo;

    //Data
    private char[]                textBuffer;
    private int                   textBufferSize;
    private Vector                images;

    //position
    private int                   currentPosition = 0; //position in chapter; CANNOT be greter than chapter length, right?

    public BookChapter(ArchivedFile af, String title, int chapterNo) {
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

    public BookChapter getPrevChapter() {
        return prevChapter;
    }

    public void setPrevChapter(BookChapter bc) {
        prevChapter = bc;
    }

    public BookChapter getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(BookChapter bc) {
        nextChapter = bc;
    }

    public int getChapterNo() {
        return chapterNo;
    }
    
    public int getSize() {
        getTextBuffer();
        return textBufferSize;
    }

    public char[] getTextBuffer() {
        if (textBuffer == null) {
            textBuffer = new char[file.getSize()];
            try {
                textBufferSize = file.getFileContentsAsChars(textBuffer);
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

    public int getPosition() {
        return currentPosition;
    }

    public void setPosition(int position) {
        currentPosition = position;
    }
}