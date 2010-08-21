/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model;

import org.albite.book.model.Chapter;

/**
 *
 * @author albus
 */
public class Bookmark {
    private Chapter chapter;
    private int         position;
    private String      text;

    public Bookmark(Chapter chapter, int position, String text) {
        this.chapter = chapter;
        this.position = position;
        this.text = text;
    }

    public Chapter getChapter() {
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
