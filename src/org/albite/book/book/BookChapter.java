package org.albite.book.book;

import java.util.Vector;
import org.albite.util.archive.ArchivedFile;

public class BookChapter {
    protected String                title;
    protected ArchivedFile          file;
    protected BookChapter           prevChapter;
    protected BookChapter           nextChapter;
    int chapterNo;

    //Data
    protected char[]                textBuffer;
    protected int                   textBufferSize;
    Vector                          images;
    Vector                          stylings;

    //position
    public int                      currentPosition = 0; //position in chapter; CANNOT be greter than chapter length, right?

    public BookChapter(ArchivedFile af, String title, int chapterNo) {
        this.file = af;
        this.title = title;
        this.chapterNo = chapterNo;
    }

    protected void close() {
        file = null;
        prevChapter = null;
        nextChapter = null;
        
        stylings = null;
        images.removeAllElements();
        images = null;
    }
    
    public String getTitle() {
        return title;
    }

    public void setPrevChapter(BookChapter bc) {
        prevChapter = bc;
    }

    public void setNextChapter(BookChapter bc) {
        nextChapter = bc;
    }

    public BookChapter getPrevChapter() {
        return prevChapter;
    }

    public BookChapter getNextChapter() {
        return nextChapter;
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

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getPosition() {
        return currentPosition;
    }

    public void setPosition(int position) {
        currentPosition = position;
    }
}