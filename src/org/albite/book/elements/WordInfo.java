/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.elements;

/**
 *
 * @author Albus Dumbledore
 */
public class WordInfo {

    final public static byte STATE_NORMAL   = 1;
    final public static byte STATE_NEW_LINE = 2;

    //the next come from parsing AlbML
    final public static byte STATE_STYLING  = 3;
    final public static byte STATE_IMAGE    = 4;
    final public static byte STATE_RULER    = 5; //simply a ruler, taking one line and having the length of the text column width
    final public static byte STATE_SEPARATOR= 6; //separates paragraphs using a nice symbol :-)

    public int      position;
    public int      length;

    public byte     state;

    public boolean  enableItalic;
    public boolean  disableItalic;
    public boolean  enableBold;
    public boolean  disableBold;
    public boolean  enableHeading;
    public boolean  disableHeading;

    public boolean  enableLeftAlign;
    public boolean  enableRightAlign;
    public boolean  enableCenterAlign;
    public boolean  enableJustifyAlign;
    public boolean  disableAlign;

    public int      imageURLPosition;  //position in current chapter's textbuffer
    public int      imageURLLength;
    public int      imageTextPosition; //position in current chapter's textbuffer
    public int      imageTextLength;

    public WordInfo() {
        reset();
    }

    public void reset() {
        position            = 0;
        length              = 0;

        state               = STATE_NORMAL;

        enableItalic        = false;
        disableItalic       = false;
        enableBold          = false;
        disableBold         = false;
        enableHeading       = false;
        disableHeading      = false;
        
        enableLeftAlign     = false;
        enableRightAlign    = false;
        enableCenterAlign   = false;
        enableJustifyAlign  = false;

        imageURLPosition    = 0;
        imageURLLength      = 0;
        imageTextPosition   = 0;
        imageTextLength     = 0;
    }

    /***
     * If a 'normal' word is found, then it returns starting position of word and its length
     * @param text
     * @param newPosition position of word
     * @param wordInfo length of word or: -2 for a linebreak; -3 for a pagebreak
     */
    public void parseNext(char[] text, int newPosition, int chapterSize) {
        reset();
        position = newPosition;

        //if reached a new line character
        if (text[newPosition] == '\r') {
            //catch CR or CR+LF sequences
            state = WordInfo.STATE_NEW_LINE;
            length = 1;
            if (newPosition+1<chapterSize && text[newPosition+1] == '\n')
                length = 2;
            return;
        }

        if (text[newPosition] == '\n') {
            //catch single LFs
            state = WordInfo.STATE_NEW_LINE;
            length = 1;
            return;
        }

        //skip the blank space
        for (int i=newPosition; i<chapterSize; i++) {
            if (text[i] != ' ' && text[i] != '\t') {
                position = i;
                break;
           }
        }

        //Parse markup instructions
        {
            int start_markup_position = position;
            char current_char;

            if (text[start_markup_position] == '@') {
                if (chapterSize >= position + 3) {//at least 3 chars needed for valid markup, i.e. @{}
                    if (text[++start_markup_position] == '{') {
                        //so it evidently is markup starting point
                        //watchout for missing ending braces! or one might have the whole file scanned
                        current_char = text[++start_markup_position];

                        state = STATE_STYLING; //i.e. expecting styling definitions @{iIbBhH}

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
                                for (int i=start_markup_position; i < chapterSize; i++) {
                                    current_char = text[i];
                                    if (current_char == '}') {
                                        length = i - position+1;
                                        break;
                                    }
                                }
                                break;

                            case STATE_STYLING:
                                for (int i=start_markup_position; i < chapterSize; i++) {
                                    current_char = text[i];
                                    if (current_char == '}') {
                                        length = i - position+1;
                                        break;
                                    }

                                    switch(current_char) {
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
                                            enableLeftAlign = true;
                                            break;

                                        case 'R':
                                            enableRightAlign = true;
                                            break;

                                        case 'C':
                                            enableCenterAlign = true;
                                            break;

                                        case 'J':
                                            enableJustifyAlign = true;
                                            break;

                                        case 'l':
                                        case 'r':
                                        case 'c':
                                        case 'j':
                                            disableAlign = true;
                                            break;
                                    }
                                }
                                break;

                            case STATE_IMAGE:
                                imageURLPosition = ++start_markup_position;
                                boolean alt_text_found = false;
                                for (int i=start_markup_position; i < chapterSize; i++) {
                                    current_char = text[i];
                                    if (current_char == '}') {
										//no alt text provided
                                        imageURLLength = i-start_markup_position;
                                        length = i - position+1;
                                        break;
                                    }

                                    if (current_char == ':') {
										//alt text follows
                                        alt_text_found = true;
                                        imageURLLength = i-imageURLPosition;
                                        start_markup_position = i+1;
                                        break;
                                    }
                                }
                                
                                if (alt_text_found) {
                                    imageTextPosition = start_markup_position;
                                    for (int i=start_markup_position; i < chapterSize; i++) {
                                        current_char = text[i];
                                        if (current_char == '}') {
                                            imageTextLength = i - start_markup_position;
                                            length = i - position+1;
                                            break;
                                        }
                                    }
                                }
                                break;
                        }
                        return;
                    }
                }
            }
        } //end of parsing markup

		//parsing normal text; stopping at stop-chars or end of textbuffer
        for (int i = position; i<chapterSize; i++) {
            if (
                    text[i] == ' '  ||
                    text[i] == '\n' ||
                    text[i] == '\t' ||
                    text[i] == '\r' ||
                   (text[i] == '@'  && i+2 < chapterSize && text[i+1] == '{')
            ) {
                length = i - position;
                return;
            }
        }
		
        //TODO: next line MAY BE BUGGY
        //that's the last word in the chapter
        length = chapterSize - position;
    }
}