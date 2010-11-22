/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

/**
 *
 * @author albus
 */
class DecoderISO_8859_1 extends SingleByteDecoder {

    private static DecoderISO_8859_1 instance;

    private DecoderISO_8859_1() {}

    public static AlbiteCharacterDecoder getInstance() {
        if (instance == null) {
            instance = new DecoderISO_8859_1();
        }
        return instance;
    }

    public int decode(int code) {
        return code;
    }
}