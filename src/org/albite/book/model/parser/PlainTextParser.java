/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.parser;

/**
 *
 * @author albus
 */
public class PlainTextParser extends TextParser {

    public boolean parseNext(
            final char[] text,
            final int textSize) {

        if (!proceed(textSize)) {
            return false;
        }

        if (processWhiteSpace(position, text, textSize)) {
            return true;
        }

        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        for (int i = position; i < textSize; i++) {
            if (isWhiteSpace(text[i]) || isNewLine(text[i])) {
                length = i - position;
                return true;
            }
        }

        /*
         * TODO: next line MAY BE BUGGY
         * that's the last word in the chapter
         */
        length = textSize - position;
        return true;
    }
}