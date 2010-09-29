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
    private int leftToRead;

    protected ArchiveInputStream(ArchivedFile af) throws IOException {
        is = af.archive.file.openInputStream();
        skip(af.position);
        leftToRead = af.size;
    }

    public final int read() throws IOException {
        if (leftToRead > 0) {
            leftToRead--;
            return is.read();
        }

        return -1;
    }

    public final int available() throws IOException {
        return 0;
    }

    public final void close() throws IOException {
        is.close();
    }

    public final long skip(long left) throws IOException {
        while (left > 0) {
            left -= is.skip(left);
        }

        return left;
    }
}