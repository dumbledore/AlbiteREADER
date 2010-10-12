/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.element;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class PreElement implements Element {

    public final boolean preformatted;

    public PreElement(final boolean preformatted) {
        this.preformatted = preformatted;
    }

    public final byte getType() {
        return PRE;
    }
}
