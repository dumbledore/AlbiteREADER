/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java13.io.BufferedInputStream;

/**
 *
 * @author albus
 */
public class AlbiteStreamReader extends Reader {

    private final   InputStream             in;
    private         AlbiteCharacterDecoder  decoder;

    public AlbiteStreamReader(
            final InputStream in,
            final String encoding)
            throws IOException {

        setEncoding(encoding);
        
        if (decoder instanceof DecoderUTF_8) {
            if (!in.markSupported()) {
                this.in = new BufferedInputStream(in);
            } else {
                this.in = in;
            }
            skipBOM();
        } else {
            this.in = in;
        }
    }

    private void skipBOM() throws IOException {
        in.mark(10);

        int c0 = in.read();
        int c1 = in.read();
        int c2 = in.read();

        if (
                   c0 != 0xEF
                || c1 != 0xBB
                || c2 != 0xBF) {

            in.reset();
        }
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

    public static boolean encodingSupported(final String encoding) {
        return AlbiteCharacterDecoder.decoderAvailable(encoding);
    }
}