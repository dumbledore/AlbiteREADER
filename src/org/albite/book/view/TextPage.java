package org.albite.book.view;

import org.albite.book.parser.TextParser;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.util.archive.Archive;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextHyphenationInfo;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

public class TextPage extends Page {

    public static final byte    TYPE_TEXT   = 0;
    public static final byte    TYPE_IMAGE  = 1;

    private byte                type        = 0;

    private int                 start;

    /*
     * start+length, i.e. character is in page if start <= char_pos < end
     */
    private int                 end;

    protected Vector            regions;
 
    public TextPage(final Booklet booklet, final PageState ip) {
        this.booklet = booklet;

        final int width = booklet.width;
        final int height = booklet.height;

        // App Settings
        final byte defaultAlign = booklet.defaultAlign;
        final AlbiteFont fontPlain = booklet.fontPlain;
        final AlbiteFont fontItalic = booklet.fontItalic;
        final int spaceWidth = fontPlain.spaceWidth;
              int dashWidth  = 0;
        final int fontHeight = booklet.fontHeight;
        final int fontHeightX2 = 2 * fontHeight;
        final int fontIndent = booklet.fontIndent;
        final ZLTextTeXHyphenator hyphenator = booklet.hyphenator;

        // Chapter settings
        final char[] buffer = booklet.getTextBuffer();
        final int bufferSize;
        final Archive bookFile = booklet.bookArchive;
        final Vector images = ip.images;

        byte style;
        byte align;
        byte color;

        AlbiteFont font;

        HyphenatedTextRegion lastHyphenatedWord;
        boolean startsNewParagraph;

        int pos;

        TextParser parser = ip.parser;
        int wordPixelWidth; //word width in pixels

        Vector wordsOnThisLine = new Vector(20); //RegionTexts

        boolean firstWord;

        int posX = 0;
        int posY = 0;

        if (images.isEmpty()) {
            //text mode
            type = TYPE_TEXT;
            regions = new Vector(300);

            pos = end = start = ip.end;

            bufferSize = buffer.length;

            style = ip.style;
            align = ip.align;

            lastHyphenatedWord = ip.lastHyphenatedWord;
            startsNewParagraph = ip.startsNewParagraph;

        } else {
            //image mode
            type = TYPE_IMAGE;

            ImageRegion ri = (ImageRegion) images.firstElement();
            images.removeElementAt(0);

            regions = new Vector(40);
            regions.addElement(ri);

            posY = ri.y + ri.height + fontHeight / 2;

            bufferSize = ri.altTextBufferPosition + ri.altTextBufferLength;
            pos = end = start = ri.altTextBufferPosition;

            style = StylingConstants.ITALIC;
            align = StylingConstants.CENTER;

            lastHyphenatedWord = null;
            startsNewParagraph = true;
        }

        /*
         * Setup font & color, based on style value from previous page.
         */
        font = StylingConstants.chooseFont(fontPlain, fontItalic, style);
        color = StylingConstants.chooseTextColor(style);

        boolean lastLine = false;
        boolean firstLine = true;
        boolean lineBreak = false;
        boolean doNotAddNextLine = false;

        page:
            while (true) {

                /*
                 * There is no more space for new lines,
                 * so the page is done.
                 */
                if (posY >= height - fontHeight) {
                    break;
                }

                /*
                 * Check if it is the last line of the page
                 */
                if (posY >= height - (fontHeightX2)) {
                    lastLine = true;
                }

                /*
                 * NB: posX & posY are in pixels, pos is in chars.
                 */
                posX = 0;
                firstWord = true;

                /*
                 * Clear the cache that will hold all the elements on the line
                 */
                wordsOnThisLine.removeAllElements();

                /*
                 * Indent the line, if it starts a new paragraph.
                 */
                if (startsNewParagraph) {
                    posX = fontIndent;
                    /*
                     * This resets the alignment on every new paragraph
                     *
                     * This is a very useful precatuion against alignment
                     * errors. Thus, @{clrj} won't be used anymore.
                     */
                    if (type == TYPE_TEXT) {
                        align = defaultAlign;
                    }
                }

                line:
                    while (true) {

                        /* No more chars to read */
                        if (pos >= bufferSize) {
                            break page;
                        }

                        /*
                         * Parse on
                         */
                        parser.parseNext(pos, buffer, bufferSize);

                        /*
                         * Logic for possible parsing states.
                         */
                        final int state = parser.state;
                        switch (state) {
                            case TextParser.STATE_PASS:
                                pos = parser.position + parser.length;
                                continue line;

                            case TextParser.STATE_NEW_LINE: //linebreak
                                pos = parser.position + parser.length;

                                if (doNotAddNextLine) {
                                    doNotAddNextLine = false;
                                    continue line;
                                }

                                int startingPoint = 0;
                                if (startsNewParagraph) {
                                    startingPoint = fontIndent;
                                }

                                if (!firstLine || (posX > startingPoint)) {
                                    lineBreak = true;
                                    break line;
                                } else {
                                    /* don't start a page with blank lines */
                                    continue line;
                                }

                            case TextParser.STATE_STYLING:
                                pos = parser.position + parser.length;

                                /* enable styling */
                                if (parser.enableBold) {
                                    style |= StylingConstants.BOLD;
                                }

                                if (parser.enableItalic) {
                                    style |= StylingConstants.ITALIC;
                                }

                                if (parser.enableHeading) {
                                    style |= StylingConstants.HEADING;
                                }

                                if (parser.enableLeftAlign) {
                                    align = StylingConstants.LEFT;
                                }

                                if (parser.enableRightAlign) {
                                    align = StylingConstants.RIGHT;
                                }

                                if (parser.enableCenterAlign) {
                                    align = StylingConstants.CENTER;
                                }

                                if (parser.enableJustifyAlign) {
                                    align = StylingConstants.JUSTIFY;
                                }

                                /* disable styling */
                                if (parser.disableBold) {
                                    style &= ~StylingConstants.BOLD;
                                }

                                if (parser.disableItalic) {
                                    style &= ~StylingConstants.ITALIC;
                                }

                                if (parser.disableHeading) {
                                    style &= ~StylingConstants.HEADING;
                                }

                                /* setup font & color */
                                font = StylingConstants.chooseFont(fontPlain,
                                        fontItalic, style);
                                color = StylingConstants.chooseTextColor(style);
                                continue line;

                            case TextParser.STATE_IMAGE:
                                pos = parser.position + parser.length;

                                if (booklet.renderImages
                                        && bookFile != null) {
                                    ImageRegion ri = new ImageRegion(
                                            bookFile.getFile(new String(buffer,
                                            parser.imageURLPosition,
                                            parser.imageURLLength)),
                                            parser.imageTextPosition,
                                            parser.imageTextLength);
                                    ri.x = (short) ((width - ri.width) / 2);
                                    images.addElement(ri);
                                    doNotAddNextLine = true;
                                }

                                continue line;

                            case TextParser.STATE_SEPARATOR:
                                pos = parser.position + parser.length;

                                regions.addElement(
                                        new LineSeparatorRegion(
                                        (short) 0,
                                        (short) posY,
                                        (short) width,
                                        (short) font.lineHeight,
                                        LineSeparatorRegion.TYPE_SEPARATOR,
                                        ColorScheme.COLOR_TEXT));
                                break line;

                            case TextParser.STATE_RULER:
                                pos = parser.position + parser.length;

                                regions.addElement(
                                        new LineSeparatorRegion(
                                        (short) fontIndent,
                                        (short) posY,
                                        (short) width,
                                        (short) font.lineHeight,
                                        LineSeparatorRegion.TYPE_RULER,
                                        ColorScheme.COLOR_TEXT));
                                break line;

                            default:
                                /*
                                 * There is nothing to do. It must be
                                 * STATE_NORMAL
                                 */
                        }

                        wordPixelWidth = font.charsWidth(buffer,
                                parser.position, parser.length);

                        /*
                         * If it is not the first word, it will need a space
                         * before it.
                         */
                        if (!firstWord) {
                            posX += font.spaceWidth;
                        }

                        /*
                         * word FITS on the line without need to split it
                         */
                        if (wordPixelWidth + posX <= width) {

                            /*
                             * if a hyphenated word chain was being build,
                             * this is the <i>last</i> chunk of it
                             */
                            if (lastHyphenatedWord != null) {
                                HyphenatedTextRegion rt =
                                        new HyphenatedTextRegion(
                                        (short) 0, (short) 0,
                                        (short) wordPixelWidth,
                                        (short) fontHeight, parser.position,
                                        parser.length, style, color,
                                        lastHyphenatedWord);

                                /*
                                 * call RegionText.buildLinks() so that, the
                                 * chunks of text would be connected
                                 */
                                rt.buildLinks();
                                lastHyphenatedWord = null;

                                wordsOnThisLine.addElement(rt);
                            } else {

                                /*
                                 * Just add a whole word to the line
                                 */
                                wordsOnThisLine.addElement(
                                        new TextRegion((short) 0, (short) 0,
                                        (short) wordPixelWidth,
                                        (short) fontHeight, parser.position,
                                        parser.length, style, color));
                            }

                            pos = parser.position + parser.length;
                            posX += wordPixelWidth;
                            firstWord = false;
                        } else {

                            /*
                             * try to hyphenate word
                             */
                            dashWidth = font.dashWidth;

                            ZLTextHyphenationInfo info =
                                    hyphenator.getInfo(buffer,
                                    parser.position, parser.length);

                            /*
                             * try to hyphenate word, so that the largest
                             * possible chunk is on this line
                             */

                            /*
                             * wordInfo.length - 2: starts from one before
                             * the last
                             */
                            for (int i = parser.length - 2; i > 0; i--) {
                                if (info.isHyphenationPossible(i)) {
                                    wordPixelWidth = font.charsWidth(buffer,
                                            parser.position, i) + dashWidth;

                                    /*
                                     * This part of the word fits on the line
                                     */
                                    if (wordPixelWidth < width - posX) {

                                        /*
                                         * If the word chunk already ends with a
                                         * dash, include it.
                                         */
                                        if (buffer[parser.position + i]
                                                == '-') {
                                            i++;
                                        }

                                        HyphenatedTextRegion rt =
                                                new HyphenatedTextRegion(
                                                (short) 0, (short) 0,
                                                (short) wordPixelWidth,
                                                (short) fontHeight,
                                                parser.position, i, style,
                                                color, lastHyphenatedWord);
                                        wordsOnThisLine.addElement(rt);
                                        lastHyphenatedWord = rt;
                                        pos = parser.position + i;
                                        posX += wordPixelWidth;
                                        firstWord = false;

                                        /* the word was hyphented */
                                        break line;
                                    }
                                }
                            }

                            /*
                             * The word could not be hyphenated. Could it fit
                             * into a single line at all?
                             */
                            if (font.charsWidth(buffer, parser.position,
                                    parser.length) > width) {

                                /* This word neither hyphenates, nor does it
                                 * fit at all on a single line, so one should
                                 * force hyphanation on it!
                                 */
                                for (int i = parser.length - 2; i > 0; i--) {
                                    wordPixelWidth = font.charsWidth(buffer,
                                            parser.position, i) + dashWidth;

                                    if (wordPixelWidth < width - posX) {
                                        /*
                                         * If the word chunk already ends with a
                                         * dash, include it.
                                         */
                                        if (buffer[parser.position + i]
                                                == '-') {
                                            i++;
                                        }

                                        HyphenatedTextRegion rt =
                                                new HyphenatedTextRegion(
                                                (short) 0, (short) 0,
                                                (short) wordPixelWidth,
                                                (short) fontHeight,
                                                parser.position, i, style,
                                                color, lastHyphenatedWord);

                                        wordsOnThisLine.addElement(rt);
                                        lastHyphenatedWord = rt;
                                        pos = parser.position + i;
                                        posX += wordPixelWidth;
                                        firstWord = false;
                                        break line;
                                    }
                                }
                            }

                            /*
                             * The word could fit on a line, so will leave it
                             * for the next line, and won't add anything here.
                             */
                            break;
                        }

                        /*
                         * All the text could fit on one line. This is usually
                         * the case for alt text for images.
                         */
                        if (pos >= bufferSize) {
                            lineBreak = true;

                            if (wordsOnThisLine.size() > 0) {
                                positionWordsOnLine(wordsOnThisLine, width,
                                        posY, spaceWidth, fontIndent, lineBreak,
                                        startsNewParagraph, align);
                                startsNewParagraph = false;
                                if (lineBreak) {
                                    startsNewParagraph = true;
                                }
                                lineBreak = false;

                                /* no more chars to read */
                                break page;
                            }
                        }
                    }

                if (pos >= bufferSize) {
                    lineBreak  = true;
                }

                positionWordsOnLine(wordsOnThisLine, width, posY, spaceWidth,
                        fontIndent, lineBreak, startsNewParagraph, align);
                startsNewParagraph = false;

                if (lineBreak) {
                    startsNewParagraph = true;
                }

                lineBreak = false;

                if (lastLine) {
                    lastLine = false;
                    break;
                }
                posY += fontHeight;
                firstLine = false;
            }

        switch (type) {
            case TYPE_TEXT:
                /*
                 * save the params for the next page
                 */
                ip.end = this.end = pos;
                ip.style = style;
                ip.align = align;
                ip.lastHyphenatedWord = lastHyphenatedWord;
                ip.startsNewParagraph = startsNewParagraph;
                break;

            case TYPE_IMAGE:
                /*
                 * center vertically text & image
                 */
                final int offset = (height - posY - fontHeight) / 2;
                final int size = regions.size();
                for (int i = 0; i < size; i++) {
                    ((Region) regions.elementAt(i)).y += offset;
                }
                break;
        }
    }

