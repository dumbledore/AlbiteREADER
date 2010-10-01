/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;

/**
 *
 * @author albus
 */
public final class StylingConstants {
    private StylingConstants() {};

    public static final byte LEFT     = 0;
    public static final byte RIGHT    = 1;
    public static final byte JUSTIFY  = 2;
    public static final byte CENTER   = 3;


    public static final byte PLAIN    = 0; //i.e. nothing is set
    public static final byte ITALIC   = 1;
    public static final byte BOLD     = 2;
    public static final byte HEADING  = 4;

    public static AlbiteFont chooseFont(AlbiteFont fontPlain, AlbiteFont fontItalic, byte style) {
        AlbiteFont font = fontPlain;
        if ((style & ITALIC) == ITALIC)
            font = fontItalic;

        return font;
    }

    public static byte chooseTextColor(byte style) {
        byte color = ColorScheme.COLOR_TEXT;
            if ((style & ITALIC) == ITALIC)
                color = ColorScheme.COLOR_TEXT_ITALIC;
            if ((style & BOLD) == BOLD)
                color = ColorScheme.COLOR_TEXT_BOLD;
            if ((style & HEADING) == HEADING)
                color = ColorScheme.COLOR_TEXT_HEADING;

            return color;
    }
}

