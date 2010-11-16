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
 * @author Albus Dumbledore
 */
public class RulerRegion extends Region {
    private static final String TEXT_REPRESENTATION = "\n----------\n";
    
    byte                        color;

    public RulerRegion(
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final byte color) {

        super(x, y, width, height, position);
        this.color = color;
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        g.setColor(cp.colors[color]);
        int yy = y + (height / 2);
        g.drawLine(x, yy, width, yy);
    }

    public final void drawSelected(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {
        g.setColor(cp.colors[color]);
        g.fillRect(x, y, width, height);
        g.setColor(cp.colors[ColorScheme.COLOR_BACKGROUND]);
        int yy = y + (height / 2);
        g.drawLine(x, yy, width, yy);
    }

    public final String getText(char[] chapterBuffer) {
        return TEXT_REPRESENTATION;
    }

    public void addTextChunk(char[] chapterBuffer, StringBuffer buf) {
        buf.append(TEXT_REPRESENTATION);
    }
}
