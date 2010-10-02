package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.util.text.TextTools;

public class RegionTextHyphenated extends RegionText {

    public RegionTextHyphenated prev;
    public RegionTextHyphenated next;

    public RegionTextHyphenated (
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final byte style,
            final byte color,
            final RegionTextHyphenated prev) {

        super(x, y, width, height, position, length, style, color);
        this.prev = prev;
    }

    public final void buildLinks() {
        RegionTextHyphenated current = this;
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
                StylingConstants.chooseFont(fontPlain, fontItalic, style);
        font.drawChars(g, color_, chapterBuffer, x, y, position, length);
        if (chapterBuffer[position + length - 1] != '-' && next != null)
            font.drawChar(g, color_, '-', x + width - font.dashWidth, y);
    }

    private RegionTextHyphenated getHead() {
        RegionTextHyphenated current = this;

        while (current.prev != null) {
            current = current.prev;
        }

        return current;
    }

    private RegionTextHyphenated getTail() {
        RegionTextHyphenated current = this;
        while (current.next != null) {
            current = current.next;
        }

        return current;
    }

    public final String getText(final char[] chapterBuffer) {
        RegionTextHyphenated head = getHead();
        RegionTextHyphenated tail = getTail();

        final int pos = head.position;
        final int len = tail.position - pos + tail.length;

        return TextTools.prepareForDict(chapterBuffer, pos, len);
    }
}