/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import org.albite.book.parser.TextParser;
import java.util.Vector;

/**
 *
 * @author albus
 */
public class PageState {
    int                     start;
    int                     end;

    byte                    style;
    byte                    align;

    HyphenatedTextRegion    lastHyphenatedWord;
    boolean                 startsNewParagraph = true;

    TextParser              parser;
    Vector                  images;

    public PageState(final byte defaultAlign, final TextParser parser) {
        start = end = 0;
        align = defaultAlign;
        style = 0;
        images = new Vector(8);
        this.parser = parser;
        parser.reset();
    }
}
