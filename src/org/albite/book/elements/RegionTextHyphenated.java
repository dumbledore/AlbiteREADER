package org.albite.book.elements;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorProfile;
import org.albite.font.BitmapFont;

public class RegionTextHyphenated extends RegionText {

    public RegionTextHyphenated   prev;
    public RegionTextHyphenated   next;

    public RegionTextHyphenated (short x, short y, short width, short height, char[]buffer, int position, int length, BitmapFont font, byte color, RegionTextHyphenated prev) {
        super(x, y, width, height, buffer, position, length, font, color);
        this.prev       = prev;
    }

    public void buildLinks() {
        RegionTextHyphenated current = this;
        while (current.prev != null) {
            current.prev.next = current;
            current = current.prev;
        }
    }

    public boolean containsPoint2DinChain(int x, int y) {
            //find the head
            RegionTextHyphenated current = this;
            while (current.prev != null)
                current = current.prev;

            //search all in chain
            while (current != null) {
                if (current.containsPoint2D(x, y))
                    return true;
                current = current.next;
            }
            return false;
    }
    
    public void draw(Graphics g, ColorProfile cp) {
        int color_ = cp.colors[color];
        font.drawChars(g, color_, buffer, x, y, position, length);
        if (buffer[position+length-1] != '-' && next != null) //does it need optimization?
            font.drawChar(g, color_, '-', x + width - font.dashWidth, y);
    }
    
}