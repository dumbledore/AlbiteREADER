/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import java.util.Vector;
import org.albite.book.model.Chapter;
import org.albite.font.AlbiteFont;
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

    final AlbiteFont fontPlain;
    final AlbiteFont fontItalic;

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

    /* this inverts the direction of pages */
    private final boolean inverted;

    public Booklet(int width, int height, boolean inverted, AlbiteFont fontPlain, AlbiteFont fontItalic, ZLTextTeXHyphenator hyphenator, Archive bookFile, Chapter chapter) {
        this.width = width;
        this.height = height;
        this.inverted = inverted;
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

    public synchronized final Page getCurrentPage() {
        return currentPage;
    }

    public synchronized final Page getNextPage() {
        return nextPage;
    }

    public synchronized final Page getPrevPage() {
        return prevPage;
    }

    public synchronized final boolean goToPrevPage() {
        if (inverted) {
            return incrementPage();
        } else {
            return decrementPage();
        }
    }

    private boolean decrementPage() {
        int index = currentPageIndex -1;
        if (index < 0)
            return false;
        currentPageIndex = index;
        setPages();
        return true;
    }

    public synchronized final boolean goToNextPage() {
        if (inverted) {
            return decrementPage();
        } else {
            return incrementPage();
        }
    }

    private boolean incrementPage() {
        int index = currentPageIndex +1;
        if (index == pages.size())
            return false;
        currentPageIndex = index;
        setPages();
        return true;
    }

    public synchronized final void goToFirstPage() {
        currentPageIndex = 1;
        setPages();
    }

    public synchronized final void goToLastPage() {
        currentPageIndex = pages.size() - 2;
        setPages();
    }

    public synchronized final void goToPosition(int position) {
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

        /* there are always at least three Pages in a booklet! */
        currentPage = (Page)(pages.elementAt(currentPageIndex));

        if (inverted) {
            prevPage = chooseNextPage();
            nextPage = choosePrevPage();
        } else {
            prevPage = choosePrevPage();
            nextPage = chooseNextPage();
        }
    }

    private Page choosePrevPage() {
        int index = currentPageIndex -1;
        if (index < 0) {
            return null;
        } else {
            return (Page)(pages.elementAt(index));
        }
    }

    private Page chooseNextPage() {
        int index = currentPageIndex +1;
        if (index == pages.size()) {
            return null;
        } else {
            return (Page)(pages.elementAt(index));
        }
    }

    public synchronized final int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public synchronized final int getPagesCount() {
        return pages.size();
    }
}
