/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author albus
 */
abstract class AlbiteCharacterDecoder {

    public static final int     DECODING_DONE       = -1;

    /**
     * Used as a substitute, if there is a char that couldn't be read
     * right.
     */
    public static final char    SUBSTITUTE_CHAR     = '?';

    public static final AlbiteCharacterDecoder DEFAULT_DECODER =
            DecoderUTF_8.getInstance();

    public static final AlbiteCharacterDecoder[] DECODERS =
            new AlbiteCharacterDecoder[] {
        DecoderUTF_8.getInstance(),
    };

    public abstract int decode(InputStream in) throws IOException;

    public static AlbiteCharacterDecoder getDecoder(final String encoding)
            throws UnsupportedEncodingException {

        for (int i = 0; i < DECODERS.length; i++) {
            if (encoding.equalsIgnoreCase(DECODERS[i].getEncoding())) {
                return DECODERS[i];
            }
        }

        throw new UnsupportedEncodingException();
    }

    /*
     * See http://msdn.microsoft.com/en-us/library/aa752010(v=VS.85).aspx
     */
    public abstract String getEncoding();
}