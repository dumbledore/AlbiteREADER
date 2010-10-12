package org.albite.book.model.book;

import javax.microedition.io.InputConnection;
import org.albite.book.model.element.Element;
import org.albite.book.processor.MarkupProcessor;

public class Chapter {

    private final Book              book;

    private final String            title;

    private final InputConnection   file;
    private final int               fileSize;

    private String                  encoding;
//            = AlbiteStreamReader.DEFAULT_ENCODING;

    private MarkupProcessor         processor;
    private Element[]               elements;

    private Chapter                 prevChapter;
    private Chapter                 nextChapter;

    private int                     currentElemend = 0;
    private int                     currentCharacter = 0;

    private final int               number;

    public Chapter(
            final Book book,
            final String title,
            final InputConnection file,
            final int fileSize,
            final String encoding,
            final MarkupProcessor processor,
            final int number) {

        this.book = book;
        this.title = title;
        this.file = file;
        this.fileSize = fileSize;
        this.encoding = encoding;
        this.processor = processor;
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

    public final Element[] getElements() {
        if (elements == null) {
            elements = processor.getElements(
                    book.getArchive(), file, fileSize, encoding);
        }

        return elements;
    }

    public final void unload() {
        elements = null;
    }

    public final int getCurrentPosition() {
//        return currentPosition;
        return 0;
        //TODO
    }

    public final void setCurrentPosition(final int pos) {
        //TODO
//        if (pos < 0) {
//            currentPosition = 0;
//        }
//
//        currentPosition = pos;
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
        elements = null;
    }

    public final void setEncodingWithoutRescan(final String encoding) {
        this.encoding = encoding;
    }
}