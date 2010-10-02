package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.util.text.TextTools;

public class RegionText extends Region {

    public int   position;
    public short length;

    public byte  color;
    public byte  style;

    public RegionText (
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final byte style,
            final byte color) {

        super(x, y, width, height);
        this.position = position;
        this.length = (short) length;
        this.style = style;
        this.color = color;
    }

    public void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        AlbiteFont font =
                StylingConstants.chooseFont(fontPlain, fontItalic, style);

        font.drawChars(g, cp.colors[color], chapterBuffer,
                x, y, position, length);
    }

    public String getText(final char[] chapterBuffer) {
        return TextTools.prepareForDict(chapterBuffer, position, length);
    }
}