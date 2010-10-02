/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model;

/**
 *
 * @author albus
 */
public class BookmarkManager {
    private Bookmark head = null;
    private Bookmark tail = null;
    private int size = 0;

    public final int addBookmark(final Bookmark bookmark) {
        System.out.print("Adding bookmark `" + bookmark.getText() +
                " @ " + bookmark.getChapter().getNumber() + ":"
                + bookmark.getPosition() + "(" + size + ")...");

        size++;

        if (head == null) {
            /*
             * No elements found
             */

            head = bookmark;
            tail = bookmark;
            System.out.println("FIRST");
            return 0;
        }

        final int bookmarkChapNo = bookmark.getChapter().getNumber();
        final int bookmarkPos = bookmark.getPosition();

        int pos = 0;
        Bookmark current = head;
        int currentChapNo;

        while (current != null) {

            currentChapNo =  current.getChapter().getNumber();

            /*
             * Sequental search. Not CPU effective, but memory effective
             * as we are searching in a linked list
             */
            if (
                    bookmarkChapNo < currentChapNo
                    || (bookmarkChapNo == currentChapNo
                        && bookmarkPos < current.getPosition())
                    ) {
                if (current.prev == null) {
                    head = bookmark;
                } else {
                    bookmark.prev = current.prev;
                    current.prev.next = bookmark;
                }

                current.prev = bookmark;
                bookmark.next = current;

                System.out.println("MIDDLE");
                return pos;
            } else {
                System.out.println("Skipping " + current.getText());
            }

            pos++;
            current = current.next;
        }

        /*
         * It's going for the last place
         */
        tail.next = bookmark;
        bookmark.prev = tail;
        tail = bookmark;
        System.out.println("LAST");
        return pos;
    }

    public final Bookmark deleteBookmarkAt(final int pos) {

        Bookmark current = bookmarkAt(pos);

        if (current == null) {
            return null;
        }

        /*
         * deleting bookmark
         */
        size--;
        
        if (current.prev != null) {
            current.prev.next = current.next;
        } else {
            head = current.next;
        }

        if (current.next != null) {
            current.next.prev = current.prev;
        } else {
            tail = current.prev;
        }

        return current;
    }

    public final Bookmark bookmarkAt(final int pos) {
        if (head == null) {
            return null;
        }

        Bookmark current = head;
        for (int i = 0; i < pos && current != null; i++) {
            current = current.next;
        }

        return current;
    }

    public final void deleteAll() {
        head = null;
        tail = null;
    }

    public final Bookmark getFirst() {
        return head;
    }

    public final Bookmark getLast() {
        return tail;
    }

    public final int size() {
        return size;
    }
}
