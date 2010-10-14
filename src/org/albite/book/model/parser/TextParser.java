/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.parser;

/**
 *
 * @author Albus Dumbledore
 */
public abstract class TextParser {

    /**
     * Discard the parsed text
     */
    public static final byte    STATE_PASS              = 0;

    /**
     * Display the parsed text
     */
    public static final byte    STATE_TEXT              = 1;

    /**
     * make a new paragraph
     */
    public static final byte    STATE_NEW_LINE          = 2;

    /**
     * makes a new line, only if the text is not on a new line already
     */
    public static final byte    STATE_NEW_SOFT_LINE     = 3;

    /**
     * Update current styling
     */
    public static final byte    STATE_STYLING           = 4;

    /**
     * Add a new image
     */
    public static final byte    STATE_IMAGE             = 5;

    /**
     * Show a simple horizontal a ruler that takes one line in height
     * and has the width of the text column.
     */
    public static final byte    STATE_RULER             = 6;

//    public int                  whiteSpace = 0;
    public int                  position;
    public int                  length;

    public boolean              processBreaks = true;
    public byte                 state;

    public boolean              enableItalic;
    public boolean              disableItalic;

    public boolean              enableBold;
    public boolean              disableBold;

    public boolean              enableHeading;
    public boolean              disableHeading;

    public boolean              enableCenterAlign;
    public boolean              disableCenterAlign;

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
    
    /*
     * used as a temp buffer
     */
    protected char              ch;
    public TextParser() {
        reset();
    }

    private final void resetContent() {
        state               = STATE_TEXT;

//        whiteSpace          = 0;

        enableItalic        = false;
        disableItalic       = false;
        enableBold          = false;
        disableBold         = false;
        enableHeading       = false;
        disableHeading      = false;

        enableCenterAlign   = false;
        disableCenterAlign  = false;

        imageURLPosition    = 0;
        imageURLLength      = 0;
        imageTextPosition   = 0;
        imageTextLength     = 0;
    }

    public final void reset() {
        resetContent();
        position            = 0;
        length              = 0;
    }

    protected final boolean proceed(final int bufferSize) {
        position += length;
        length = 0;

        if (position >= bufferSize) {
            return false;
        }

        resetContent();

        return true;
    }

    protected boolean processWhiteSpace(
            final int newPosition,
            final char[] text,
            final int textSize) {

        if (processBreaks) {
            ch = text[newPosition];
            if (ch == '\r') {
                //catch CR or CR+LF sequences
                state = TextParser.STATE_NEW_LINE;
                length = 1;
                if (newPosition + 1 < textSize
                        && text[newPosition + 1] == '\n') {
                    length = 2;
                }
                sout("new line r or rn");
                return true;
            }

            if (ch == '\n') {
                //catch single LFs
                state = TextParser.STATE_NEW_LINE;
                length = 1;
                sout("new line n");
                return true;
            }
        }
        //skip the blank space
        for (int i = newPosition; i < textSize; i++) {
            ch = text[i];
            if (isWhiteSpace(ch) || isNewLine(ch)) {
                continue;
            }

            position = i;
            return false;
        }

        position = textSize;
        return false;
    }

    protected final boolean isWhiteSpace(final char c) {
        return c == ' '
                || c == '\t'
                || c == 0; //null char
    }

    protected final boolean isNewLine(final char c) {
        return c == '\n'
                || c == '\r'
                || c == '\f';
    }
    
    /*
     * If a 'normal' word is found, then it returns starting position of word
     * and its length
     */
//    public abstract void parseNext(int newPosition, char[] text, int textSize);
    public abstract boolean parseNext(char[] text, int textSize);

    public static final void sout(final String s) {
//        System.out.println(s);
    }
}