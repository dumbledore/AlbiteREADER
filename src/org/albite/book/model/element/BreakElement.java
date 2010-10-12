/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.element;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class BreakElement implements Element {
    /**
     * Defines a line break, if true.
     * Defines a page break, if false.
     */
    public final boolean lineBreak;

    public BreakElement(final boolean lineBreak) {
        this.lineBreak = lineBreak;
    }

    public final byte getType() {
        return BREAK;
    }
}
