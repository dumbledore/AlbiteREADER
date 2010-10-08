/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author albus
 */
public class AlbiteStreamReader extends Reader {

    public static final String  DEFAULT_ENCODING =
            AlbiteCharacterDecoder.DEFAULT_DECODER.getEncoding();

    public static final String ENCODING_UTF_8 = DecoderUTF_8.ENCODING;
    public static final String ENCODING_ASCII = DecoderASCII.ENCODING;
    public static final String ENCODING_ISO_8859_1 = DecoderISO_8859_1.ENCODING;
    public static final String ENCODING_WINDOWS_1251 =
            DecoderWindows_1251.ENCODING;

    private AlbiteCharacterDecoder decoder;
    private InputStream in;

    public AlbiteStreamReader(
            final InputStream in, final String encoding)
            throws UnsupportedEncodingException {

        setEncoding(encoding);
        this.in = in;
    }

    public final void setEncoding(final String encoding)
            throws UnsupportedEncodingException {

        this.decoder = AlbiteCharacterDecoder.getDecoder(encoding);
    }

    public int read() throws IOException {
        return decoder.decode(in);
    }

    public int read(char[] cbuf, int off, int len) throws IOException {

        int read = 0;

        for (int i = 0; i < len; i++) {
            read = decoder.decode(in);

            if (read == -1) {
                return i;
            }

            cbuf[i + off] = (char) read;
        }

        return len;
    }

    public char[] read(int size) throws IOException {
        char[] buf = new char[size];
        int read = read(buf);

        if (read == -1) {
            return new char[0];
        }

        if (read == size) {
            return buf;
        }

        /*
         * Trim the array
         */
        char[] res = new char[read];
        System.arraycopy(buf, 0, res, 0, read);
        return res;
    }

    public void close() throws IOException {
        in.close();
    }
}