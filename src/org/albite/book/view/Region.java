package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorProfile;
import org.albite.font.BitmapFont;

public abstract class Region {
    short x;
    short y;
    short width;
    short height;

    public Region(short x, short y, short width, short height) {

        if (x < 0 || y < 0 || width < 0 || height < 0)
            throw new IllegalArgumentException();
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean containsPoint2D(int px, int py) {
        return 
                px >= x &&
                py >= y &&
                px < x+width &&
                py < y+height
                ;
    }

    public int hashCode() {
        return ((x & 0xFFFF) << 16) | (y & 0xFFFF);
    }
    
    public boolean equals(Object o) {
        if (o instanceof Region) {
            Region sr = (Region)o;
            return this.x == sr.x && this.y == sr.y && this.width == sr.width && this.height == sr.height;
        }
        return false;
    }

    public abstract void draw(Graphics g, ColorProfile cp, BitmapFont fontPlain, BitmapFont fontItalic, char[] textBuffer);
}