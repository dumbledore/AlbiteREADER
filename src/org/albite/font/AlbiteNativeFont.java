/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.font;

import java.util.Hashtable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author albus
 */
public class AlbiteNativeFont extends AlbiteFont {

    private final Font font;
    private final Hashtable metrics = new Hashtable(1024);

    public AlbiteNativeFont(final int style, final int size) {
        font = Font.getFont(Font.FACE_PROPORTIONAL, style, size);
    }

    public int getLineHeight() {
        return font.getHeight();
    }

    public int charWidth(char c) {
        final Integer cachedWidth = (Integer) metrics.get(new Character(c));
        if (cachedWidth != null) {
            return cachedWidth.intValue();
        }

        final int width = font.charWidth(c);
        metrics.put(new Character(c), new Integer(width));
        return width;
    }

    public void drawChars(Graphics g, int color, char[] buffer, int x, int y, int offset, int length) {
        g.setColor(color);
        final int end = offset + length;
        char c;
        for (int i = offset; i < end; i++) {
            c = buffer[i];
            g.drawChar(c, x, y, Graphics.TOP | Graphics.LEFT);
            x += charWidth(c);
        }
    }

    public void drawChar(Graphics g, int color, char c, int x, int y) {
        g.setColor(color);
        g.drawChar(c, x, y, Graphics.TOP | Graphics.LEFT);
    }
}
