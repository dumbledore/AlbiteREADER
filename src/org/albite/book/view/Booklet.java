/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import java.util.Vector;
import org.albite.book.StyleConstants;
import org.albite.book.model.book.Chapter;
import org.albite.font.AlbiteFont;
import org.albite.book.view.region.Breaks;
import org.albite.book.view.region.PageBuilder;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 * This class is non-mutable with the exception that the ColorProfile can be
 * changed if desired for no reflow is required when changing colours.
 * 
 * @author albus
 */
public class Booklet implements Breaks, StyleConstants {

//    final ArchiveZip            bookArchive;
    final Chapter               chapter;

    final int                   width;
    final int                   height;

    final AlbiteFont            fontPlain;
    final AlbiteFont            fontItalic;

    final ZLTextTeXHyphenator   hyphenator;

    final boolean               renderImages;

//    final int                   fontHeight;
//    final int                   fontIndent;

//    final byte                  defaultAlign = StyleConstants.JUSTIFY;

    private final Page[]        pages; //Page elements

    private Page                currentPage;
    private int                 currentPageIndex;
    private Page                prevPage;
    private Page                nextPage;

    /* this inverts the direction of pages */
    private final boolean       inverted;

    public Booklet(
            final int width,
            final int height,
            final boolean inverted,
            final Chapter chapter,
//            final ArchiveZip bookArchive,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final int lineSpacing,
            final boolean renderImages, 
            final ZLTextTeXHyphenator hyphenator
//            final TextParser parser
            ) {

        this.width = width;
        this.height = height;
        this.inverted = inverted;
        this.chapter = chapter;
//        this.bookArchive = bookArchive;
        this.fontPlain = fontPlain;
        this.fontItalic = fontItalic;
        this.hyphenator = hyphenator;
        this.renderImages = renderImages;

//        fontHeight = fontPlain.lineHeight + lineSpacing;
//        fontIndent = fontPlain.spaceWidth * 3;

        PageBuilder pageBuilder = new PageBuilder(
                width, height,
                chapter.getElements(),
                fontPlain, fontItalic,
                lineSpacing, renderImages,
                hyphenator,
                this
                );

        pages = pageBuilder.build(
                chapter.getPrevChapter() != null,
                chapter.getNextChapter() != null
                );

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
        if (inverted) {
            return incrementPage();
        } else {
            return decrementPage();
        }
    }

    private boolean decrementPage() {
        int index = currentPageIndex - 1;
        if (index < 0) {
            return false;
        }
        currentPageIndex = index;
        setPages();
        return true;
    }

    public final boolean goToNextPage() {
        if (inverted) {
            return decrementPage();
        } else {
            return incrementPage();
        }
    }

    private boolean incrementPage() {
        int index = currentPageIndex + 1;
        if (index == pages.length) {
            return false;
        }
        currentPageIndex = index;
        setPages();
        return true;
    }

    public final void goToFirstPage() {
        currentPageIndex = 1;
        setPages();
    }

    public final void goToLastPage() {
        currentPageIndex = pages.length - 2;
        setPages();
    }

    public final void goToPosition(final int position) {
        if (position <= 0) {
            goToFirstPage();
            return;
        }

//        if (position >= chapter.getTextBuffer().length) {
//            goToLastPage();
//            return;
//        }

//        Page foundPage;
//        final int pagesSize = pages.size();
//        for (int i = 0; i < pagesSize; i++) {
//            foundPage = (Page) pages.elementAt(i);
//            if (foundPage.contains(position)) {
//                goToIndex(i);
//                return;
//            }
//        }
        goToFirstPage();
    }

    private void goToIndex(final int index) {
        if (index < 0) {
            goToFirstPage();
            return;
        }

        if (index >= pages.length) {
            goToLastPage();
            return;
        }

        currentPageIndex = index;
        setPages();
    }

    private void setPages() {

        /* there are always at least three Pages in a booklet! */
        currentPage = (Page)(pages[currentPageIndex]);

        if (inverted) {
            prevPage = chooseNextPage();
            nextPage = choosePrevPage();
        } else {
            prevPage = choosePrevPage();
            nextPage = chooseNextPage();
        }

//        chapter.setCurrentPosition(currentPage.getStart());
    }

    private Page choosePrevPage() {
        int index = currentPageIndex -1;
        if (index < 0) {
            return null;
        } else {
            return (Page)(pages[index]);
        }
    }

    private Page chooseNextPage() {
        int index = currentPageIndex +1;
        if (index == pages.length) {
            return null;
        } else {
            return (Page)(pages[index]);
        }
    }

    public final int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public final int getPagesCount() {
        return pages.length;
    }

//    public final char[] getTextBuffer() {
//        return chapter.getTextBuffer();
//    }

    public final Chapter getChapter() {
        return chapter;
    }
}
