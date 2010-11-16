package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.lang.TextTools;

public class HyphenatedTextRegion extends TextRegion {

    public HyphenatedTextRegion prev;
    public HyphenatedTextRegion next;

    public HyphenatedTextRegion (
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final byte style,
            final byte color,
            final HyphenatedTextRegion prev) {

        super(x, y, width, height, position, length, style, color);
        this.prev = prev;
    }

    public final void buildLinks() {
        HyphenatedTextRegion current = this;
        while (current.prev != null) {
            current.prev.next = current;
            current = current.prev;
        }
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        int color_ = cp.colors[color];
        AlbiteFont font =
                TextPage.chooseFont(fontPlain, fontItalic, style);
        font.drawChars(g, color_, chapterBuffer, x, y, position, length);
        if (chapterBuffer[position + length - 1] != '-' && next != null)
            font.drawChar(g, color_, '-', x + width - font.dashWidth, y);
    }

    public void drawSelected(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        int colorBG = cp.colors[color];
        int colorText = cp.colors[ColorScheme.COLOR_BACKGROUND];
        AlbiteFont font =
                TextPage.chooseFont(fontPlain, fontItalic, style);
        g.setColor(colorBG);
        g.fillRect(x, y, width, height);
        font.drawChars(g, colorText,
                chapterBuffer, x, y, position, length);
        if (chapterBuffer[position + length - 1] != '-' && next != null)
            font.drawChar(g, colorText, '-', x + width - font.dashWidth, y);
    }


    private HyphenatedTextRegion getHead() {
        HyphenatedTextRegion current = this;

        while (current.prev != null) {
            current = current.prev;
        }

        return current;
    }

    private HyphenatedTextRegion getTail() {
        HyphenatedTextRegion current = this;
        while (current.next != null) {
            current = current.next;
        }

        return current;
    }

    public final String getText(final char[] chapterBuffer) {
        HyphenatedTextRegion head = getHead();
        HyphenatedTextRegion tail = getTail();

        final int pos = head.position;
        final int len = tail.position - pos + tail.length;

        return TextTools.prepareForDict(chapterBuffer, pos, len);
    }
}