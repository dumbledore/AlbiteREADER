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
class DecoderISO_8859_9 extends SingleByteDecoder {

    private static DecoderISO_8859_9 instance;

    private static final short[] MAP = {
        /* 0xd0 */
        0x011e, 0x00d1, 0x00d2, 0x00d3, 0x00d4, 0x00d5, 0x00d6, 0x00d7,
        0x00d8, 0x00d9, 0x00da, 0x00db, 0x00dc, 0x0130, 0x015e, 0x00df,
        /* 0xe0 */
        0x00e0, 0x00e1, 0x00e2, 0x00e3, 0x00e4, 0x00e5, 0x00e6, 0x00e7,
        0x00e8, 0x00e9, 0x00ea, 0x00eb, 0x00ec, 0x00ed, 0x00ee, 0x00ef,
        /* 0xf0 */
        0x011f, 0x00f1, 0x00f2, 0x00f3, 0x00f4, 0x00f5, 0x00f6, 0x00f7,
        0x00f8, 0x00f9, 0x00fa, 0x00fb, 0x00fc, 0x0131, 0x015f, 0x00ff,
    };

    private DecoderISO_8859_9() {}

    public static AlbiteCharacterDecoder getInstance() {
        if (instance == null) {
            instance = new DecoderISO_8859_9();
        }
        return instance;
    }

    public final int decode(int code) {
        if (code < 0xd0) {
            return code;
        } else {
            return MAP[code - 0xD0];
        }
    }

    public final String getEncoding() {
        return Encodings.ISO_8859_9;
    }
}