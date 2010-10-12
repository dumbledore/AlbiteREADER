/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view.region;

import java.util.Vector;
import org.albite.albite.ColorScheme;
import org.albite.book.StyleConstants;
import org.albite.book.model.element.*;
import org.albite.font.AlbiteFont;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextHyphenationInfo;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class LineBuilder
        implements Breaks, StyleConstants {

    final int                       lineWidth;
    final ZLTextTeXHyphenator       hyphenator;

    /*
     * Precomputed values frequently used
     */
    private int                     spaceWidth;
    private int                     dashWidth;
    private int                     lineHeight;
    private int                     indent;

    /*
     * Input. Concerning text & style
     */
    private AlbiteFont              font;
    private boolean                 fontPlain;
    private byte                    colorIndex;
    private byte                    align;

    private TextElement             textel;
    private char[]                  textBuffer;

    private WordBuilder             wordBuilder;

    /*
     * Output. Concerning line.
     */
    private HyphenatedTextRegion    lastHyphenatedWord  = null;

    private Vector                  wordsOnThisLine     = new Vector(50);
    private int                     xpos;

    /**
     * Whether it's necessary to make a brake after the line
     */
    public  int                     breakAfter;
    private boolean                 startsNewParagraph;

    /**
     * Set to false when using text from some previous TextElement
     */
    private boolean                 resetLine           = false;

    public LineBuilder(
            final int lineWidth,
            final ZLTextTeXHyphenator hyphenator
            ) {

        this.lineWidth = lineWidth;
        this.hyphenator = hyphenator;

        colorIndex = ColorScheme.COLOR_TEXT;

        /*
         * Set default line values
         */
        startsNewParagraph = true;
        breakAfter = BREAK_NONE;
        xpos = indent;
    }

    public final Vector getLine() {
        return wordsOnThisLine;
    }

    public final void set(
            final AlbiteFont font,
            final boolean plainFont,
            final byte align,
            final byte colorIndex,
            final TextElement textel,
            final boolean preformattedText) {

        this.font = font;
        this.fontPlain = plainFont;
        this.align = align;
        this.colorIndex = colorIndex;
        this.textel = textel;
        this.textBuffer = textel.text;
        this.wordBuilder =
                new WordBuilder(
                textel.text, preformattedText);

        /*
         * Set initial values and compute constants
         */
        spaceWidth = font.spaceWidth;
        dashWidth = font.dashWidth;
        indent = font.spaceWidth * 4; /* i.e. 4 spaces => tab */

        /*
         * Don't reset the line, reuse it.
         */
        this.resetLine = false;
    }

    public final void resetLine() {
        resetLine = true;
    }

    /**
     * Builds next line
     * @return true if the builded line isn't last
     */
    public final boolean next() {
        /*
         * Building next line
         */

        /**
         * The width of the current word
         */
        int wordPixWidth = 0;
        int wordPos = 0;
        int wordLen = 0;

        if (resetLine) {

            wordsOnThisLine.removeAllElements();

            if (breakAfter == BREAK_NONE) {
                /*
                 * No breaks. Continue
                 */
                xpos = 0;
                startsNewParagraph = false;
            } else {
                xpos = indent;
                startsNewParagraph = true;
            }
        } else {
            resetLine = true;
        }

        line:
        while (true) {

            /*
             * Read next word
             */
            if (!wordBuilder.next()) {
                /*
                 * No more words to read from this element
                 */
                resetLine = false;
                return false;
            }

            breakAfter = wordBuilder.getBreakType();
            if (breakAfter != BREAK_NONE) {
                /*
                 * The line is ready
                 */
                return true;
            }

            wordPos = wordBuilder.getPosition();
            wordLen = wordBuilder.getLength();
            wordPixWidth = font.charsWidth(textBuffer, wordPos, wordLen);

            System.out.println("Word is: <" + new String(textel.text, wordPos, wordLen) + ">, BREAK: " + wordBuilder.getBreakType());

            if (wordPixWidth < 1) {
                /*
                 * Don't add zero-width words
                 */
                continue;
            }
            /*
             * If it is not the first word, one may add space(s) before it.
             */
            if (!wordsOnThisLine.isEmpty()) {
                xpos += font.spaceWidth * wordBuilder.getWhiteSpace();
            }

            /*
             * word fits on the line without need to split it
             */
            if (wordPixWidth + xpos <= lineWidth) {

                /*
                 * if a hyphenated word chain was being build,
                 * this is the last chunk of it
                 */
                if (lastHyphenatedWord != null) {
                    HyphenatedTextRegion rt =
                            new HyphenatedTextRegion(
                            textel,
                            (short) 0, (short) 0,
                            (short) wordPixWidth,
                            (short) lineHeight,
                            wordPos, wordLen,
                            fontPlain, colorIndex,
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
                            new TextRegion(
                            textel,
                            (short) 0, (short) 0,
                            (short) wordPixWidth,
                            (short) lineHeight,
                            wordPos, wordLen,
                            fontPlain, colorIndex));
                }

                xpos += wordPixWidth;
            } else {

                /*
                 * try to hyphenate word
                 */

                ZLTextHyphenationInfo info =
                        hyphenator.getInfo(textBuffer, wordPos, wordLen);

                /*
                 * try to hyphenate word, so that the largest
                 * possible chunk is on this line
                 */

                /*
                 * wordInfo.length - 2: starts from one before
                 * the last
                 */
                for (int i = wordLen - 2; i > 0; i--) {
                    if (info.isHyphenationPossible(i)) {
                        wordPixWidth =
                                font.charsWidth(textBuffer, wordPos, i)
                                + dashWidth;

                        /*
                         * This part of the word fits on the line
                         */
                        if (wordPixWidth < lineWidth - xpos) {

                            /*
                             * If the word chunk already ends with a
                             * dash, include it.
                             */
                            if (textBuffer[wordPos + i] == '-') {
                                i++;
                            }

                            HyphenatedTextRegion rt =
                                    new HyphenatedTextRegion(
                                    textel,
                                    (short) 0, (short) 0,
                                    (short) wordPixWidth,
                                    (short) lineHeight,
                                    wordPos, i,
                                    fontPlain, colorIndex,
                                    lastHyphenatedWord);
                            wordsOnThisLine.addElement(rt);
                            lastHyphenatedWord = rt;
                            xpos += wordPixWidth;

                            /*
                             * the word was hyphented
                             * proceed with the next one
                             */
                            break line;
                        }
                    }
                }

                /*
                 * The word could not be hyphenated. Could it fit
                 * into a single line at all?
                 */
                if (font.charsWidth(textBuffer, wordPos, wordLen) > lineWidth) {

                    /* This word neither hyphenates, nor does it
                     * fit at all on a single line, so one should
                     * force hyphanation on it!
                     */
                    for (int i = wordLen - 2; i > 0; i--) {
                        wordPixWidth = font.charsWidth(textBuffer, wordPos, i)
                                + dashWidth;

                        if (wordPixWidth < lineWidth - xpos) {
                            /*
                             * If the word chunk already ends with a
                             * dash, include it.
                             */
                            if (textBuffer[wordPos + i] == '-') {
                                i++;
                            }

                            HyphenatedTextRegion rt =
                                    new HyphenatedTextRegion(
                                    textel,
                                    (short) 0, (short) 0,
                                    (short) wordPixWidth, (short) lineHeight,
                                    wordPos, i,
                                    fontPlain, colorIndex,
                                    lastHyphenatedWord);

                            wordsOnThisLine.addElement(rt);
                            lastHyphenatedWord = rt;
                            xpos += wordPixWidth;
                            break line;
                        }
                    }
                }

                /*
                 * The word could fit on a line, so will leave it
                 * for the next line, and won't add anything here.
                 */
                break line;
            }

            /*
             * All the text could fit on one line. This is usually
             * the case for alt text for images.
             */
        }

        return true;
    }

    public final void positionWordsOnLine() {
//            final Vector words,
//                  int lineWidth,
//            final int spaceWidth,
//            final int fontIndent,
//            final boolean endsParagraph,
//            final boolean startsNewParagraph,
//                  byte align) {

        final int wordsSize = wordsOnThisLine.size();
        final int wordSpacing = spaceWidth;
        byte align_ = align;
        if (breakAfter != BREAK_NONE && align_ == JUSTIFY) {
            System.out.println("break after: " + breakAfter);
            align_ = LEFT;
        }

        System.out.println("Positioning, count:" + wordsSize + ", align: "
                + align_ + ", space: " + spaceWidth
                + "starts: " + startsNewParagraph
                );
        if (wordsSize > 0) {

            int width = lineWidth;
            int textWidth = 0;
            int x = 0;
            if (startsNewParagraph) {
                width -= indent;
                x = indent;
            }

            for (int i = 0; i < wordsSize; i++) {
                TextRegion word = (TextRegion) wordsOnThisLine.elementAt(i);
                textWidth += word.width; //compute width without spaces
            }

            int spacing = 0;

            /* set spacing */
            if (align_ != JUSTIFY) {
                spacing = wordSpacing;
            } else {
                /* calculate spacing so words would be justified */
                if (wordsOnThisLine.size() > 1) {
                    spacing = (width - textWidth)/(wordsSize-1);
                }
            }

            /* calc X so that the block would be centered */
            if (align_ == CENTER) {
                x = (width - (textWidth + (spacing * (wordsSize-1))))/2;
            }

            /* align right */
            if (align_ == RIGHT) {
                x = (width - (textWidth + (spacing * (wordsSize-1))));
            }

            for (int i=0; i<wordsSize; i++) {
                TextRegion word = (TextRegion) wordsOnThisLine.elementAt(i);

                word.x = (short) x;

                x += word.width + spacing;
            }
        }
    }
}