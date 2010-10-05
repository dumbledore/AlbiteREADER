/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.text.decoder;

import java.io.IOException;

/**
 *
 * @author albus
 */
public class DecoderException extends IOException {
    protected char[] text = null;

    public DecoderException() {}

    public DecoderException(final char[] text) {
        this.text = text;
    }

    public final char[] getBuffer() {
        return text;
    }
}