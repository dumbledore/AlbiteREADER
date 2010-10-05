/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io;

/**
 *
 * @author albus
 */
public class DecoderASCII extends SingleByteDecoder {

    private static DecoderASCII instance = new DecoderASCII();

    public static final String ENCODING = "us-ascii";

    private DecoderASCII() {}

    public static AlbiteCharacterDecoder getInstance() {
        return instance;
    }

    public int decode(int code) {
        if (code < 0x80) {
            return code;
        }

        return SUBSTITUTE_CHAR;
    }

    public String getEncoding() {
        return ENCODING;
    }
}