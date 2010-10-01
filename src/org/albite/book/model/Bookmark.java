/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model;

/**
 *
 * @author albus
 */
public class Bookmark {
    private Chapter chapter;
    private int     position;
    private String  text;

    protected Bookmark prev = null;
    protected Bookmark next = null;

    public Bookmark(Chapter chapter, int position, String text) {
        this.chapter = chapter;
        this.position = position;
        this.text = text;
    }

    public final Chapter getChapter() {
        return chapter;
    }

    public final int getPosition() {
        return position;
    }

    public final String getText() {
        return text;
    }

    public final void setText(String text) {
        this.text = text;
    }

    public final Bookmark getPrev() {
        return prev;
    }

    public final Bookmark getNext() {
        return next;
    }
}
