/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.text.decoder;

import java.io.DataInput;
import java.io.IOException;

/**
 *
 * @author albus
 */
public class DecoderUTF_8 extends AlbiteCharacterDecoder {

    public static final String[] ACCEPTED_ENCODINGS = new String[] {
        "utf-8",
        "unicode-1-1-utf-8", "unicode-2-0-utf-8", "x-unicode-2-0-utf-8"
    };

    public final char[] decode(final DataInput in, final int utflen)
            throws IOException {

        final char[] out = new char[utflen];

        byte[] bytearr = new byte[utflen];
        int c, char2, char3;
        int count = 0;
        in.readFully(bytearr, 0, utflen);

        int pointer = 0;

        while (count < utflen) {

            c = (int) bytearr[count] & 0xff;

            switch (c >> 4) {

                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    count++;
                    out[pointer++] = (char) c;
                    break;

                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen) {
                        throw new DecoderException(trim(out, pointer));
                    }

                    char2 = (int) bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80) {
                        throw new DecoderException(trim(out, pointer));
                    }

                    out[pointer++] =
                            (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                break;

                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen) {
                        throw new DecoderException(trim(out, pointer));
                    }
                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        throw new DecoderException(trim(out, pointer));
                    }
                    
                    out[pointer++] = (char) (((c & 0x0F) << 12)
                            | ((char2 & 0x3F) << 6)
                            | ((char3 & 0x3F) << 0));
                    break;

                default:
                /* 10xx xxxx,  1111 xxxx */
                throw new DecoderException(trim(out, pointer));
            }
        }

        return trim(out, pointer);
        }

    public String[] getAcceptedEncodings() {
        return ACCEPTED_ENCODINGS;
    }
}