package org.albite.book.view;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorProfile;
import org.albite.font.AlbiteFont;
import org.albite.util.text.TextTools;

public class RegionTextHyphenated extends RegionText {

    public RegionTextHyphenated prev;
    public RegionTextHyphenated next;

    public RegionTextHyphenated (short x, short y, short width, short height, int position, int length, byte style, byte color, RegionTextHyphenated prev) {
        super(x, y, width, height, position, length, style, color);
        this.prev = prev;
    }

    public void buildLinks() {
        RegionTextHyphenated current = this;
        while (current.prev != null) {
            current.prev.next = current;
            current = current.prev;
        }
    }

    public void draw(Graphics g, ColorProfile cp, AlbiteFont fontPlain, AlbiteFont fontItalic, char[] chapterBuffer) {
        int color_ = cp.colors[color];
        AlbiteFont font = StylingConstants.chooseFont(fontPlain, fontItalic, style);
        font.drawChars(g, color_, chapterBuffer, x, y, position, length);
        if (chapterBuffer[position+length-1] != '-' && next != null) //does it need optimization?
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

    public String getText(final char[] chapterBuffer) {
        RegionTextHyphenated head = getHead();
        RegionTextHyphenated tail = getTail();

        final int pos = head.position;
        final int len = tail.position - pos + tail.length;

        return TextTools.prepareForDict(chapterBuffer, pos, len);
    }
}