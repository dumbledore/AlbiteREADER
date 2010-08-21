/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import java.util.Vector;
import org.albite.book.model.Chapter;
import org.albite.font.BitmapFont;
import org.albite.util.archive.Archive;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 * This class is non-mutable with the exception that the ColorProfile can be
 * changed if desired for no reflow is required when changing colours.
 * 
 * @author albus
 */
public class Booklet {
    final int width;
    final int height;

    final BitmapFont fontPlain;
    final BitmapFont fontItalic;

    final ZLTextTeXHyphenator hyphenator;
    final Archive bookFile;
    
    final char[] textBuffer;
    final int textBufferSize;

    final int fontHeight;
    final int fontIndent;

    final byte defaultAlign = StylingConstants.JUSTIFY;

    private final Vector pages; //Page elements

    private Page currentPage;
    private int  currentPageIndex;
    private Page prevPage;
    private Page nextPage;

    public Booklet(int width, int height, BitmapFont fontPlain, BitmapFont fontItalic, ZLTextTeXHyphenator hyphenator, Archive bookFile, Chapter chapter) {
        this.width = width;
        this.height = height;
        this.fontPlain = fontPlain;
        this.fontItalic = fontItalic;
        this.hyphenator = hyphenator;
        this.bookFile = bookFile;
        this.textBuffer = chapter.getTextBuffer();
        this.textBufferSize = chapter.getTextBufferSize();

        fontHeight = fontPlain.lineHeight + StylingConstants.LINE_SPACING;
        fontIndent = fontPlain.spaceWidth * 3;


        pages = new Vector(200); //typically ~60-100 pages per chapter
        //here comes text pagination

        //setup infopage's buffer
        //TODO: some of these options might be available to set by the user
        InfoPage ip = new InfoPage(StylingConstants.JUSTIFY);

        //First dummy page (transition to prev chapter or opening of book)
        if (chapter.getPrevChapter() == null) {
            pages.addElement(new PageDummy(this, PageDummy.TYPE_BOOK_START));
        } else {
            pages.addElement(new PageDummy(this, PageDummy.TYPE_CHAPTER_PREV));
        }

        //Real pages

        PageText current;

        for (int i=0; i<textBufferSize;) { //there will be at least one 'real' (i.e. not dummy) page

            current = new PageText(this, ip);

            if (!current.isEmpty()) {
                //page with content to render

                int pageType = current.getType();
                switch (pageType) {
                    case PageText.TYPE_TEXT:
                        i = current.getEnd();
                        break;
                    case PageText.TYPE_IMAGE:
                        break;
                }

                pages.addElement(current);
            } else {
                i = current.getEnd();
            }
        }

        //Last dummy page (transition to next chapter or end of book)
        if (chapter.getNextChapter() == null) {
            pages.addElement(new PageDummy(this, PageDummy.TYPE_BOOK_END));
        } else {
            pages.addElement(new PageDummy(this, PageDummy.TYPE_CHAPTER_NEXT));
        }

        goToFirstPage();
    }

    public final Page getCurrentPage() {
        return currentPage;
    }

    public final Page getNextPage() {
        return nextPage;
    }

    public final Page getPrevPage() {
        return prevPage;
    }

    public final boolean goToPrevPage() {
        int index = currentPageIndex -1;
        if (index < 0)
            return false;
        currentPageIndex = index;
        setPages();
        return true;
    }

    public final boolean goToNextPage() {
        int index = currentPageIndex +1;
        if (index == pages.size())
            return false;
        currentPageIndex = index;
        setPages();
        return true;
    }

    public final void goToFirstPage() {
        currentPageIndex = 1;
        setPages();
    }

    public final void goToLastPage() {
        System.out.println("Going to last page: " + (pages.size() -2) + "/" + (pages.size() -1));
        currentPageIndex = pages.size() - 2;
        setPages();
    }

    public final void goToPosition(int position) {
        if (position <= 0) {
            goToFirstPage();
            return;
        }

        if (position >= textBufferSize) {
            goToLastPage();
            return;
        }

        Page foundPage;
        final int pagesSize = pages.size();
        for (int i=0; i<pagesSize; i++) {
            foundPage = (Page)pages.elementAt(i);
            if (foundPage.contains(position)) {
                goToIndex(i);
                return;
            }
        }
        goToFirstPage();
    }

    private void goToIndex(int index) {
        if (index < 0) {
            goToFirstPage();
            return;
        }

        if (index >= pages.size()) {
            goToLastPage();
            return;
        }

        currentPageIndex = index;
        setPages();
    }

    private void setPages() {
        int index = currentPageIndex -1;
        if (index < 0) {
            prevPage = null;
        } else {
            prevPage = (Page)(pages.elementAt(index));
        }

        /* there are always at least three Pages in a booklet! */
        currentPage = (Page)(pages.elementAt(currentPageIndex));

        index = currentPageIndex +1;
        if (index == pages.size()) {
            nextPage = null;
        } else {
            nextPage = (Page)(pages.elementAt(index));
        }

        System.out.println("Current page is " + currentPageIndex);
    }
}
