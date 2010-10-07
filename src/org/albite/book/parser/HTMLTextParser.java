/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.parser;

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
            if (
                    text[i] == ' '
                    || text[i] == '\n'
                    || text[i] == '\t'
                    || text[i] == '\r'
                    || text[i] == '<'
                    || text[i] == 0 //null char
                    ) {
                length = i - position;
                System.out.println("LEN: " + length);
                System.out.println(new String(text, position, length));
                return;
            }
        }
        System.out.println("END.");
    }

    private boolean processWhiteSpace(
            final int newPosition,
            final char[] text,
            final int textSize) {

        //skip the blank space
        for (int i = newPosition; i < textSize; i++) {
            if (text[i] == ' '
                    || text[i] == '\n'
                    || text[i] == '\r'
                    || text[i] == '\t'
                    || text[i] == '\f'
                    || text[i] == 0 //null char
                    ) {
                continue;
            }

            position = i;
            return false;
        }

        position = textSize;
        return false;
    }

    private boolean parseMarkup(
            final char[] text,
            final int textSize
            ) {

        int startMarkupPosition = position;
        char currentChar;
        boolean terminatingTag = false;

        /*
         * At least two chars for tags
         */
        if (textSize > startMarkupPosition + 1
                && text[startMarkupPosition] == '<') {

            state = STATE_PASS;

            if (text[startMarkupPosition + 1] == '/') {
                terminatingTag = true;
                startMarkupPosition++;
            }

            startMarkupPosition++;

            if (text.length <= startMarkupPosition) {
                return false;
            }

            for (int i = startMarkupPosition; i < textSize; i++) {

                currentChar = text[i];

                if (currentChar == '>') {
//                    length = i - startMarkupPosition;
                    length = i - position + 1;

                    //TODO, parse the tag!
//                    System.out.println(
//                            new String(text, position, length));
//                            new String(text, startMarkupPosition, length - 2));

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