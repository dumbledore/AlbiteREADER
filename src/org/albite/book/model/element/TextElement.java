/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.element;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class TextElement implements Element {
    public final char[] text;

    public TextElement(final char[] text) {
        this.text = text;
    }

    public byte getType() {
        return TEXT;
    }
}