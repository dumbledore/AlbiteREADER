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
 *
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

        reset();
        position = newPosition;
        processWhiteSpace(newPosition, text, textSize);

        //Parse markup instructions
        parseMarkup(text, textSize);

        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        for (int i = position; i < textSize; i++) {
            if (text[i] == '<') {
                length = i - position;
                return;
            }
        }
    }

    private void processWhiteSpace(
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
                    ) {
                continue;
            }

            position = i;
            return;
        }
    }

    private void parseMarkup(
            final char[] text,
            final int textSize
            ) {

        int start_markup_position = position;
        char current_char;
        boolean terminatingTag = false;

        if (text[start_markup_position] == '<') {
            /*
             * so it evidently is markup starting point
             * watchout for missing ending braces!
             * or one might have the whole file scanned
             */
            if (text.length > start_markup_position + 2
                    && text[start_markup_position + 1] == '/') {
                terminatingTag = true;
                start_markup_position++;
            }

            start_markup_position++;

            if (text.length <= start_markup_position) {
                return;
            }

            current_char = text[start_markup_position];

            for (int i = start_markup_position; i < textSize; i++) {

                current_char = text[i];
                if (current_char == '>') {
                    length = i - position + 1;
                    System.out.println(
                            new String(text, start_markup_position, length));
                    //TODO, parse the tag!
                    break;
                }
            }
        }
    }
}