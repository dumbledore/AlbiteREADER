/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.parser;

/**
 *
 * @author Albus Dumbledore
 */
public abstract class TextParser {

    public static final byte    STATE_PASS      = 0;

    public static final byte    STATE_NORMAL    = 1;
    public static final byte    STATE_NEW_LINE  = 2;

    /*
     * the next come from parsing AlbML
     */
    public static final byte    STATE_STYLING   = 3;
    public static final byte    STATE_IMAGE     = 4;

    /*
     * simply a ruler, taking one line and having the length
     * of the text column width
     */
    public static final byte    STATE_RULER     = 5;

    /*
     * separates paragraphs
     */
    public static final byte    STATE_SEPARATOR = 6;

    public int                  position;
    public int                  length;

    public byte                 state;

    public boolean              enableItalic;
    public boolean              disableItalic;
    public boolean              enableBold;
    public boolean              disableBold;
    public boolean              enableHeading;
    public boolean              disableHeading;

    public boolean              enableLeftAlign;
    public boolean              enableRightAlign;
    public boolean              enableCenterAlign;
    public boolean              enableJustifyAlign;

    /*
     * position in current chapter's textbuffer
     */
    public int                  imageURLPosition;

    public int                  imageURLLength;

    /*
     * position in current chapter's textbuffer
     */
    public int                  imageTextPosition;
    public int                  imageTextLength;

    public TextParser() {
        reset();
    }

    public final void reset() {
        position            = 0;
        length              = 0;

        state               = STATE_NORMAL;

        enableItalic        = false;
        disableItalic       = false;
        enableBold          = false;
        disableBold         = false;
        enableHeading       = false;
        disableHeading      = false;

        enableLeftAlign     = false;
        enableRightAlign    = false;
        enableCenterAlign   = false;
        enableJustifyAlign  = false;

        imageURLPosition    = 0;
        imageURLLength      = 0;
        imageTextPosition   = 0;
        imageTextLength     = 0;
    }

    public final void reset(final int position) {
        reset();
        this.position = position;
    }

    /*
     * If a 'normal' word is found, then it returns starting position of word
     * and its length
     */
    public abstract void parseNext(int newPosition, char[] text, int textSize);
}