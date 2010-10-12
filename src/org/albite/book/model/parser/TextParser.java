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
    public static final byte    STATE_PASS      = 0;

    /**
     * Display the parsed text
     */
    public static final byte    STATE_NORMAL    = 1;

    /**
     * make a new paragraph
     */
    public static final byte    STATE_NEW_LINE  = 2;

    /*
     * the next come from parsing AlbML
     */

    /**
     * Update current styling
     */
    public static final byte    STATE_STYLING   = 3;

    /**
     * Add a new image
     */
    public static final byte    STATE_IMAGE     = 4;

    /**
     * Show a simple horizontal a ruler that takes one line in height
     * and has the width of the text column.
     */
    public static final byte    STATE_RULER     = 5;

    /**
     * Show a symbol separating the paragraphs. For now
     * has the same effect as STATE_RULER with the exception
     * that the ruler is shorter in width
     */
    public static final byte    STATE_SEPARATOR = 6;

    public int                  whiteSpace = 0;
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
    
    /*
     * used as a temp buffer
     */
    protected char              ch;
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
                System.out.println("new line r or rn");
                return true;
            }

            if (ch == '\n') {
                //catch single LFs
                state = TextParser.STATE_NEW_LINE;
                length = 1;
                System.out.println("new line n");
                return true;
            }
        }
        //skip the blank space
        for (int i = newPosition; i < textSize; i++) {
            ch = text[i];
            if (isWhiteSpace(ch) || isNewLine(ch)) {
                whiteSpace++;
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
    public abstract void parseNext(int newPosition, char[] text, int textSize);
}