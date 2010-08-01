package org.albite.book.elements;

import java.util.Vector;
import org.albite.albite.BookCanvas;
import org.albite.albite.ColorProfile;
import org.albite.book.book.BookChapter;
import org.albite.font.BitmapFont;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextHyphenationInfo;

public class Page {

    protected int start;
    protected int end; //start+length, i.e. character is in page if start <= char_pos < end
    
    int pageMode;
    final public static int PAGE_MODE_NORMAL                        = 0;
    final public static int PAGE_MODE_LEAVES_CHAPTER_NEXT           = 1;
    final public static int PAGE_MODE_LEAVES_CHAPTER_PREV           = 2;
    final public static int PAGE_MODE_LEAVES_CHAPTER_END_OF_BOOK    = 3;
    final public static int PAGE_MODE_LEAVES_CHAPTER_START_OF_BOOK  = 4;
    final public static int PAGE_MODE_IMAGE                         = 5;

    final public static char[] LABEL_LEAVES_CHAPTER_NEXT            = "Next chapter".toCharArray();
    final public static char[] LABEL_LEAVES_CHAPTER_PREV            = "Previous chapter".toCharArray();
    final public static char[] LABEL_LEAVES_CHAPTER_END_OF_BOOK     = "End of book".toCharArray();
    final public static char[] LABEL_LEAVES_CHAPTER_START_OF_BOOK   = "Start of book".toCharArray();

    public Page prevPage;
    public Page nextPage;

    protected Vector regions;

    final public static int ALIGN_LEFT      = 0;
    final public static int ALIGN_RIGHT     = 1;
    final public static int ALIGN_JUSTIFY   = 2;
    final public static int ALIGN_CENTER    = 3;
    
    //next settings must be overwritten if bookcanvas assigns new value
    final public static Vector IMAGES_QUEUE_POOL = new Vector(8);
    public static int DEFAULT_LINE_SPACING = 2;
    public static int DEFAULT_ALIGN = ALIGN_JUSTIFY;
    public static int FONT_INDENT;
    public static int WIDTH;
    public static int HEIGHT;
    public static BitmapFont FONT_PLAIN;
    public static BitmapFont FONT_ITALIC;

    //settings mediated through pages, i.e. next page is build upon THESE
    public RegionTextHyphenated lastHyphenatedWord;
    public int align = DEFAULT_ALIGN;
    public boolean bold, italic, heading;
    public boolean startsNewParagraph = true;

    public Page(BookChapter chapter, boolean next) { //dummy page
        
        if (next) {
            start = end = chapter.getSize();
            if (chapter.getNextChapter() != null) {
                pageMode = PAGE_MODE_LEAVES_CHAPTER_NEXT;
            } else {
                pageMode = PAGE_MODE_LEAVES_CHAPTER_END_OF_BOOK;
            }
        } else {
            start = end = 0;
            if (chapter.getPrevChapter() != null) {
                pageMode = PAGE_MODE_LEAVES_CHAPTER_PREV;
            } else {
                pageMode = PAGE_MODE_LEAVES_CHAPTER_START_OF_BOOK;
            }
        }
    }

