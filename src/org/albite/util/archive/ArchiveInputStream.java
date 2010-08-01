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
    final protected InputStream is;
          protected int available;

    protected ArchiveInputStream(ArchivedFile af) throws IOException {
        is = af.archive.fileData;
        is.reset();
        is.skip(af.position);
        available = af.size;
    }

    public int read() throws IOException {
        if (available > 0) {
            available--;
            return is.read();
        }
        return -1;
    }

    public int available() throws IOException {
        return available;
    }

    public void close() throws IOException {
		//is.close();
        //MUST NOT close real stream, for it may be reused by another ArchiveInputStream
		//this is an effect ot the fact that a single FileConnection can
		//have only one InputStream
    }

    public long skip(long n) throws IOException {
        return is.skip(n);
    }
}
