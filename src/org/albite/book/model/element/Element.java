/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.element;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public interface Element {

    public final byte BREAK     = 0;
    public final byte IMAGE     = 1;
    public final byte PRE       = 2;
    public final byte RULER     = 3;
    public final byte STYLE     = 4;
    public final byte TEXT      = 5;

    public byte getType();
}
