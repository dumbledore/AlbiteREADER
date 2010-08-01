package org.albite.book.elements;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorProfile;
import org.albite.font.BitmapFont;

public class RegionText extends Region {

    public char[] buffer; //used by all

    public int          position;
    public int          length;

    public byte         color;
    public BitmapFont   font;

    public RegionText (short x, short y, short width, short height, char[] buffer, int position, int length, BitmapFont font, byte color) {
        super(x, y, width, height);
        this.buffer = buffer;
        this.position = position;
        this.length = length;
        this.font = font;
        this.color = color;
    }
       
    public void draw(Graphics g, ColorProfile cp) {
        font.drawChars(g, cp.colors[color], buffer, x, y, position, length);
    }
    
}