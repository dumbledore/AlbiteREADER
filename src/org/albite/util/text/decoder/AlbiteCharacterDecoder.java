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
public abstract class AlbiteCharacterDecoder {

    public static final int     DECODING_ERROR      = -1;
    public static final char    DEFAULT_CHAR        = '?';

    public static final String  DEFAULT_ENCODING    = "ASCII";

    public static final AlbiteCharacterDecoder DEFAULT_DECODER =
            new DecoderASCII();

    public static final AlbiteCharacterDecoder[] DECODERS =
            new AlbiteCharacterDecoder[] {
                DEFAULT_DECODER,
                new DecoderUTF_8(),
                new DecoderISO_8859_1(),
                new DecoderCP1251()
    };

    public abstract char[]
            decode(DataInput data, int length) throws IOException;

    public static AlbiteCharacterDecoder getDecoder(String encoding) {

        String[] acceptedEncodings;

        for (int i = 0; i < DECODERS.length; i++) {
            acceptedEncodings = DECODERS[i].getAcceptedEncodings();
            for (int j = 0; j < acceptedEncodings.length; j++) {
                if (encoding.equalsIgnoreCase(acceptedEncodings[j])) {
                    return DECODERS[i];
                }
            }
        }

        return DEFAULT_DECODER;
    }

    /*
     * See http://msdn.microsoft.com/en-us/library/aa752010(v=VS.85).aspx
     */
    public abstract String[] getAcceptedEncodings();

    public static char[] trim(final char[] text, final int size) {
        if (text == null || size >= text.length) {
            return text;
        }

        final char[] res = new char[size];
        System.arraycopy(text, 0, res, 0, size);
        return res;
    }
}