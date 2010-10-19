/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author albus
 */
public abstract class SingleByteDecoder extends AlbiteCharacterDecoder {

    public final int decode(final InputStream in) throws IOException {
        int code = in.read();
        if (code == -1) {
            return DECODING_DONE;
        } else {
            return decode(code);
        }
    }

    public abstract int decode(int code);
}