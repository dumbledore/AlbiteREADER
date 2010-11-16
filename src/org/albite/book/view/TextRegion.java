package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.lang.TextTools;

public class TextRegion extends Region {

    protected final short length;

    protected final byte  color;
    protected final byte  style;

    public TextRegion (
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final byte style,
            final byte color) {

        super(x, y, width, height, position);
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
                TextPage.chooseFont(fontPlain, fontItalic, style);

        font.drawChars(g, cp.colors[color], chapterBuffer,
                x, y, position, length);
    }

    public void drawSelected(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {
        
        AlbiteFont font =
                TextPage.chooseFont(fontPlain, fontItalic, style);

        g.setColor(cp.colors[color]);
        g.fillRect(x, y, width, height);
        font.drawChars(g, cp.colors[ColorScheme.COLOR_BACKGROUND], chapterBuffer,
                x, y, position, length);
    }

    public final String getText(final char[] chapterBuffer) {
        return TextTools.prepareForDict(chapterBuffer, position, length);
    }

    public void addTextChunk(
            final char[] chapterBuffer,
            final StringBuffer buf) {
        buf.append(chapterBuffer, position, length);
        buf.append(' ');
    }
}