    public Page(char[] buffer, int bufferSize, int pos, Page justAdded) {

        int fontHeight = FONT_PLAIN.lineHeight + DEFAULT_LINE_SPACING;

        bold = justAdded.bold;
        italic = justAdded.italic;
        heading = justAdded.heading;
        lastHyphenatedWord = justAdded.lastHyphenatedWord;
        align = justAdded.align;
        startsNewParagraph = justAdded.startsNewParagraph;

        boolean textMode = true;
        RegionImage ri = null;

        if (IMAGES_QUEUE_POOL.isEmpty()) {
            //normal mode
            this.pageMode = PAGE_MODE_NORMAL;
            this.lastHyphenatedWord = justAdded.lastHyphenatedWord;
            regions = new Vector(200);
            this.start    = pos;
        } else {
            //image mode
            this.pageMode = PAGE_MODE_IMAGE;
            this.lastHyphenatedWord = null;
            ri = (RegionImage)IMAGES_QUEUE_POOL.firstElement();
            IMAGES_QUEUE_POOL.removeElementAt(0);
            
            bufferSize = ri.altTextBufferPosition + ri.altTextBufferLength;
            regions = new Vector(40);
            this.start = pos = ri.altTextBufferPosition;

            textMode = false;
        }
        
        WordInfo wordInfo = new WordInfo();
        int wordPixelWidth; //word width in pixels

        Vector wordsOnThisLine = new Vector(20); //RegionTexts

        boolean firstWord;

        int posX = 0;
        int posY = 0;

        if (pageMode == PAGE_MODE_IMAGE) {
            regions.addElement(ri);
            posY = (((ri.y + ri.height) / fontHeight) +1) * fontHeight;
        }


        boolean lastLine = false;
        boolean firstLine = true;
        boolean lineBreak = false;
        boolean doNotAddNextLine = false;

        BitmapFont font = updateStylingFont();
        byte color = updateStylingColor();

        fw_page:
            while(true) {
                if (posY >= HEIGHT - fontHeight)
                    break; //the page has been filled up

                if (posY >= HEIGHT - (2*fontHeight)) {
                    lastLine = true;
                }

                posX = 0; //posX is in pixels; pos is in chars
                firstWord = true;

                wordsOnThisLine.removeAllElements(); //cache

                if (startsNewParagraph) {
                    posX = FONT_INDENT;
                }
                
                fw_line:
                    while(true) {

                        if (pos >= bufferSize)
                            break fw_page; //no more chars to read

                        wordInfo.parseNext(buffer, pos, bufferSize);

                        final int state = wordInfo.state;
                        switch(state) {
                            case WordInfo.STATE_NEW_LINE: //linebreak
                                pos = wordInfo.position + wordInfo.length; //i.e. the length is one
                                if (doNotAddNextLine) {
                                    doNotAddNextLine = false;
                                    continue fw_line;
                                }

                                int startingPoint = 0;
                                if (startsNewParagraph)
                                    startingPoint = FONT_INDENT;

                                if (!firstLine || posX > startingPoint) {
                                    lineBreak = true;
                                    break fw_line;
                                } else {
                                    //don't start page with blank lines
                                    continue fw_line;
                                }

                            case WordInfo.STATE_STYLING:
                                pos = wordInfo.position + wordInfo.length;

                                //enable styling
                                if (wordInfo.enableBold)
                                    bold = true;

                                if (wordInfo.enableItalic)
                                    italic = true;

                                if (wordInfo.enableHeading) {
                                    heading = true;
                                }

                                if (wordInfo.enableLeftAlign)
                                    align = ALIGN_LEFT;

                                if (wordInfo.enableRightAlign)
                                    align = ALIGN_RIGHT;

                                if (wordInfo.enableCenterAlign)
                                    align = ALIGN_CENTER;

                                if (wordInfo.enableJustifyAlign)
                                    align = ALIGN_JUSTIFY;
                                
                                //disable styling
                                if (wordInfo.disableBold)
                                    bold = false;

                                if (wordInfo.disableItalic)
                                    italic = false;

                                if (wordInfo.disableHeading) {
                                    heading = false;
                                }

                                if (wordInfo.disableAlign)
                                    align = DEFAULT_ALIGN;

                                //set font
                                font = updateStylingFont();
                                color = updateStylingColor();
                                continue fw_line;
                                
                            case WordInfo.STATE_IMAGE:
                                //TODO
                                pos = wordInfo.position + wordInfo.length;
                                RegionImage ri_ = new RegionImage(new String(buffer, wordInfo.imageURLPosition, wordInfo.imageURLLength), wordInfo.imageTextPosition, wordInfo.imageTextLength);
                                ri_.x = (short)((WIDTH-ri_.width)/2);
                                IMAGES_QUEUE_POOL.addElement(ri_);
                                doNotAddNextLine = true;
                                continue fw_line;

                            case WordInfo.STATE_SEPARATOR:
                                //TODO
                                pos = wordInfo.position + wordInfo.length;
                                break fw_line;

                            case WordInfo.STATE_RULER:
                                //TODO
                                pos = wordInfo.position + wordInfo.length;
                                regions.addElement(new RegionLineSeparator((short)0, (short)posY, (short)WIDTH, (short)fontHeight, RegionLineSeparator.TYPE_RULER, ColorProfile.CANVAS_TEXT_COLOR));
                                break fw_line;
                        }

                        wordPixelWidth = font.charsWidth(buffer, wordInfo.position, wordInfo.length);
                        if (!firstWord)
                            posX += font.spaceWidth;

                        if (wordPixelWidth + posX <= WIDTH) {
                            //word FITS on the line without need to split it

                            //if a hyphenated word chain was being build
                            //this is the last chunk of it
                            if (lastHyphenatedWord != null) {
                                RegionTextHyphenated rt = new RegionTextHyphenated((short)0, (short)0, (short)wordPixelWidth, (short)fontHeight, buffer, wordInfo.position, wordInfo.length, font, color, lastHyphenatedWord);
                                wordsOnThisLine.addElement(rt);
                                rt.buildLinks();
                                lastHyphenatedWord = null;
                            } else {
                                wordsOnThisLine.addElement(new RegionText((short)0, (short)0, (short)wordPixelWidth, (short)fontHeight, buffer, wordInfo.position, wordInfo.length, font, color));
                            }
                            pos = wordInfo.position + wordInfo.length;
                            posX += wordPixelWidth;
                            firstWord = false;
                        } else {
                            int dashWidth = font.dashWidth;

                            //try to hyphenate word
                            ZLTextHyphenationInfo info = BookCanvas.getCurrentBook().getHyphenator().getInfo(buffer, wordInfo.position, wordInfo.length);

                            //try to hyphenate word so that the largest possible chunk is on this line
                            for(int i = wordInfo.length -2; i > 0; i--) { //-2 - starts from one before the last
                                if (info.isHyphenationPossible(i)) {
                                    wordPixelWidth = font.charsWidth(buffer, wordInfo.position, i) + dashWidth;

                                    if (wordPixelWidth < WIDTH-posX) {
                                        if (buffer[wordInfo.position + i] == '-') {
                                            //if word chunk already ends
                                            //with a dash, include it
                                            i++;
                                        }
                                        RegionTextHyphenated rt = new RegionTextHyphenated((short)0, (short)0, (short)wordPixelWidth, (short)fontHeight, buffer, wordInfo.position, i, font, color, lastHyphenatedWord);
                                        wordsOnThisLine.addElement(rt);
                                        lastHyphenatedWord = rt;
                                        pos = wordInfo.position + i;
                                        posX += wordPixelWidth;
                                        firstWord = false;
                                        //word hyphented
                                        break fw_line;
                                    }
                                }
                            }

                            //word could not be hyphenated. Could it fit into a single line at all?
                            if (font.charsWidth(buffer, wordInfo.position, wordInfo.length) > WIDTH) {//, style) > width) {
                                //that is a very nasty word that neither hyphenates nor does it fit at all on a single line
                                //So one should force hyphanation on it!
                                for(int i = wordInfo.length -2; i > 0; i--) {
                                    wordPixelWidth = font.charsWidth(buffer, wordInfo.position, i) + dashWidth;
                                    if (wordPixelWidth < WIDTH-posX) {
                                        if (buffer[wordInfo.position + i] == '-') {
                                            // if word chunk already ends
                                            //with a dash, include it
                                            i++;
                                        }
                                        RegionTextHyphenated rt = new RegionTextHyphenated((short)0, (short)0, (short)wordPixelWidth, (short)fontHeight, buffer, wordInfo.position, i, font, color, lastHyphenatedWord);
                                        wordsOnThisLine.addElement(rt);
                                        lastHyphenatedWord = rt;
                                        pos = wordInfo.position + i;
                                        posX += wordPixelWidth;
                                        firstWord = false;
                                        //word forcefully hyphenated
                                        break fw_line;
                                    }
                                }
                            }
                            //no other words were written on this line
                            break;
                        }

                        if (pos >= bufferSize) { //all the text could fit on one line (usually alt text for images)
                            if (wordsOnThisLine.size() > 0) {
                                positionWordsOnLine(wordsOnThisLine, WIDTH, posY, textMode, lineBreak, startsNewParagraph, align);
                                startsNewParagraph = false;
                                if (lineBreak)
                                    startsNewParagraph = true;
                                lineBreak = false;
                                break fw_page; //no more chars to read
                            }
                        }
                    }
                if (pos >= bufferSize)
                    lineBreak  = true;
                positionWordsOnLine(wordsOnThisLine, WIDTH, posY, textMode, lineBreak, startsNewParagraph, align);
                startsNewParagraph = false;
                if (lineBreak)
                    startsNewParagraph = true;
                lineBreak = false;

                if (lastLine) {
                    lastLine = false;
                    break;
                }
                posY += fontHeight;
                firstLine = false;
            }
        this.end = pos;
    }

