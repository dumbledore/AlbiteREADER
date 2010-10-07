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

        reset(newPosition);

        System.out.println("AlbP @ " + newPosition);

        if (processWhiteSpace(newPosition, text, textSize)) {
            return;
        }
        System.out.println("parsing markup text");

        //Parse markup instructions
        if (parseMarkup(text, textSize)) {
            System.out.println("Markup found!");
            return;
        }

        /*
         * parsing normal text; stopping at stop-chars or end of textbuffer
         */
        System.out.println("processing normal text...");
        for (int i = position; i < textSize; i++) {
            System.out.println("seaching for word " + i + " of " + textSize + "...");
            if (
                    text[i] == ' '
                    || text[i] == '\n'
                    || text[i] == '\t'
                    || text[i] == '\r'
                    || (text[i] == '@'
                   && i + 2 < textSize && text[i + 1] == '{')
            ) {
                length = i - position;
                System.out.println("found word @ " + i + ": " + new String(text, position, length));
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
            char currentChar;

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
            currentChar = text[++startMarkupPosition];

            /*
             * i.e. expecting styling definitions @{iIbBhH}
             */

            switch (currentChar) {
                case 'x':
                case 'X':
                    /*
                     * Image instructions:
                     * @{X:image.png:Image alt text}
                     */
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

                default:
                    /*
                     * All other possible instructions are assumed to
                     * refer to styling, i.e. turn on/off styling attributes
                     */
                    state = STATE_STYLING;
                    break;
            }

            switch (state) {

                case STATE_RULER:
                case STATE_SEPARATOR:
                    for (int i = startMarkupPosition; i < textSize; i++) {
                        /*
                         * Just skipping any other instructions
                         */
                        currentChar = text[i];
                        if (currentChar == '}') {
                            length = i - position + 1;
                            return true;
                        }
                    }

                case STATE_STYLING:
                    for (int i = startMarkupPosition; i < textSize; i++) {
                        /*
                         * Parsing all chars in the braces
                         */
                        currentChar = text[i];

                        if (currentChar == '}') {
                            /*
                             * End of instructions
                             */
                            length = i - position + 1;
                            return true;
                        }

                        switch (currentChar) {
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

                case STATE_IMAGE:
                    startMarkupPosition += 2;
                    imageURLPosition = startMarkupPosition;

                    for (int i = startMarkupPosition; i < textSize; i++) {
                        currentChar = text[i];

                        if (currentChar == '}') {
                            /*
                             * End of instructions.
                             * No image title provided.
                             */
                            imageURLLength = i - startMarkupPosition;
                            length = i - position + 1;
                            return true;
                        }

                        if (currentChar == ':') {
                            /*
                             * image title follows
                             */
                            imageURLLength = i - imageURLPosition;
                            startMarkupPosition = i + 1;
                            break;
                        }
                    }

                    imageTextPosition = startMarkupPosition;
                    for (int i = startMarkupPosition; i < textSize; i++) {
                        currentChar = text[i];

                        if (currentChar == '}') {
                            /*
                             * End of instructions
                             */
                            imageTextLength = i - startMarkupPosition;
                            length = i - position + 1;
                            return true;
                        }
                    }
                default:
                    /*
                     *  EOF reached without closing brace
                     */
                    position = textSize;
            }
        }
        /*
         * The text is not for parsing
         */
        return false;
    }
}
