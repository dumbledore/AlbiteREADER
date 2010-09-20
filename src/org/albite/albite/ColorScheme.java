/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

/**
 *
 * @author Albus Dumbledore
 */
public class ColorScheme {

    public static final byte COLOR_BACKGROUND           = 0;
    public static final byte COLOR_TEXT                 = 1;
    public static final byte COLOR_TEXT_BOLD            = 2;
    public static final byte COLOR_TEXT_ITALIC          = 3;
    public static final byte COLOR_TEXT_HEADING         = 4;
    public static final byte COLOR_TEXT_DUMMY           = 5;
    public static final byte COLOR_MENU                 = 6;
    public static final byte COLOR_MENU_PRESSED         = 7;
    public static final byte COLOR_CURSOR_WAIT          = 8;
    public static final byte COLOR_TEXT_STATUS          = 9;
    public static final byte COLOR_FRAME                = 10;

    public static final String[] SCHEMES =
            {"Black and white", "Soft", "Bright"};

    public static final String[] HUE_NAMES = {
        "Orange",
        "Golden Yellow",
        "Yellow",
        "Green-Yellow",
        "Green",
        "Cyan",
        "Blue",
        "Violet",
        "Pink",
        "Red"
    };

    public static final float[] HUE_VALUES = {
        0.083F, //Orange
        0.12F, //Golden Yellow
        0.167F, //Yellow
        0.24F, //Green-Yellow
        0.36F, //Green
        0.48F, //Cyan
        0.60F, //Blue
        0.75F, //-violet
        0.84F,
        0.96F
    };

    public static final byte TYPE_DEFAULT             = 0;
    public static final byte TYPE_SOFT                = 1;
    public static final byte TYPE_BRIGHT              = 2;

    public static final ColorScheme DEFAULT_DAY =
            new ColorScheme(TYPE_DEFAULT, true, 0,
            new int[] {
                0xFFFFFF,   //bg
                0x000000,   //text
                0x777777,   //bold
                0x555555,   //italic
                0x777777,   //heading
                0x555555,   //dummy
                0x555555,   //menu
                0x777777,   //menu p
                0x555555,   //wait
                0x555555,   //status
                0x555555,   //frame
            });

    public static final ColorScheme DEFAULT_NIGHT =
            new ColorScheme(TYPE_DEFAULT, false, 0,
            new int[] {
                0x000000,   //bg
                0xFFFFFF,   //text
                0x666666,   //bold
                0x888888,   //italic
                0x666666,   //heading
                0x888888,   //dummy
                0x555555,   //menu
                0x777777,   //menu p
                0x555555,   //wait
                0x555555,   //status
                0x888888,   //frame
            });

    /*
     * Colors array. It's public as to speed things a bit, as it would be used
     * quite a lot.
     */
    public  final int[] colors;

    private final byte type;
    private final boolean day;
    private final float hue;
    private ColorScheme other;

    private ColorScheme(
            final byte type, final boolean day,
            final float hue, final int[] colors) {

        this.type = type;
        this.day = day;
        this.hue = hue;
        this.colors = colors;
    }

    public static ColorScheme getScheme(
            final byte type, final boolean day, final float hue) {

        switch(type) {
            case TYPE_DEFAULT:
                return (day ? DEFAULT_DAY : DEFAULT_NIGHT);

            case TYPE_SOFT:
                return (day ? softDayScheme(hue) : softNightScheme(hue));

            case TYPE_BRIGHT:
                return (day ? brightDayScheme(hue) : brightNightScheme(hue));

            default:
                return DEFAULT_DAY;
        }
    }

    public final void link(final ColorScheme cs) {
        this.other = cs;
        cs.other = this;
    }

    public final ColorScheme getOther() {
        return other;
    }

    public final byte getType() {
        return type;
    }

    public final float getHue() {
        return hue;
    }

    public final boolean isDay() {
        return day;
    }

