/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import org.albite.book.model.parser.TextParser;
import java.util.Vector;

/**
 *
 * @author albus
 */
public class PageState {
    int                     position;
    int                     length;

    byte                    style;
    boolean                 center;

    HyphenatedTextRegion    lastHyphenatedWord;
    boolean                 startsNewParagraph = true;

    TextParser              parser;
    Vector                  images;

    boolean                 bufferRead = false;

    public PageState(final TextParser parser) {
        position = 0;
        length = 0;
        center = false;
        style = 0;
        images = new Vector(8);
        this.parser = parser;
        parser.reset();
    }

    public boolean finishedReading() {
        return bufferRead && images.isEmpty();
    }
}
