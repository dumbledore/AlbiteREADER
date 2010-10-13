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
    public abstract boolean contains(int position);

    public final void draw(final Graphics g, final ColorScheme cp) {
        draw(g,
             cp,
             booklet.fontPlain,
             booklet.fontItalic,
             booklet.getTextBuffer());
    }

    protected abstract void draw(
            Graphics g,
            ColorScheme cp,
            AlbiteFont fontPlain,
            AlbiteFont fontItalic,
            char[] textBuffer);

    public int getStart() {
        if (this instanceof TextPage) {
            return ((TextPage) this).getStart();
         } else {
            return 0;
        }
    }

    public int getEnd() {
        if (this instanceof TextPage) {
            return ((TextPage) this).getEnd();
         } else {
            return 0;
        }
    }
}