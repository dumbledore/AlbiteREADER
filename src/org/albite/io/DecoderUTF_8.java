package org.albite.io;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author albus
 */
public class DecoderUTF_8 extends AlbiteCharacterDecoder {

    private static DecoderUTF_8 instance = new DecoderUTF_8();

    public static final String ENCODING = "utf-8";

    private DecoderUTF_8() {}

    public static AlbiteCharacterDecoder getInstance() {
        return instance;
    }

    public final int decode(final InputStream in) throws IOException {

        int char1, char2, char3;

        char1 = in.read();

        if (char1 == -1) {
            return DECODING_DONE;
        }

        switch (char1 >> 4) {

            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                /* 0xxxxxxx*/
                return char1;

            case 12: case 13:
                /* 110x xxxx   10xx xxxx*/
                char2 = in.read();
                if (char2 == -1) {
                    return SUBSTITUTE_CHAR;
                }

                if ((char2 & 0xC0) != 0x80) {
                    return SUBSTITUTE_CHAR;
                }

                return (((char1 & 0x1F) << 6) | (char2 & 0x3F));

            case 14:
                /* 1110 xxxx  10xx xxxx  10xx xxxx */
                char2 = in.read();
                if (char2 == -1) {
                    return SUBSTITUTE_CHAR;
                }

                char3 = in.read();
                if (char3 == -1) {
                    return SUBSTITUTE_CHAR;
                }

                if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                    return SUBSTITUTE_CHAR;
                }

                return (  ((char1 & 0x0F) << 12)
                        | ((char2 & 0x3F) << 6 )
                        | ((char3 & 0x3F)      ));

            default:
                /* 10xx xxxx,  1111 xxxx */
                return SUBSTITUTE_CHAR;
        }
    }

    public String getEncoding() {
        return ENCODING;
    }
}