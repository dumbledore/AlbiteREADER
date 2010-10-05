/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io;

/**
 *
 * @author albus
 */
public class DecoderISO_8859_1 extends SingleByteDecoder {

    private static DecoderISO_8859_1 instance = new DecoderISO_8859_1();

    public static final String ENCODING = "iso-8859-1";

    private DecoderISO_8859_1() {}

    public static AlbiteCharacterDecoder getInstance() {
        return instance;
    }

    public int decode(int code) {
        return code;
    }

    public String getEncoding() {
        return ENCODING;
    }
}