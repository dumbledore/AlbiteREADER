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
public abstract class Page {
    protected Booklet booklet;

    public abstract Region getRegionAt(int x, int y);
    public abstract int getRegionIndexAt(int x, int y);
    public abstract boolean contains(int position);

    public final void draw(final Graphics g, final ColorScheme cp) {
        draw(g,
             cp,
             booklet.fontPlain,
             booklet.fontItalic,
             booklet.getTextBuffer());
    }

    public void drawSelected(
            final Graphics g, final ColorScheme cp,
            final int firstElement, int lastElement) {
        draw(g, cp);
    }

    protected abstract void draw(
            Graphics g,
            ColorScheme cp,
            AlbiteFont fontPlain,
            AlbiteFont fontItalic,
            char[] textBuffer);

    public int getStart() {
        return 0;
    }

    public int getEnd() {
        return 0;
    }

    public Region getRegionForIndex(final int index) {
        return null;
    }

    public String getTextForBookmark(final char[] chapterBuffer) {
        return "";
    }

    public String getTextForBookmark(
            final char[] chapterBuffer,
            final int firstIndex,
            final int lastIndex) {
        return "";
    }

    public boolean hasImage() {
        return false;
    }
}