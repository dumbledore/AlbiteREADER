/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.parser;

import org.albite.lang.TextTools;

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
public class HTMLTextParser extends TextParser {

    private int pos;
    private int len;

    public HTMLTextParser() {
        processBreaks = false;
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
            if (isWhiteSpace(ch) || isNewLine(ch) || ch == '<') {
                length = i - position;
                System.out.println("LEN: " + length);
                System.out.println(new String(text, position, length));
                return;
            }
        }
        System.out.println("END.");
    }

    private boolean parseMarkup(final char[] text, final int textSize) {

        pos = position;
        boolean terminatingTag = false;

        /*
         * At least two chars for tags
         */
        if (textSize > pos + 1
                && text[pos] == '<') {

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

                if (ch == '>') {
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

                    TextTools.toLowerCase(text, position, len);

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
            return true;
        }

        return false;
    }
}