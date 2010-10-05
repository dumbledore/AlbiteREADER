/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.text.decoder;

/**
 *
 * @author albus
 */
public class DecoderISO_8859_1 extends SingleByteDecoder {

    public static final String[] ACCEPTED_ENCODINGS = new String[] {
        "iso-8859-1",
        "cp819", "csISOLatin1", "ibm819",
        "iso_8859-1", "iso_8859-1:1987", "iso8859-1", "iso-ir-100",
        "l1", "latin1"
    };

    public int decodeChar(int code) {
        return code;
    }

    public String[] getAcceptedEncodings() {
        return ACCEPTED_ENCODINGS;
    }
}