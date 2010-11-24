/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

/**
 *
 * @author albus
 */
class DecoderWindows_1254 extends SingleByteDecoder {

    private static DecoderWindows_1254 instance;

    private static final short[] MAP_1 = {
        /* 0x80 */
        0x20ac, (short) 0xfffd, 0x201a, 0x0192, 0x201e, 0x2026, 0x2020, 0x2021,
        0x02c6, 0x2030, 0x0160, 0x2039, 0x0152, (short) 0xfffd, (short) 0xfffd, (short) 0xfffd,
        /* 0x90 */
        (short) 0xfffd, 0x2018, 0x2019, 0x201c, 0x201d, 0x2022, 0x2013, 0x2014,
        0x02dc, 0x2122, 0x0161, 0x203a, 0x0153, (short) 0xfffd, (short) 0xfffd, 0x0178,
    };

    private static final short[] MAP_2 = {
        /* 0xd0 */
        0x011e, 0x00d1, 0x00d2, 0x00d3, 0x00d4, 0x00d5, 0x00d6, 0x00d7,
        0x00d8, 0x00d9, 0x00da, 0x00db, 0x00dc, 0x0130, 0x015e, 0x00df,
    };

    private static final short[] MAP_3 = {
        /* 0xf0 */
        0x011f, 0x00f1, 0x00f2, 0x00f3, 0x00f4, 0x00f5, 0x00f6, 0x00f7,
        0x00f8, 0x00f9, 0x00fa, 0x00fb, 0x00fc, 0x0131, 0x015f, 0x00ff,
    };

    private DecoderWindows_1254() {}

    public static AlbiteCharacterDecoder getInstance() {
        if (instance == null) {
            instance = new DecoderWindows_1254();
        }
        return instance;
    }

    public final int decode(int code) {
        if (code < 0x80) {
            return code;
        } else if (code < 0xa0) {
            code = MAP_1[code - 0x80] & 0xFFFF;
            if (code == 0xFFFD) {
                return SUBSTITUTE_CHAR;
            } else {
                return code;
            }
        } else if (code < 0xd0) {
            return code;
        } else if (code < 0xe0) {
            return MAP_2[code - 0xd0];
        } else if (code < 0xf0) {
            return code;
        } else {
            return MAP_3[code - 0xf0];
        }
    }

    public final String getEncoding() {
        return Encodings.WINDOWS_1254;
    }
}