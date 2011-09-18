/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.font;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Albus Dumbledore
 */
public abstract class AlbiteFont {
    public static final int         MAGIC_NUMBER = 1095516742;
    public static final String      FILE_EXTENSION = ".alf";
    protected static final String   INVALID_FILE_ERROR = "ALF file is corrupted.";
    
    protected final String          fontname;

    protected AlbiteFont(final String fontname) {
        this.fontname = fontname;
    }

    public final String getFontname() {
        return fontname;
    }

    public abstract int getLineHeight();

    public int getLineSpacing() {
        return 0;
    }

    public final int charsWidth(
            final char[] c, final int offset, final int length) {

        int res = 0;

        for (int i = offset; i < offset + length; i++) {
            res += charWidth(c[i]);
        }

        return res;
    }

    public abstract int charWidth(char c);

    public final int charsWidth(final char[] c) {
        return charsWidth(c, 0, c.length);
    }

    public abstract void drawChars(
            final Graphics g,
            final int color,
            final char[] buffer,
                  int x, final int y,
            final int offset,
            final int length);

    public final void drawChars(
            final Graphics g,
            final int color,
            final char[] buffer,
            final int x, final int y) {
        drawChars(g, color, buffer, x, y, 0, buffer.length);
    }

    public abstract void drawChar(
            final Graphics g,
            final int color,
            final char c,
            final int x, final int y);
}