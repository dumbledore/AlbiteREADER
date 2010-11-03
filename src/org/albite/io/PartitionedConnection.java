/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.InputConnection;

/**
 *
 * @author albus
 */
public class PartitionedConnection implements InputConnection {
    final InputConnection input;
    final int position;
    final int size;

    public PartitionedConnection(
            final InputConnection input,
            final int position,
            final int size) {

        if (input == null || position < 0 || size < 1) {
            throw new IllegalArgumentException();
        }

        this.input = input;
        this.position = position;
        this.size = size;
    }

    public InputStream openInputStream() throws IOException {
        return new PartialInputStream(this);
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    public void close() throws IOException {
        input.close();
    }
}