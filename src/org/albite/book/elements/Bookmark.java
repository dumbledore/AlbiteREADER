/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.elements;

import org.albite.book.book.BookChapter;

/**
 *
 * @author albus
 */
public class Bookmark {
    private BookChapter chapter;
    private int         position;
    private String      text;

    public Bookmark(BookChapter chapter, int position, String text) {
        this.chapter = chapter;
        this.position = position;
        this.text = text;
    }

    public BookChapter getChapter() {
        return chapter;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
