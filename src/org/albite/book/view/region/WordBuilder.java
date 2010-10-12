/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view.region;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class WordBuilder implements Breaks {

    private final char[]    text;
    private final boolean   processBreaks;

    private int             position    = 0;
    private int             length      = 0;
    private int             whitespace  = 0;
    private int             breakType   = BREAK_NONE;

    public WordBuilder(final char[] text, final boolean processBreaks) {
        this.text = text;
        this.processBreaks = processBreaks;
    }

    public final int getPosition() {
        return position;
    }

    public final int getLength() {
        return length;
    }

    public final int getBreakType() {
        return breakType;
    }

    public final int getWhiteSpace() {
        return whitespace;
    }

    /**
     *
     * Reads text and updates the position, length and line-break values
     *
     * @param start         The position to start reading from
     * @param text          The text array
     * @return              Whether there is more to read
     */
    public final boolean next() {

        breakType = BREAK_NONE;

        /*
         * Advance the position
         */
        position += length;

        if (position >= text.length) {
            return false;
        }

        /*
         * Skip whitespace and possibly produce a line-break
         */
        if (processWhiteSpace()) {
            return true;
        }

         /*
         * Skip non-whitespace
         */
        return processText();
    }

    /**
     * Reads until it finds a non-whitespace or line-break.
     *
     * @return Returns true if a line-break is following, and false
     * otherwise.
     */
    private boolean processWhiteSpace() {

        System.out.println("\n---------------------");
        System.out.println("Starting @ " + position);
        System.out.println("Processing whitespace...");
        whitespace = 0;
        char c = text[position];

        /*
         * new line reached
         */
        if (processBreaks) {
            if (c == '\r') {
                /*
                 * catch CR or CR+LF sequences
                 */
                breakType = BREAK_LINE;
                length = 1;

                if (position + 1 < text.length
                        && text[position + 1] == '\n') {
                    length = 2;
                }

                System.out.println("new line r or rn");
                return true;
            }

            if (c == '\n'
                    || c == '\u2028'    //line separator
                    || c == '\u2029'    //paragraph separator
                    ) {
                breakType = BREAK_LINE;
                length = 1;

                System.out.println("new line n");
                return true;
            }

            if (c == '\f') {
                breakType = BREAK_PAGE;
                length = 1;

                System.out.println("new page n");
                return true;
            }
        }

        /*
         * process the white space
         */
        for (; position < text.length; position++) {
            c = text[position];

            if (
                       c == ' '
                    || c == '\t'
                    || c == '\u200B'
                    || c == 0 /* null character */
                    ) {
                whitespace++;
                System.out.println("skipping space");
                continue;
            }

            System.out.println("space skipped.");
            return false;
        }

        /*
         * End of array
         */
        System.out.println("nothing to do");
        position = text.length;
        length = 0;
        return false;
    }

    /**
     * Reads until it finds a whitespace.
     * @return True if there is more text to read
     */
    private boolean processText() {
        char c;

        for (int i = position; i < text.length; i++) {
            c = text[i];

            System.out.println(
                    "seaching for word " + i + " of " + text.length + "...");
            if (
                       c == ' '         //space
                    || c == '\n'        //new line
                    || c == '\t'        //tab
                    || c == '\r'        //carriage return
                    || c == '\f'        //form feed
                    || c == '\u2028'    //line separator
                    || c == '\u2029'    //paragraph separator
                    || c == '\u200b'    //zero width space
                    || c == 0           //null space
            ) {
                length = i - position;
                System.out.println(
                        "found word @ " + position + ", length (" + length + ")");
                return true;
            }
        }

        /*
         * End of array
         */
        position = text.length;
        length = 0;
        return false;
    }
}