/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.parser;

import java.util.Vector;
import org.albite.book.model.book.elements.StylingConstants;
import org.albite.io.HTMLSubstitues;

/**
 *
 * This is a <i>very</i> simple HTML parser made for the specific purpose of
 * parsingHTMLs on the fly, i.e. without conversion. It preserves some of the
 * formatting: the one specified by the following tags:
 * - [meta encoding]
 * - [i] or [em]
 * - [b] or [strong]
 * - [center], [left], [right]
 *
 * Because of memory considerations, and File linking issues,
 * images will not be preserved.
 *
 * @author albus
 */
public class HTMLTextParser extends TextParser
        implements HTMLSubstitues, StylingConstants {

    private static final String TAG_P       = "p";
    private static final String TAG_BR      = "br";

    private static final String TAG_IMG     = "img";

    private static final String TAG_B       = "b";
    private static final String TAG_STRONG  = "strong";
    private static final String TAG_I       = "i";
    private static final String TAG_EM      = "em";

    private static final String TAG_H1      = "h1";
    private static final String TAG_H2      = "h2";
    private static final String TAG_H3      = "h3";
    private static final String TAG_H4      = "h4";
    private static final String TAG_H5      = "h5";
    private static final String TAG_H6      = "h6";

    private static final String TAG_CENTER  = "center";
    private static final String TAG_LEFT    = "left";
    private static final String TAG_RIGHT   = "right";
    private static final String TAG_HR      = "hr";

    private static final String TAG_PRE     = "pre";

    private int pos;
    private int len;

    private int bold = 0;
    private int italic = 0;
    private int heading = 0;
    
    private int pre = 0;

    private Vector align = new Vector(20); // max 20 align depth?

    public HTMLTextParser() {
        processBreaks = false;
        align.addElement(new Integer(JUSTIFY));
    }

    public final void parseNext(
            final int newPosition,
            final char[] text,
            final int textSize) {

        reset(newPosition);

        System.out.println("Position @ " + position + " of " + textSize);
//        System.out.print("Parsing whitespace...");
        if (processWhiteSpace(newPosition, text, textSize)) {
//            System.out.println("...RET");
            return;
        }
//        System.out.println("...CON");

        //Parse markup instructions
        System.out.print("Parsing markup...");
        if (parseMarkup(text, textSize)) {
            System.out.println("OK");
            return;
        }
        System.out.println();

        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        System.out.print("Parsing text...");
        for (int i = position; i < textSize; i++) {
            ch = text[i];
            if (isWhiteSpace(ch) || isNewLine(ch) || ch == START_TAG_CHAR) {
                length = i - position;
                System.out.println("LEN: " + length);
                System.out.println(new String(text, position, length));
                return;
            }
        }

        position = textSize;
        length = 0;
        state = STATE_PASS;
        System.out.println("END.");
    }

    private boolean parseMarkup(final char[] text, final int textSize) {

        pos = position;
        boolean terminatingTag = false;

        /*
         * At least two chars for tags
         */
        if (textSize > pos + 1
                && text[pos] == START_TAG_CHAR) {

            state = STATE_PASS;

            if (text[pos + 1] == '/') {
                terminatingTag = true;
                pos++;
            }

            pos++;

            /*
             * back to position
             */
            position = pos;
            
            if (text.length <= pos) {
                return false;
            }

            for (int i = pos; i < textSize; i++) {

                ch = text[i];

                if (ch == END_TAG_CHAR) {
//                    length = i - startMarkupPosition;
                    length = i - position + 1;

//                    TODO, parse the tag!
                    System.out.println();
                    System.out.println("[" + new String(text, position, length - 1) + "]");
//                    System.out.println(new String(text, pos, length - 2));
                    System.out.println(terminatingTag);

                    /*
                     * Parse the name
                     */
                    len = length - 1;
                    int max = position + length - 1;
                    for (int k = position; k < max; k++) {
                        ch = text[k];

                        if (isWhiteSpace(ch) || isNewLine(ch)) {
                            len = k - position;
                            break;
                        }
                    }

                    String name = new String(text, position, len);

                    if (TAG_BR.equalsIgnoreCase(name)) {
                        /*
                         * New line
                         */
                        state = STATE_NEW_LINE;
                        return true;
                    }

                    if (terminatingTag && TAG_P.equalsIgnoreCase(name)) {
                        /*
                         * New line
                         */
                        state = STATE_NEW_LINE;
                        return true;
                    }

                    if (TAG_HR.equalsIgnoreCase(name)) {
                        /*
                         * Horizontal ruler
                         */
                        state = STATE_RULER;
                        return true;
                    }

                    if (TAG_IMG.equalsIgnoreCase(name)) {
                        /*
                         * Image
                         */

                        //TODO: parse attributes
                        return true;
                    }

                    if (terminatingTag) {
                        if (TAG_B.equalsIgnoreCase(name)
                                || TAG_STRONG.equalsIgnoreCase(name)) {
                            bold--;

                            if (bold <= 0) {
                                bold = 0;
                                disableBold = true;
                                state = STATE_STYLING;
                            }

                            state = STATE_PASS;
                            return true;
                        }

                        if (TAG_I.equalsIgnoreCase(name)
                                || TAG_EM.equalsIgnoreCase(name)) {
                            italic--;

                            if (italic <= 0) {
                                italic = 0;
                                disableItalic = true;
                                state = STATE_STYLING;
                            }

                            state = STATE_PASS;
                            return true;
                        }

                        if (TAG_H1.equalsIgnoreCase(name)
                                || TAG_H2.equalsIgnoreCase(name)
                                || TAG_H3.equalsIgnoreCase(name)
                                || TAG_H4.equalsIgnoreCase(name)
                                || TAG_H5.equalsIgnoreCase(name)
                                || TAG_H6.equalsIgnoreCase(name)) {
                            heading--;

                            if (heading <= 0) {
                                heading = 0;
                                disableHeading = true;
                                state = STATE_STYLING;
                            }

                            state = STATE_PASS;
                            return true;
                        }

                        if (TAG_CENTER.equalsIgnoreCase(name)
                                || TAG_LEFT.equalsIgnoreCase(name)
                                || TAG_RIGHT.equalsIgnoreCase(name)) {

                            if(align.isEmpty()) {
                                enableJustifyAlign = true;
                            } else {

                                byte al = ((Integer) align.lastElement())
                                        .byteValue();

                                switch (al) {
                                    case LEFT:
                                        enableLeftAlign = true;
                                        break;

                                    case RIGHT:
                                        enableRightAlign = true;
                                        break;

                                    case CENTER:
                                        enableCenterAlign = true;
                                        break;

                                    default:
                                        enableJustifyAlign = true;
                                }
                            }

                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_PRE.equalsIgnoreCase(name)) {
                            pre--;

                            if (pre <= 0) {
                                pre = 0;
                                processBreaks = false;
                            }

                            state = STATE_PASS;
                            return true;
                        }
                    } else {
                        if (TAG_B.equalsIgnoreCase(name)
                                || TAG_STRONG.equalsIgnoreCase(name)) {
                            bold++;

                            enableBold = true;
                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_I.equalsIgnoreCase(name)
                                || TAG_EM.equalsIgnoreCase(name)) {
                            italic++;

                            enableItalic = true;
                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_H1.equalsIgnoreCase(name)
                                || TAG_H2.equalsIgnoreCase(name)
                                || TAG_H3.equalsIgnoreCase(name)
                                || TAG_H4.equalsIgnoreCase(name)
                                || TAG_H5.equalsIgnoreCase(name)
                                || TAG_H6.equalsIgnoreCase(name)) {
                            heading++;

                            enableHeading = true;
                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_CENTER.equalsIgnoreCase(name)) {
                            align.addElement(new Integer(CENTER));
                            enableCenterAlign = true;
                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_LEFT.equalsIgnoreCase(name)) {
                            align.addElement(new Integer(LEFT));
                            enableLeftAlign = true;
                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_RIGHT.equalsIgnoreCase(name)) {
                            align.addElement(new Integer(RIGHT));
                            enableRightAlign = true;
                            state = STATE_STYLING;
                            return true;
                        }

                        if (TAG_PRE.equalsIgnoreCase(name)) {
                            pre++;
                            processBreaks = true;
                            state = STATE_PASS;
                        }
                    }

                    System.out.println("Name: {" + new String(text, position, len) + "}");
//                    System.out.println("Ends @ " + i);
//                    position = startMarkupPosition; // + 1;
//                    position = i; // + 1;
                    return true;
                }
            }
            /*
             * TODO: Do not know if next line is OK.
             */
            position = textSize;
            length = 1;
            return true;
        }

        return false;
    }
}