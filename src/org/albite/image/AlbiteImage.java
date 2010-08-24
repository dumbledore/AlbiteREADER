/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.image;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author albus
 */
public abstract class AlbiteImage {
    public static final int MAGIC_NUMBER = 1095516745;
    public static final String FILE_EXTENSION = ".ali";
    protected static final String INVALID_FILE_ERROR = "File not an image.";

    int width;
    int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void loadHeader(DataInputStream din)
            throws IOException, AlbiteImageException {

        if (din.readInt() != MAGIC_NUMBER) {
            throw new AlbiteImageException(INVALID_FILE_ERROR);
        }

        width  = din.readShort();
        height = din.readShort();

        /*
         * if EOF was risen, then probably the supplied dimensions from the
         * header were invalid!
         */
    }
}
