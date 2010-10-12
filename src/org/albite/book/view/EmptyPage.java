/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import org.albite.book.view.region.Region;
import org.albite.book.view.Booklet;
import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;

/**
 *
 * @author albus
 */
public class EmptyPage extends Page {
    final public static byte    TYPE_CHAPTER_PREV   = 0;
    final public static byte    TYPE_CHAPTER_NEXT   = 1;
    final public static byte    TYPE_BOOK_START     = 2;
    final public static byte    TYPE_BOOK_END       = 3;
    final public static byte    TYPE_EMPTY_CHAPTER  = 4;
    final public static int     TYPE_COUNT          = 5;

    private byte type;

    final public static char[]  LABEL_CHAPTER_PREV
            = "Previous chapter".toCharArray();

    final public static char[]  LABEL_CHAPTER_NEXT
            = "Next chapter".toCharArray();

    final public static char[]  LABEL_BOOK_START
            = "Start of book".toCharArray();

    final public static char[]  LABEL_BOOK_END
            = "End of book".toCharArray();

    final public static char[]  LABEL_EMPTY_CHAPTER
            = "Empty chapter".toCharArray();

    public EmptyPage(final Booklet booklet, final byte pageType) {
        super(booklet);

        if (pageType < 0 || pageType >= TYPE_COUNT) {
            throw new IllegalArgumentException();
        }

        this.type = pageType;
    }

    public final Region getRegionAt(final int x, final int y) {
        return null;
    }

    public final boolean contains(int position) {
        return false;
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic) {

        final int colorDummy = cp.colors[ColorScheme.COLOR_TEXT_DUMMY];
        final int width = booklet.width;
        final int hcentered = booklet.height / 2 - 20;

        char[] label = LABEL_EMPTY_CHAPTER;

        switch (type) {

            case TYPE_CHAPTER_PREV:
                label = LABEL_CHAPTER_PREV;
                break;

            case TYPE_CHAPTER_NEXT:
                label = LABEL_CHAPTER_NEXT;
                break;

            case TYPE_BOOK_START:
                label = LABEL_BOOK_START;
                break;

            case TYPE_BOOK_END:
                label = LABEL_BOOK_END;
                break;

            case TYPE_EMPTY_CHAPTER:
                label = LABEL_EMPTY_CHAPTER;
                break;
        }

        int w = fontItalic.charsWidth(label);

        fontItalic.drawChars(g, colorDummy, label,
                (booklet.width - w) / 2, booklet.height / 2 - 20);
    }

    public final byte getType() {
        return type;
    }
}