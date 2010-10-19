/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.html;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public interface HTMLSubstitues {
    /*
     * Using specially reserved chars from the BMP
     */
    public static final char    START_TAG_CHAR      = '\ufdd0';
    public static final int     START_TAG_INT       = 0xfdd0;

    public static final char    END_TAG_CHAR        = '\ufdd1';
    public static final int     END_TAG_INT         = 0xfdd1;
}