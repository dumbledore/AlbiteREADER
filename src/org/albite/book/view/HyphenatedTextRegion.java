package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;

public class HyphenatedTextRegion extends TextRegion {

    protected final int chunkPosition;
    protected final int chunkLength;

    public HyphenatedTextRegion (
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final byte style,
            final byte color,
            final int chunkPosition,
            final int chunkLength) {

        super(x, y, width, height, position, length, style, color);
        this.chunkPosition = chunkPosition;
        this.chunkLength = chunkLength;
    }

    public HyphenatedTextRegion(
            final short x,
            final short y,
            final short width,
            final short height,
            final HyphenatedTextRegion lastRegion,
            final int chunkPosition,
            final int chunkLength) {

        this(x, y, width, height,
                lastRegion.position,
                lastRegion.length,
                lastRegion.style,
                lastRegion.color,
                chunkPosition,
                chunkLength);
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        draw(
                g,
                TextPage.chooseFont(fontPlain, fontItalic, style),
                chapterBuffer,
                0,
                cp.colors[color],
                false);
    }

    public void drawSelected(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        draw(
                g,
                TextPage.chooseFont(fontPlain, fontItalic, style),
                chapterBuffer,
                cp.colors[color],
                cp.colors[ColorScheme.COLOR_BACKGROUND],
                true);
    }

    private void draw(
            final Graphics g,
            final AlbiteFont font,
            final char[] chapterBuffer,
            final int backgroundColor,
            final int textColor,
            final boolean drawBackground) {

        if (drawBackground) {
            g.setColor(backgroundColor);
            g.fillRect(x, y, width, height);
        }

        font.drawChars(g, textColor,
                chapterBuffer, x, y, chunkPosition, chunkLength);

        if (chapterBuffer[chunkPosition + chunkLength - 1] != '-'
                && (chunkPosition + chunkLength != position + length))
            font.drawChar(g, textColor, '-', x + width - font.dashWidth, y);
    }

    public void addTextChunk(
            final char[] chapterBuffer,
            final StringBuffer buf) {

        buf.append(chapterBuffer, chunkPosition, chunkLength);
        
        if (position + length == chunkPosition + chunkLength) {
            buf.append(' ');
        }
    }
}