    private static int HSBtoRGB(float hue, float saturation, float brightness) {
	int r = 0, g = 0, b = 0;
    	if (saturation == 0) {
	    r = g = b = (int) (brightness * 255.0f + 0.5f);
	} else {
	    float h = (hue - (float)Math.floor(hue)) * 6.0f;
	    float f = h - (float)java.lang.Math.floor(h);
	    float p = brightness * (1.0f - saturation);
	    float q = brightness * (1.0f - saturation * f);
	    float t = brightness * (1.0f - (saturation * (1.0f - f)));
	    switch ((int) h) {
	    case 0:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (t * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 1:
		r = (int) (q * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 2:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (t * 255.0f + 0.5f);
		break;
	    case 3:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (q * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 4:
		r = (int) (t * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 5:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (q * 255.0f + 0.5f);
		break;
	    }
	}
	return (r << 16) | (g << 8) | (b << 0);
    }

    private static ColorScheme softDayScheme(final float hue) {

        final int text = HSBtoRGB(hue, 0.32F, 0.44F);
        final int bold = HSBtoRGB(hue, 0.45F, 0.18F);
        final int italic = HSBtoRGB(hue, 0.27F, 0.31F);
        final int menu = HSBtoRGB(hue, 0.31F, 0.69F);
        final int menup = HSBtoRGB(hue, 0.6F, 0.5F);

        final int[] colors = new int[] {
            0xFFFFFF,   //bg
            text,       //text
            bold,       //bold
            italic,     //italic
            bold,       //heading
            menu,       //dummy
            menu,       //menu
            menup,      //menu p
            italic,     //wait
            menu,       //status
            italic,       //frame
        };

        return new ColorScheme(TYPE_SOFT, true, hue, colors);
    }

    private static ColorScheme brightDayScheme(final float hue) {

        final int text = HSBtoRGB(hue, 1, 0.5F);
        final int bold = HSBtoRGB(hue, 0.7F, 0.35F);
        final int italic = HSBtoRGB(hue, 0.7F, 0.7F);
        final int menu = HSBtoRGB(hue, 0.8F, 0.8F);
        final int menup = HSBtoRGB(hue, 0.8F, 0.55F);

        final int[] colors = new int[] {
            0xFFFFFF,   //bg
            text,       //text
            bold,       //bold
            italic,     //italic
            bold,       //heading
            menu,       //dummy
            menu,       //menu
            menup,      //menu p
            italic,     //wait
            menu,       //status
            text,       //frame
        };

        return new ColorScheme(TYPE_BRIGHT, true, hue, colors);
    }

    private static ColorScheme softNightScheme(final float hue) {

        final int text = HSBtoRGB(hue, 0.60F, 0.38F);
        final int bold = HSBtoRGB(hue, 0.77F, 0.84F);
        final int italic = HSBtoRGB(hue, 0.42F, 0.72F);
        final int menu = HSBtoRGB(hue, 0.85F, 0.38F);
        final int menup = HSBtoRGB(hue, 0.85F, 0.84F);

        final int[] colors = new int[] {
            0x000000,   //bg
            text,       //text
            bold,       //bold
            italic,     //italic
            bold,       //heading
            menu,       //dummy
            menu,       //menu
            menup,      //menu p
            italic,     //wait
            menu,       //status
            text,       //frame
        };

        return new ColorScheme(TYPE_SOFT, false, hue, colors);
    }

    private static ColorScheme brightNightScheme(final float hue) {

        final int text = HSBtoRGB(hue, 1, 1);
        final int bold = HSBtoRGB(hue, 0.1F, 1);
        final int italic = HSBtoRGB(hue, 0.4F, 1);
        final int menu = HSBtoRGB(hue, 1F, 1F);
        final int menup = HSBtoRGB(hue, 0.2F, 1F);

        final int[] colors = new int[] {
            0x000000,   //bg
            text,       //text
            bold,       //bold
            italic,     //italic
            bold,       //heading
            menu,       //dummy
            menu,       //menu
            menup,      //menu p
            italic,     //wait
            menu,       //status
            text,       //frame
        };

        return new ColorScheme(TYPE_BRIGHT, false, hue, colors);
    }
}