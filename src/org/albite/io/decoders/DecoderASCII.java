/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

/**
 *
 * @author albus
 */
class DecoderASCII extends SingleByteDecoder {

    private static DecoderASCII instance;

    private DecoderASCII() {}

    public static AlbiteCharacterDecoder getInstance() {
        if (instance == null) {
            instance = new DecoderASCII();
        }
        return instance;
    }

    public int decode(int code) {
        if (code < 0x80) {
            return code;
        }

        return SUBSTITUTE_CHAR;
    }
}