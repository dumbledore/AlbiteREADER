package org.albite.book.view.region;

import org.albite.book.view.ContentPage;
import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.book.model.element.TextElement;
import org.albite.font.AlbiteFont;
import org.albite.lang.TextTools;

public class HyphenatedTextRegion extends TextRegion {

    public HyphenatedTextRegion prev;
    public HyphenatedTextRegion next;

    public HyphenatedTextRegion (
            final TextElement textel,
            final short x,
            final short y,
            final short width,
            final short height,
            final int position,
            final int length,
            final boolean plainFont,
            final byte color,
            final HyphenatedTextRegion prev) {

        super(textel, x, y, width, height, position, length, plainFont, color);
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
            final AlbiteFont fontItalic) {

        int color_ = cp.colors[color];
        AlbiteFont font = (plainFont ? fontPlain : fontItalic);
        font.drawChars(g, color_, textel.text, x, y, position, length);
        if (textel.text[position + length - 1] != '-' && next != null)
            font.drawChar(g, color_, '-', x + width - font.dashWidth, y);
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

    public final String getText() {
        HyphenatedTextRegion head = getHead();
        HyphenatedTextRegion tail = getTail();

        final int pos = head.position;
        final int len = tail.position - pos + tail.length;

        return TextTools.prepareForDict(textel.text, pos, len);
    }
}