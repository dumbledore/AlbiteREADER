/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

/**
 *
 * @author albus
 */
class DecoderWindows_1252 extends SingleByteDecoder {

    private static DecoderWindows_1252 instance;

    private static final short[] MAP = {
        /* 0x80 */
        0x20ac, (short) 0xfffd, 0x201a, 0x0192, 0x201e, 0x2026, 0x2020, 0x2021,
        0x02c6, 0x2030, 0x0160, 0x2039, 0x0152, (short) 0xfffd, 0x017d, (short) 0xfffd,
        /* 0x90 */
        (short) 0xfffd, 0x2018, 0x2019, 0x201c, 0x201d, 0x2022, 0x2013, 0x2014,
        0x02dc, 0x2122, 0x0161, 0x203a, 0x0153, (short) 0xfffd, 0x017e, 0x0178,
    };

    private DecoderWindows_1252() {}

    public static AlbiteCharacterDecoder getInstance() {
        if (instance == null) {
            instance = new DecoderWindows_1252();
        }
        return instance;
    }

    public final int decode(int code) {
        if (code < 0x80 || code >= 0xA0) {
            return code;
        } else {
            code = MAP[code - 0x80] & 0xFFFF;
            if (code == 0xFFFD) {
                return SUBSTITUTE_CHAR;
            } else {
                return code;
            }
        }
    }
}