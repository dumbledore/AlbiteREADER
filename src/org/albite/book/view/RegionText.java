package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorProfile;
import org.albite.font.AlbiteFont;
import org.albite.util.text.TextTools;

public class RegionText extends Region {

    public int   position;
    public short length;

    public byte  color;
    public byte  style;

    public RegionText (short x, short y, short width, short height, int position, int length, byte style, byte color) {
        super(x, y, width, height);
        this.position = position;
        this.length = (short)length;
        this.style = style;
        this.color = color;
    }
       
    public void draw(Graphics g, ColorProfile cp, AlbiteFont fontPlain, AlbiteFont fontItalic, char[] chapterBuffer) {
        AlbiteFont font = StylingConstants.chooseFont(fontPlain, fontItalic, style);
        font.drawChars(g, cp.colors[color], chapterBuffer, x, y, position, length);
    }

    public String getText(char[] chapterBuffer) {
        return TextTools.prepareForDict(chapterBuffer, position, length);
//        return new String(chapterBuffer, position, length);
    }
    
}