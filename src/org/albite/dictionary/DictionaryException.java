//#condition !(TinyMode || TinyModeExport || LightMode || LightModeExport)
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.dictionary;

/**
 *
 * @author albus
 */
public class DictionaryException extends Exception {

    public DictionaryException() {
        super();
    }

    public DictionaryException(final String s) {
        super(s);
    }
}