    private void positionWordsOnLine(
            final Vector words,
                  int lineWidth,
            final int lineY,
            final int spaceWidth,
            final int fontIndent,
            final boolean endsParagraph,
            final boolean startsNewParagraph,
                  byte align) {

        final int wordsSize = words.size();
        final int wordSpacing = spaceWidth;

        if (endsParagraph && align == StylingConstants.JUSTIFY) {
            align = StylingConstants.LEFT;
        }

        if (wordsSize > 0) {
            int textWidth = 0;
            int x = 0;
            if (startsNewParagraph) {
                lineWidth = lineWidth - fontIndent;
                x = fontIndent;
            }

            for (int i = 0; i < wordsSize; i++) {
                TextRegion word = (TextRegion) words.elementAt(i);
                textWidth += word.width; //compute width without spaces
            }

            int spacing = 0;

            /* set spacing */
            if (align != StylingConstants.JUSTIFY) {
                spacing = wordSpacing;
            } else {
                /* calculate spacing so words would be justified */
                if (words.size() > 1) {
                    spacing = (lineWidth - textWidth)/(wordsSize-1);
                }
            }
            
            /* calc X so that the block would be centered */
            if (align == StylingConstants.CENTER) {
                x = (lineWidth - (textWidth + (spacing * (wordsSize-1))))/2;
            }

            /* align right */
            if (align == StylingConstants.RIGHT) {
                x = (lineWidth - (textWidth + (spacing * (wordsSize-1))));
            }

            for (int i=0; i<wordsSize; i++) {
                TextRegion word = (TextRegion)words.elementAt(i);

                word.x = (short)x;
                word.y = (short)lineY;

                x += word.width + spacing;

                regions.addElement(word);
            }
        }
    }

    public final int getStart() {
        return start;
    }

    public final int getEnd() {
        return end;
    }

    public final boolean contains(final int position) { //this way one can search for the page
        return start <= position && position < end;
    }

    public final Region getRegionAt(final int x, final int y) {
        Region current = null;
        int regionsSize = regions.size();
        for (int i = 0; i < regionsSize; i++) {
            current = (Region) regions.elementAt(i);
            if (current.containsPoint2D(x, y)) {
                return current;
            }
        }
        return null;
    }

    public final boolean isEmpty() {

        return regions.isEmpty();
    }

    public final void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] textBuffer) {
        final int regionSize = regions.size();

        /*
         * drawing regions in a normal page
         */
        for (int i = 0; i < regionSize; i++) {
            Region region = (Region) regions.elementAt(i);
            region.draw(g, cp, fontPlain, fontItalic, textBuffer);
        }
    }

    public final byte getType() {
        return type;
    }

    public final String getFirstWord(final char[] chapterBuffer) {

        final int size = regions.size();

        for (int i = 0; i < size; i++) {
            Region r = (Region) regions.elementAt(i);
            if (r instanceof TextRegion) {
                return ((TextRegion) r).getText(chapterBuffer);
            }
        }

        return "";
    }
}