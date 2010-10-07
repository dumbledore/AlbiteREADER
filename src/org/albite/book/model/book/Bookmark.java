/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

/**
 *
 * @author albus
 */
public class Bookmark {
    private Chapter     chapter;
    private int         position;
    private String      text;

    protected Bookmark  prev = null;
    protected Bookmark  next = null;

    public Bookmark(
            final Chapter chapter, final int position, final String text) {

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

    public final void setText(final String text) {
        this.text = text;
    }

    public final String getTextForHTML() {

        if (text.indexOf('<') == -1 && text.indexOf('>') == -1) {
            /*
             * No special chars here
             */
            return text;
        }

        /*
         * Need to process
         */
        final char[] cs = text.toCharArray();

        char c = 0;

        for (int i = 0; i < cs.length; i++) {
            c = cs[i];
            if (c == '<') {
                cs[i] = '[';
            } else if (c == '>') {
                cs[i] = ']';
            }
        }

        return new String(cs);
    }

    public final String getTextForList() {
        return "Ch. #" + (chapter.getNumber() + 1) + ": " + text;
    }

    public final Bookmark getPrev() {
        return prev;
    }

    public final Bookmark getNext() {
        return next;
    }
}