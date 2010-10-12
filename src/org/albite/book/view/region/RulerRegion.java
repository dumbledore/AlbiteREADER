/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view.region;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.book.model.element.RulerElement;
import org.albite.font.AlbiteFont;

/**
 *
 * @author Albus Dumbledore
 */
public class RulerRegion extends Region {
    byte color;

    public RulerRegion(
            final RulerElement ruler,
            final short x,
            final short y,
            final short width,
            final short height,
            final byte color) {

        super(ruler, x, y, width, height);
        this.color = color;
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic) {

        g.setColor(cp.colors[color]);
        int yy = y + (height / 2);
        g.drawLine(x, yy, width, yy);
    }
}
