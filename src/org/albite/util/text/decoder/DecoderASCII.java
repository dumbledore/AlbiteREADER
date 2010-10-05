/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.text.decoder;

/**
 *
 * @author albus
 */
public class DecoderASCII extends SingleByteDecoder {

    public static final String[] ACCEPTED_ENCODINGS = new String[] {
        "us-ascii",
        "ANSI_X3.4-1968",
        "ANSI_X3.4-1986",
        "ascii",
        "cp367",
        "csASCII",
        "IBM367",
        "ISO_646.irv:1991",
        "ISO646-US",
        "iso-ir-6us"
    };

    public int decodeChar(int code) {
        if (code < 0x80) {
            return code;
        }

        return DECODING_ERROR;
    }

    public String[] getAcceptedEncodings() {
        return ACCEPTED_ENCODINGS;
    }
}