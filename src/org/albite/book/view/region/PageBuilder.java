/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view.region;

import java.util.Vector;
import org.albite.albite.ColorScheme;
import org.albite.book.StyleConstants;
import org.albite.book.model.element.*;
import org.albite.book.view.Booklet;
import org.albite.book.view.ContentPage;
import org.albite.book.view.EmptyPage;
import org.albite.book.view.Page;
import org.albite.font.AlbiteFont;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class PageBuilder
        implements Breaks, StyleConstants {

    /*
     * Typically ~60-100 pages per chapter, so 200 is quite enough
     */
    private final Vector        pages = new Vector(200);

    final Element[]             elements;

    final int                   width;
    final int                   height;

    final AlbiteFont            fontPlain;
    final AlbiteFont            fontItalic;

    final int                   lineHeight;

    final ZLTextTeXHyphenator   hyphenator;

    final Booklet               booklet;

    /*
     * Variables of the current state
     */
    LineBuilder lineBuilder;
    AlbiteFont font;
    boolean fontIsPlain = true;
    boolean preformattedText = false;
    byte colorIndex = ColorScheme.COLOR_TEXT;
    byte align = JUSTIFY;
    Element element;
    byte elementType;

    private Vector currentPageRegions = new Vector(300);
    private int ypos = 0;
    private Vector oneElementLines = new Vector(10);

    public PageBuilder(
            final int width,
            final int height,
            final Element[] elements,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final int lineSpacing,
            final boolean renderImages,
            final ZLTextTeXHyphenator hyphenator,
            final Booklet booklet
            ) {

        this.width = width;
        this.height = height;
        this.elements = elements;
        this.fontPlain = fontPlain;
        this.fontItalic = fontItalic;
        this.lineHeight = fontPlain.lineHeight + lineSpacing;
        this.hyphenator = hyphenator;
        this.booklet = booklet;

        this.lineBuilder = new LineBuilder(width, hyphenator);
        this.font = fontPlain;
    }

    public Page[] build(final boolean prevChapter, final boolean nextChapter) {

        /*
         * First dummy page (transition to prev chapter or opening of book)
         */
            pages.addElement(
                    new EmptyPage(booklet,
                    (prevChapter
                    ? EmptyPage.TYPE_CHAPTER_PREV
                    : EmptyPage.TYPE_BOOK_START)
                    ));

        /*
         * Real pages
         */
        for (int i = 0; i < elements.length; i++) {
            element = elements[i];
            elementType = element.getType();

            switch (elementType) {
                case Element.BREAK:
                    BreakElement breakElement = (BreakElement) element;
                    lineBuilder.breakAfter =
                            (breakElement.lineBreak ? BREAK_LINE : BREAK_PAGE);
                    break;

                case Element.IMAGE:
                    /*
                     * TODO
                     */
                    break;

                case Element.PRE:
                    preformattedText = ((PreElement) element).preformatted;
                    break;

                case Element.RULER:
                    oneElementLines.removeAllElements();
                    oneElementLines.addElement(new RulerRegion(
                            (RulerElement) element,
                            (short) 0, (short) 0,
                            (short) width, (short) lineHeight,
                            ColorScheme.COLOR_TEXT));
                    addLineToPage(oneElementLines, BREAK_LINE, lineHeight);
                    break;

                case Element.STYLE:
                    /*
                     * Setup style
                     */
                    StyleElement style = (StyleElement) element;

                    align = style.align;

                    if (style.italic == ENABLE) {
                        colorIndex = ColorScheme.COLOR_TEXT_ITALIC;
                        font = fontItalic;
                    } else if (style.italic == DISABLE) {
                        colorIndex = ColorScheme.COLOR_TEXT;
                        font = fontPlain;
                    }

                    if (style.bold == ENABLE) {
                        colorIndex = ColorScheme.COLOR_TEXT_BOLD;
                    } else if (style.bold == DISABLE) {
                        colorIndex = ColorScheme.COLOR_TEXT;
                        //TODO
                    }

                    if (style.heading == ENABLE) {
                        colorIndex = ColorScheme.COLOR_TEXT_HEADING;
                    } else if (style.heading == DISABLE) {
                        colorIndex = ColorScheme.COLOR_TEXT;
                        //TODO
                    }

                    break;

                case Element.TEXT:
                    System.out.println("Text element: "
                            + " align: " + align
                            + " pre: " + preformattedText);
                    lineBuilder.set(font, fontIsPlain, align, colorIndex,
                            (TextElement) element, preformattedText);

                    /*
                     * Parse all text
                     */
                    while (lineBuilder.next()) {
                        lineBuilder.positionWordsOnLine();
                        addLineToPage(
                                lineBuilder.getLine(),
                                lineBuilder.breakAfter, lineHeight);
                    }

                    break;
            }
        }

        if (pages.size() == 1) {
            /*
             * No TextPages have been added
             */

            pages.addElement(
                    new EmptyPage(booklet, EmptyPage.TYPE_EMPTY_CHAPTER));
        }

        /*
         * Last dummy page (transition to next chapter or end of book)
         */
        pages.addElement(
                new EmptyPage(booklet,
                (nextChapter
                ? EmptyPage.TYPE_CHAPTER_NEXT
                : EmptyPage.TYPE_BOOK_END)
                ));

        Page[] result = new Page[pages.size()];
        pages.copyInto(result);
        return result;
    }

    private void addLineToPage(
            final Vector words, final int breakAfter, final int lineHeight) {
        int nextypos = ypos + lineHeight;

        /*
         * Does the line fit on the page?
         */
        if (nextypos >= height || breakAfter == BREAK_PAGE) {
            /*
             * start a new page, but before that finish with the current one
             */

            /*
             * Was the last page empty?
             */
            if (!currentPageRegions.isEmpty()) {
                /*
                 * Not empty, then add it
                 */
                pages.addElement(new ContentPage(booklet, currentPageRegions));
            }

            /*
             * reset page values
             */
            currentPageRegions.removeAllElements();
            ypos = 0;
            nextypos = lineHeight;
        }

        for (int i = 0; i < words.size(); i++) {
            Region r = (Region) words.elementAt(i);

            /*
             * Set y
             */
            r.y = (short) ypos;

            /*
             * Add the region to the page
             */
            currentPageRegions.addElement(r);
        }

        /*
         * Increment ypos
         */
        ypos = nextypos;
    }
}
