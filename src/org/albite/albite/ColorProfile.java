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
    public int canvasBackgroupColor;
    public int canvasTextColor;
    public int canvasTextBoldColor;
    public int canvasTextItalicColor;
    public int canvasTextHeadingColor;
    public int canvasTextDummyColor;
    public int menuButtonsColor;
    public int menuButtonsPressedColor;
    public int cursorWaitColor;
    public int statusBarTextColor;

    final public static byte CANVAS_BACKGROUND_COLOR            = 0;
    final public static byte CANVAS_TEXT_COLOR                  = 1;
    final public static byte CANVAS_TEXT_BOLD_COLOR             = 2;
    final public static byte CANVAS_TEXT_ITALIC_COLOR           = 3;
    final public static byte CANVAS_TEXT_HEADING_COLOR          = 4;
    final public static byte CANVAS_DUMMY_COLOR                 = 5;
    final public static byte MENU_BUTTONS_COLOR                 = 6;
    final public static byte MENU_BUTTONS_PRESSED_COLOR         = 7;
    final public static byte CURSOR_WAIT_COLOR                  = 8;
    final public static byte STATUS_BAR_TEXT_COLOR              = 9;
    final public static byte COLORS_COUNT                       = 10;

    public int[] colors;

    final public static ColorProfile CP_DAY = new ColorProfile("day", 0xFFFFFF,
            0x6F654C, 0x2F291A, 0x4F493A, 0x2F291A,
            0xAF9F78, 0xAF9F78, 0xFF9F78, 0xAF9F78, 0xAF9F78);

    final public static ColorProfile CP_NIGHT = new ColorProfile("night", 0x222222,
            0x4c351d, 0x826630, 0x705525, 0x997332,
            0x442200, 0x4c351d, 0x884400, 0x664e24, 0x664e24);

//    final public static ColorProfile CP_NIGHT = new ColorProfile("night", 0x111111,
//            0x332000, 0x664000, 0x443000, 0xAA6600,
//            0x442200, 0x332000, 0x884400, 0x332000, 0x332000);
//
    final public static ColorProfile DEFAULT_DAY = CP_DAY;
    final public static ColorProfile DEFAULT_NIGHT = CP_NIGHT;

    final public static ColorProfile[] colorProfiles = {
        CP_DAY,
        CP_NIGHT
    };
    
    final public String name;

    public ColorProfile next;

    public ColorProfile(String name, int canvasBackgroundColor,
            int canvasTextColor, int canvasTextBoldColor,
            int canvasTextItalicColor, int canvasTextHeadingColor,
            int canvasTextDummyColor,
            int menuButtonsColor, int menuButtonsPressedColor,
            int cursorWaitColor, int statusBarTextColor) {

        this.name = name;
        this.canvasBackgroupColor = canvasBackgroundColor;
        this.canvasTextColor = canvasTextColor;
        this.canvasTextBoldColor = canvasTextBoldColor;
        this.canvasTextItalicColor = canvasTextItalicColor;
        this.canvasTextHeadingColor = canvasTextHeadingColor;
        this.canvasTextDummyColor = canvasTextDummyColor;
        this.menuButtonsColor = menuButtonsColor;
        this.menuButtonsPressedColor = menuButtonsPressedColor;
        this.cursorWaitColor = cursorWaitColor;
        this.statusBarTextColor = statusBarTextColor;

        colors = new int[COLORS_COUNT];
        colors[0] = canvasBackgroundColor;
        colors[1] = canvasTextColor;
        colors[2] = canvasTextBoldColor;
        colors[3] = canvasTextItalicColor;
        colors[4] = canvasTextHeadingColor;
        colors[5] = canvasTextDummyColor;
        colors[6] = menuButtonsColor;
        colors[7] = menuButtonsPressedColor;
        colors[8] = cursorWaitColor;
        colors[9] = statusBarTextColor;
    }

    public static ColorProfile findProfileByName(String name) {
        for (int i=0; i<colorProfiles.length; i++) {
            if (colorProfiles[i].name.equals(name))
                return colorProfiles[i];
        }
        return DEFAULT_DAY;
    }
}
