/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;

/**
 *
 * @author albus
 */
public class PageDummy extends Page {
    final public static byte TYPE_CHAPTER_PREV   = 0;
    final public static byte TYPE_CHAPTER_NEXT   = 1;
    final public static byte TYPE_BOOK_START     = 2;
    final public static byte TYPE_BOOK_END       = 3;
    final public static int  TYPE_COUNT = 4;

    private byte type;

    final public static char[] LABEL_CHAPTER_PREV   = "Previous chapter".toCharArray();
    final public static char[] LABEL_CHAPTER_NEXT   = "Next chapter".toCharArray();
    final public static char[] LABEL_BOOK_START     = "Start of book".toCharArray();
    final public static char[] LABEL_BOOK_END       = "End of book".toCharArray();

    public PageDummy(Booklet booklet, byte pageType) {
        if (pageType < 0 || pageType >= TYPE_COUNT)
            throw new IllegalArgumentException();
        
        this.type = pageType;
        this.booklet = booklet;
    }

    public Region getRegionAt(int x, int y) {
        return null;
    }

    public boolean contains(int position) {
        return false;
    }

    public void draw(Graphics g, ColorScheme cp, AlbiteFont fontPlain, AlbiteFont fontItalic, char[] chapterBuffer) {
        final int colorDummy = cp.colors[cp.COLOR_TEXT_DUMMY];
        final int width = booklet.width;
        final int height = booklet.height;
        
        switch(type) {

            case TYPE_CHAPTER_PREV:
                {
                    int w = fontItalic.charsWidth(LABEL_CHAPTER_PREV);
                    fontItalic.drawChars(g, colorDummy, LABEL_CHAPTER_PREV, (width-w)/2, height/2-20);
                }
                break;

            case TYPE_CHAPTER_NEXT:
                {
                    int w = fontItalic.charsWidth(LABEL_CHAPTER_NEXT);
                    fontItalic.drawChars(g, colorDummy, LABEL_CHAPTER_NEXT, (width-w)/2, height/2-20);
                }
                break;

            case TYPE_BOOK_START:
                {
                    int w = fontItalic.charsWidth(LABEL_BOOK_START);
                    fontItalic.drawChars(g, colorDummy, LABEL_BOOK_START, (width-w)/2, height/2-20);
                }
                break;

            case TYPE_BOOK_END:
                {
                    int w = fontItalic.charsWidth(LABEL_BOOK_END);
                    fontItalic.drawChars(g, colorDummy, LABEL_BOOK_END, (width-w)/2, height/2-20);
                }
                break;
        }
    }

    public final byte getType() {
        return type;
    }
}