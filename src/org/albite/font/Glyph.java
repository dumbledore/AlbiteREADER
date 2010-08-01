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
    final protected short x;
    final protected short y;
    final protected short width;
    final protected short height;
    final protected short xoffset;
    final protected short yoffset;
    final protected short xadvance;

    public Glyph(short x, short y, short width, short height, short xoffset, short yoffset, short xadvance) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        this.xadvance = xadvance;
    }
}
