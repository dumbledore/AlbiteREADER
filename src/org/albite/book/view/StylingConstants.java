/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import org.albite.albite.ColorProfile;
import org.albite.font.BitmapFont;

/**
 *
 * @author albus
 */
public class StylingConstants {
    private StylingConstants() {};

    public static final byte LEFT     = 0;
    public static final byte RIGHT    = 1;
    public static final byte JUSTIFY  = 2;
    public static final byte CENTER   = 3;


    public static final byte PLAIN    = 0; //i.e. nothing is set
    public static final byte ITALIC   = 1;
    public static final byte BOLD     = 2;
    public static final byte HEADING  = 4;

    public static final int  LINE_SPACING = 2;

    public static BitmapFont chooseFont(BitmapFont fontPlain, BitmapFont fontItalic, byte style) {
        BitmapFont font = fontPlain;
        if ((style & ITALIC) == ITALIC)
            font = fontItalic;

        return font;
    }

    public static byte chooseTextColor(byte style) {
        byte color = ColorProfile.CANVAS_TEXT_COLOR;
            if ((style & ITALIC) == ITALIC)
                color = ColorProfile.CANVAS_TEXT_ITALIC_COLOR;
            if ((style & BOLD) == BOLD)
                color = ColorProfile.CANVAS_TEXT_BOLD_COLOR;
            if ((style & HEADING) == HEADING)
                color = ColorProfile.CANVAS_TEXT_HEADING_COLOR;

            return color;
    }
}

