package org.albite.book.view.region;

import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.book.model.element.Element;
import org.albite.font.AlbiteFont;

public abstract class Region {
    final Element element;

    short x;
    short y;
    short width;
    short height;

    public Region(
            final Element element,
            final short x,
            final short y,
            final short width,
            final short height) {

        if (x < 0 || y < 0 || width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }

        this.element = element;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean containsPoint2D(final int px, final int py) {
        return 
                px >= x
                && py >= y
                && px < x + width
                && py < y + height;
    }

    public int hashCode() {
        return ((x & 0xFFFF) << 16) | (y & 0xFFFF);
    }

    public final boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof Region) {
            Region sr = (Region) o;
            return
                    this.x == sr.x
                    && this.y == sr.y
                    && this.width == sr.width
                    && this.height == sr.height;
        }
        return false;
    }

    public Element getElement() {
        return element;
    }

    public int getCharPosition() {
        return 0;
    }

    public abstract void draw(
            Graphics g,
            ColorScheme cp,
            AlbiteFont fontPlain,
            AlbiteFont fontItalic,
            char[] textBuffer);
}