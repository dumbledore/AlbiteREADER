/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.parser;

/**
 *
 * @author albus
 */
public class PlainTextParser extends TextParser {

    public void parseNext(
            final int newPosition,
            final char[] text,
            final int textSize) {

        reset(newPosition);

        System.out.println("@ " + newPosition);

        if (processWhiteSpace(newPosition, text, textSize)) {
            return;
        }
        System.out.println("parsing normal text");
        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        for (int i = position; i < textSize; i++) {
            System.out.println("seaching for word " + i + " of " + textSize + "...");
            if (
                    text[i] == ' '
                    || text[i] == '\n'
                    || text[i] == '\t'
                    || text[i] == '\r'
                    || text[i] == 0
            ) {
                length = i - position;
                System.out.println("found word @ " + i + ", length (" + length + ")");
                return;
            }
        }

        /*
         * TODO: next line MAY BE BUGGY
         * that's the last word in the chapter
         */
        length = textSize - position;
        System.out.println("the thing: " + length);
    }

    protected boolean processWhiteSpace(
            final int newPosition,
            final char[] text,
            final int textSize) {

        System.out.println("Processing whitespace...");

//        if reached a new line character
        if (text[newPosition] == '\r') {
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

        if (text[newPosition] == '\n') {
            //catch single LFs
            state = TextParser.STATE_NEW_LINE;
            length = 1;
            System.out.println("new line n");
            return true;
        }

        //skip the blank space
        for (int i = newPosition; i < textSize; i++) {
            if (
                    text[i] == ' '
                    || text[i] == '\t'
                    || text[i] == 0 //null character!
                    ) {
                System.out.println("skipping space");
                continue;
            }
            System.out.println("space skipped.");
            position = i;
            return false;
        }
        position = textSize;
        System.out.println("nothing to do");
        return false;
    }
}