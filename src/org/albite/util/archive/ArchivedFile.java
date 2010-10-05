/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util.archive;

import com.tinyline.util.GZIPInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.InputConnection;

/**
 *
 * @author Albus Dumbledore
 */
public class ArchivedFile
        implements InputConnection {
    protected Archive   archive;

    protected String    filename;
    protected int       position;
    protected int       size;
    protected boolean   compressed;

    public ArchivedFile(
            final Archive file,
            final String filename,
            final int position,
            final int size,
            final boolean compressed) {

        this.archive = file;
        this.filename = filename;
        this.position = position;
        this.size = size;
        this.compressed = compressed;
    }

    public final int fileSize() {
        return size;
    }

    public final int getPosition() {
        return position;
    }

    public final String getFileName() {
        return filename;
    }

    /**
     * Opens an input stream to the file.
     * Note that because of the underlying FileConnection,
     * only <b>one</b> input stream is allowed to be open at a time!
     *
     * @return  InputStream to the ArchivedFile.
     *          It will be decompressed, if necessary.
     *
     * @throws IOException
     */
    public final InputStream openInputStream() throws IOException {
        if (compressed) {
            return new GZIPInputStream(new ArchiveInputStream(this));
        } else {
            return new ArchiveInputStream(this);
        }
    }

    public final DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    /*
     * This doesn't do anything for it MUST NOT close the underlying
     * FileConnection which may be reaused across other ArchivedFile objects.
     */
    public final void close() {}
}