    private void positionWordsOnLine(Vector words, int lineWidth, int lineY, boolean textMode, boolean lineBreak, boolean startsNewParagraph, int textAlign) {
        final int wordsSize = words.size();
        final int wordSpacing = FONT_PLAIN.spaceWidth;

        if (!textMode) {
            textAlign = ALIGN_CENTER;
        } else {
            if (lineBreak && textAlign == ALIGN_JUSTIFY) {
                textAlign = ALIGN_LEFT;
            }
        }

        if (wordsSize > 0) {
            int textWidth = 0;
            int x = 0;
            if (startsNewParagraph) {
                lineWidth = lineWidth - FONT_INDENT;
                x = FONT_INDENT;
            }
            for (int i=0; i<wordsSize; i++) {
                RegionText word = (RegionText)words.elementAt(i);
                textWidth += word.width; //compute width without spaces
            }

            int spacing = 0;

            //set spacing
            if (textAlign != ALIGN_JUSTIFY)
                spacing = wordSpacing;
            else
                //calculate spacing so words would be justified
                if (words.size() > 1)
                    spacing = (lineWidth - textWidth)/(wordsSize-1);

            if (textAlign == ALIGN_CENTER) {
                //calc X so that the block would be centered
                x = (lineWidth - (textWidth + (spacing * (wordsSize-1))))/2;
            }

            if (textAlign == ALIGN_RIGHT) {
                //right
                x = (lineWidth - (textWidth + (spacing * (wordsSize-1))));
            }

            for (int i=0; i<wordsSize; i++) {
                RegionText word = (RegionText)words.elementAt(i);

                word.x = (short)x;
                word.y = (short)lineY;

                x += word.width + spacing;

                regions.addElement(word);
            }
        }
    }

    private BitmapFont updateStylingFont() {
        if (italic)
            return FONT_ITALIC;
        return FONT_PLAIN;
    }

    private byte updateStylingColor() {
        byte color = ColorProfile.CANVAS_TEXT_COLOR;
        if (italic)
            color = ColorProfile.CANVAS_TEXT_ITALIC_COLOR;
        if (bold)
            color = ColorProfile.CANVAS_TEXT_BOLD_COLOR;
        if (heading)
            color = ColorProfile.CANVAS_TEXT_HEADING_COLOR;

        return color;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public int getMode() {
        return pageMode;
    }

    public boolean contains(int position) { //this way one can search for the page
        return start <= position && position < end;
    }

    public Region getRegionAt(int x, int y) {
        Region current = null;
        int regionsSize = regions.size();
        for (int i=0; i<regionsSize; i++) {
            current = (Region)regions.elementAt(i);
            if (current.containsPoint2D(x, y))
                return current;
        }
        return null;
    }

    public boolean isEmpty() {
        return regions.isEmpty();
    }
}