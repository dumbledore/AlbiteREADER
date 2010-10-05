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
public abstract class SingleByteDecoder extends AlbiteCharacterDecoder {
    public char[] decode(final DataInput data, final int length)
            throws IOException {

        final char[] result = new char[length];
        int read;
        int pos = 0;
        int decoded;
        boolean decodedWithoutErrors = true;

        for (; pos < length; pos++) {

            read = data.readUnsignedByte();

            if (read == -1) {
                throw new DecoderException(trim(result, pos));
            }

            decoded = decodeChar(read);
            if (decoded == -1) {
                decodedWithoutErrors = false;
                result[pos] = DEFAULT_CHAR;
            } else {
                result[pos] = (char) decoded;
            }
        }

        if (pos < length) {
            /*
             * Something wasn't decoded, because read returned -1
             */
            throw new DecoderException(trim(result, pos));
        }

        return result;
    }

    public abstract int decodeChar(int code);
}