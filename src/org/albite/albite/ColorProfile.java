/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

/**
 *
 * @author Albus Dumbledore
 */
public class ColorProfile {

    public static final byte CANVAS_BACKGROUND_COLOR            = 0;
    public static final byte CANVAS_TEXT_COLOR                  = 1;
    public static final byte CANVAS_TEXT_BOLD_COLOR             = 2;
    public static final byte CANVAS_TEXT_ITALIC_COLOR           = 3;
    public static final byte CANVAS_TEXT_HEADING_COLOR          = 4;
    public static final byte CANVAS_DUMMY_COLOR                 = 5;
    public static final byte MENU_BUTTONS_COLOR                 = 6;
    public static final byte MENU_BUTTONS_PRESSED_COLOR         = 7;
    public static final byte CURSOR_WAIT_COLOR                  = 8;
    public static final byte STATUS_BAR_TEXT_COLOR              = 9;
    public final int[] colors                                   = new int[10];

//    final public static ColorProfile CP_NIGHT = new ColorProfile("night", 0x111111,
//            0x332000, 0x664000, 0x443000, 0xAA6600,
//            0x442200, 0x332000, 0x884400, 0x332000, 0x332000);
//
    public static final ColorProfile[] availableProfiles = {
        new ColorProfile("day", 0xFFFFFF,
            0x6F654C, 0x2F291A, 0x4F493A, 0x2F291A,
//            0x000000, 0x2F291A, 0x4F493A, 0x2F291A,
            0xAF9F78, 0xAF9F78, 0xFF9F78, 0xAF9F78, 0xAF9F78),

        new ColorProfile("night", 0x000000,
            0x4c351d, 0x826630, 0x705525, 0x997332,
            0x664e24, 0x3C250d, 0x884400, 0x664e24, 0x3C250d)
    };
    
    public static final ColorProfile DEFAULT_DAY    = availableProfiles[0]; //day
    public static final ColorProfile DEFAULT_NIGHT  = availableProfiles[1]; //night

    final public String name;
    public ColorProfile other; //

    public ColorProfile(String name, int canvasBackgroundColor,
            int canvasTextColor, int canvasTextBoldColor,
            int canvasTextItalicColor, int canvasTextHeadingColor,
            int canvasTextDummyColor,
            int menuButtonsColor, int menuButtonsPressedColor,
            int cursorWaitColor, int statusBarTextColor) {

        this.name = name;
        colors[CANVAS_BACKGROUND_COLOR]     = canvasBackgroundColor;
        colors[CANVAS_TEXT_COLOR]           = canvasTextColor;
        colors[CANVAS_TEXT_BOLD_COLOR]      = canvasTextBoldColor;
        colors[CANVAS_TEXT_ITALIC_COLOR]    = canvasTextItalicColor;
        colors[CANVAS_TEXT_HEADING_COLOR]   = canvasTextHeadingColor;
        colors[CANVAS_DUMMY_COLOR]          = canvasTextDummyColor;
        colors[MENU_BUTTONS_COLOR]          = menuButtonsColor;
        colors[MENU_BUTTONS_PRESSED_COLOR]  = menuButtonsPressedColor;
        colors[CURSOR_WAIT_COLOR]           = cursorWaitColor;
        colors[STATUS_BAR_TEXT_COLOR]       = statusBarTextColor;
    }

    public final int getColor(byte colorIndex) {
        return colors[colorIndex];
    }

    public static ColorProfile findProfileByName(String name) {
        for (int i=0; i<availableProfiles.length; i++) {
            if (availableProfiles[i].name.equals(name))
                return availableProfiles[i];
        }
        return DEFAULT_DAY;
    }

    public final void link(ColorProfile cp) {
        this.other = cp;
        cp.other = this;
    }
}
