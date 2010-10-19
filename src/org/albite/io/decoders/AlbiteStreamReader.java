/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author albus
 */
public class AlbiteStreamReader
        extends Reader
        implements Encodings {

    private final   InputStream             in;
    private         AlbiteCharacterDecoder  decoder;

    public AlbiteStreamReader(
            final InputStream in,
            final String encoding)
            throws UnsupportedEncodingException {

        this.in = in;
        setEncoding(encoding);
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
                /*
                 * EOF
                 */
                return i;
            }

            cbuf[i + off] = (char) read;
        }

        return len;
    }

    /**
     * Creates a char array of the specified size,
     * tries to fill it, and trims it if less bytes have been read or
     * less characters have been produced (in the case of utf-8)
     * 
     * @param   how many bytes to read
     * @return  the characters read
     * @throws  IOException
     */
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

    public void mark(final int readlimit) {
        in.mark(readlimit);
    }

    public void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }
}