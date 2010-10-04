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
public class LineSeparatorRegion extends Region {
    final public static byte    TYPE_RULER      = 1;
    final public static byte    TYPE_SEPARATOR  = 2;
    
    byte                        color;
    byte                        type;

    public LineSeparatorRegion(
            final short x,
            final short y,
            final short width,
            final short height,
            final byte type,
            final byte color) {

        super(x, y, width, height);
        this.type = type;
        this.color = color;
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        switch (type) {
            case TYPE_RULER:
                {
                    g.setColor(cp.colors[color]);
                    int yy = y + (height / 2);
                    g.drawLine(x, yy, width, yy);
                }
                break;

            case TYPE_SEPARATOR:
                {
                    g.setColor(cp.colors[color]);
                    int yy = y + (height / 2);
                    int xx = width / 4;
                    g.drawLine(xx, yy, width - xx, yy);
                }
                break;
        }
    }
}
