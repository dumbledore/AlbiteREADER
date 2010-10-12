/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.book.view.region.Region;
import org.albite.font.AlbiteFont;

/**
 *
 * @author albus
 */
public abstract class Page {
    final Booklet booklet;

    public Page(final Booklet booklet) {
        this.booklet = booklet;
    }

    public Region getRegionAt(int x, int y) {
        return null;
    }

    public boolean contains(
            final int elementIndex, final int charPosition) {

        return false;
    }

    public final void draw(final Graphics g, final ColorScheme cp) {
        draw(g,
             cp,
             booklet.fontPlain,
             booklet.fontItalic);
    }

    protected abstract void draw(
            Graphics g,
            ColorScheme cp,
            AlbiteFont fontPlain,
            AlbiteFont fontItalic);
}