/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.font;

/**
 *
 * @author Albus Dumbledore
 */
public class Glyph {
    protected final short x;
    protected final short y;
    protected final short width;
    protected final short height;
    protected final short xoffset;
    protected final short yoffset;
    protected final short xadvance;

    public Glyph(
            final short x, final short y,
            final short width, final short height,
            final short xoffset, final short yoffset,
            final short xadvance) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        this.xadvance = xadvance;
    }
}