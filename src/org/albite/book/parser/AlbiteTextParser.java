/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.parser;

/**
 *
 * @author albus
 */
public class AlbiteTextParser extends PlainTextParser {

    public final void parseNext(
            final int newPosition,
            final char[] text,
            final int textSize) {

        reset();
        position = newPosition;

        if (processWhiteSpace(newPosition, text, textSize)) {
            return;
        }

        //Parse markup instructions
        if (parseMarkup(text, textSize)) {
            return;
        }

        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        for (int i = position; i < textSize; i++) {
            if (
                    text[i] == ' '
                    || text[i] == '\n'
                    || text[i] == '\t'
                    || text[i] == '\r'
                    || (text[i] == '@'
                   && i + 2 < textSize && text[i + 1] == '{')
            ) {
                length = i - position;
                return;
            }
        }

        /*
         * TODO: next line MAY BE BUGGY
         * that's the last word in the chapter
         */
        length = textSize - position;
    }

    /**
     *
     * @param text
     * @param textSize
     * @return true if it's necessary to stop parsing
     */
    private boolean parseMarkup(
            final char[] text,
            final int textSize
            ) {
            int startMarkupPosition = position;
            char current_char;

        /*
         * at least 3 chars needed for valid markup, i.e. @{}
         */
        if (textSize > startMarkupPosition + 2
                    && text[startMarkupPosition] == '@'
                    && text[++startMarkupPosition] == '{') {
            /*
             * so it evidently is markup starting point
             * watchout for missing ending braces!
             * or one might have the whole file scanned
             */
            current_char = text[++startMarkupPosition];

            /*
             * i.e. expecting styling definitions @{iIbBhH}
             */
            state = STATE_STYLING;

            switch (current_char) {
                case 'x':
                case 'X':
                    //Image instruction: @{image.png:Image alt text}
                    state = STATE_IMAGE;
                    break;

                case 'u':
                case 'U':
                    state = STATE_RULER;
                    break;

                case 's':
                case 'S':
                    state = STATE_SEPARATOR;
                    break;
            }

            switch(state) {

                case STATE_RULER:
                case STATE_SEPARATOR:
                    for (int i = startMarkupPosition;
                    i < textSize; i++) {

                        current_char = text[i];
                        if (current_char == '}') {
                            length = i - position+1;
                            break;
                        }
                    }
                    break;

                case STATE_STYLING:
                    for (int i = startMarkupPosition;
                    i < textSize; i++) {

                        current_char = text[i];
                        if (current_char == '}') {
                            length = i - position+1;
                            break;
                        }

                        switch (current_char) {
                            case 'I':
                                enableItalic = true;
                                break;

                            case 'i':
                                disableItalic = true;
                                break;

                            case 'B':
                                enableBold = true;
                                break;

                            case 'b':
                                disableBold = true;
                                break;

                            case 'H':
                                enableHeading = true;
                                break;

                            case 'h':
                                disableHeading = true;
                                break;

                            case 'L':
                            case 'l':
                                enableLeftAlign = true;
                                break;

                            case 'R':
                            case 'r':
                                enableRightAlign = true;
                                break;

                            case 'C':
                            case 'c':
                                enableCenterAlign = true;
                                break;

                            case 'J':
                            case 'j':
                                enableJustifyAlign = true;
                                break;
                        }
                    }
                    break;

                case STATE_IMAGE:
                    imageURLPosition = ++startMarkupPosition;
                    boolean alt_text_found = false;
                    for (int i = startMarkupPosition;
                    i < textSize; i++) {

                        current_char = text[i];
                        if (current_char == '}') {
                            /*
                             * no alt text provided
                             */
                            imageURLLength =
                                    i - startMarkupPosition;
                            length = i - position + 1;
                            break;
                        }

                        if (current_char == ':') {
                            /*
                             * alt text follows
                             */
                            alt_text_found = true;
                            imageURLLength = i - imageURLPosition;
                            startMarkupPosition = i + 1;
                            break;
                        }
                    }

                    if (alt_text_found) {
                        imageTextPosition = startMarkupPosition;
                        for (int i = startMarkupPosition;
                        i < textSize; i++) {
                            current_char = text[i];
                            if (current_char == '}') {
                                imageTextLength =
                                        i - startMarkupPosition;
                                length = i - position + 1;
                                break;
                            }
                        }
                    }
                    break;
            }
            return true;
        }
        return false;
    }
}
