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

        sout("@ " + position);

        if (processWhiteSpace(position, text, textSize)) {
            return true;
        }

        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        sout("parsing normal text");
        for (int i = position; i < textSize; i++) {
            sout("seaching for word " + i + " of " + textSize + "...");
            if (isWhiteSpace(text[i]) || isNewLine(text[i])) {
                length = i - position;
                sout("found word @ " + i + ", length (" + length + ")");
                return true;
            }
        }

        /*
         * TODO: next line MAY BE BUGGY
         * that's the last word in the chapter
         */
        length = textSize - position;
        sout("the thing: " + length);
        return true;
    }
}