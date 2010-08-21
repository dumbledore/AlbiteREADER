/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorProfile;
import org.albite.font.BitmapFont;

/**
 *
 * @author Albus Dumbledore
 */
public class RegionLineSeparator extends Region {
    final public static byte TYPE_RULER      = 1;
    final public static byte TYPE_SEPARATOR  = 2;
    
    byte color;
    byte type;

    public RegionLineSeparator(short x, short y, short width, short height, byte type, byte color) {
        super(x, y, width, height);
        this.type = type;
        this.color = color;
    }

    public void draw(Graphics g, ColorProfile cp, BitmapFont fontPlain, BitmapFont fontItalic, char[] chapterBuffer) {
        switch(type) {
            case TYPE_RULER:
                {
                    g.setColor(cp.colors[color]);
                    int yy = y + (height/2);
                    g.drawLine(0, yy, width, yy);
                }
                break;

            case TYPE_SEPARATOR:
                {
                    g.setColor(cp.colors[color]);
                    int yy = y + (height/2);
                    int xx = width/4;
                    g.drawLine(xx, yy, width-xx, yy);
                }
                break;
        }
    }
}
