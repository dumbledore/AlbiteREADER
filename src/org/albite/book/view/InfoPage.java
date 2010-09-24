/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.view;

import java.util.Vector;

/**
 *
 * @author albus
 */
public class InfoPage {
    int start;
    int end;

    byte style;
    byte align;
    
    RegionTextHyphenated lastHyphenatedWord;
    boolean startsNewParagraph = true;

    InfoWord word;
    Vector images;

    public InfoPage(byte defaultAlign) {
        start = end = 0;
        align = defaultAlign;
        style = 0;
        word = new InfoWord();
        images = new Vector(8);
    }
}
