//#condition !(TinyMode || TinyModeExport)
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

/**
 *
 * @author albus
 */
class DecoderISO_8859_15 extends SingleByteDecoder {

    private static DecoderISO_8859_15 instance;

    private static final short[] MAP = {
        /* 0xa0 */
        0x00a0, 0x00a1, 0x00a2, 0x00a3, 0x20ac, 0x00a5, 0x0160, 0x00a7,
        0x0161, 0x00a9, 0x00aa, 0x00ab, 0x00ac, 0x00ad, 0x00ae, 0x00af,
        /* 0xb0 */
        0x00b0, 0x00b1, 0x00b2, 0x00b3, 0x017d, 0x00b5, 0x00b6, 0x00b7,
        0x017e, 0x00b9, 0x00ba, 0x00bb, 0x0152, 0x0153, 0x0178, 0x00bf,
    };

    private DecoderISO_8859_15() {}

    public static AlbiteCharacterDecoder getInstance() {
        if (instance == null) {
            instance = new DecoderISO_8859_15();
        }
        return instance;
    }

    public final int decode(int code) {
        if (code >= 0xa0 && code < 0xc0) {
            return MAP[code - 0xA0];
        } else {
            return code;
        }
    }

    public final String getEncoding() {
        return Encodings.ISO_8859_15;
    }
}