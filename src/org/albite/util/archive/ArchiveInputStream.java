/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Albus Dumbledore
 */
class ArchiveInputStream extends InputStream {
    private final InputStream is;
    private final int start;

    private int availableForReading;

//    private int pos;

    protected ArchiveInputStream(ArchivedFile af) throws IOException {
        is = af.archive.fileData;
        start = af.getPosition();
        availableForReading = af.size;
        reset();
    }

    public int read() throws IOException {
        if (availableForReading > 0) {
            availableForReading--;
            return is.read();
        }
        return -1;
    }

    public int available() throws IOException {
        return 0;
    }

    public void close() throws IOException {
        /*
         * MUST NOT close the real stream, for it may be reused by another
         * ArchiveInputStream. This is an effect ot the fact that a single
         * FileConnection can have only ONE InputStream
         */
        //is.close();
    }

    public long skip(final long n) throws IOException {
        return is.skip(n);
    }

    public void reset() throws IOException {
        is.reset();
        is.skip(start);
    }
}
