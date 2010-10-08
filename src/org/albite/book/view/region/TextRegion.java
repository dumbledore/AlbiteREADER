package org.albite.book.view.region;

import org.albite.book.view.page.ContentPage;
import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.book.model.element.TextElement;
import org.albite.font.AlbiteFont;
import org.albite.lang.TextTools;

public class TextRegion extends Region {

    public TextElement  textel;
    public int          position;
    public short        length;

    public byte         color;
    public byte         style;

    public TextRegion (
            final TextElement textel,
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final byte style,
            final byte color) {

        super(textel, x, y, width, height);
        this.textel = textel;
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
                ContentPage.chooseFont(fontPlain, fontItalic, style);

        font.drawChars(g, cp.colors[color], chapterBuffer,
                x, y, position, length);
    }

    public String getText() {
        return TextTools.prepareForDict(textel.text, position, length);
    }
}