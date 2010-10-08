/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view.breaker;

import java.util.Vector;
import org.albite.book.model.element.*;
import org.albite.book.view.region.*;
import org.albite.font.AlbiteFont;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class LineBreaker {
    private final Element[]         elements;
    private final int               lineWidth;

    private final AlbiteFont        fontPlain;
    private final AlbiteFont        fontItalic;

    private final ZLTextTeXHyphenator hyphenator;

    /*
     * Precomputed values
     */
    private final int               spaceWidth;
    private final int               textLineHeight;
    private final int               indent;

    /*
     * Temporary values
     */
    private StyleElement            currentStyle = StyleElement.DEFAULT_STYLE;
    private AlbiteFont              font;
    private HyphenatedTextRegion    lastHyphenatedWord = null;

    private int                     elementIndex    = 0;

    /*
     * Result elements
     */
    public int                      height          = 0;
    public Region[]                 regions         = null;
    public int                      breakAfter      = WordBreaker.BREAK_NONE;
    public boolean                  vcentered       = false;

    public LineBreaker(
            final Element[] elements,
            final int lineWidth,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final ZLTextTeXHyphenator hyphenator,
            final int lineSpacing) {

        this.elements  = elements;
        this.lineWidth = lineWidth;

        this.fontPlain = fontPlain;
        this.fontItalic = fontItalic;
        this.hyphenator = hyphenator;

        spaceWidth = fontPlain.spaceWidth;
        textLineHeight = fontPlain.lineHeight + lineSpacing;
        indent = fontPlain.spaceWidth * 4; /* i.e. 4 spaces => tab */
    }

    public final int getHeight() {
        return height;
    }

    public final Region[] getRegions() {
        return regions;
    }

    public final int getBreakAfter() {
        return breakAfter;
    }

    public final boolean getVCentered() {
        return vcentered;
    }

    public final boolean next() {
        if (elementIndex >= elements.length) {
            return false;
        }

        int wordPixelWidth; //word width in pixels

        Vector wordsOnThisLine = new Vector(20); //RegionTexts

        boolean firstWord;

        int xpos = 0;

        return false;
    }
}