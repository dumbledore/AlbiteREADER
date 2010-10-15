/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.image;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author albus
 */
public abstract class AlbiteImage {
    public static final int         MAGIC_NUMBER        = 1095516745;
    public static final String      FILE_EXTENSION      = ".ali";
    protected static final String   INVALID_FILE_ERROR  = "File not an image.";

    int width;
    int height;

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    protected final void loadHeader(final DataInputStream din)
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

    public static int[] getPNGDimensions(final DataInputStream din)
            throws IOException {
        /*
         * skipping PNG header
         */
        din.skipBytes(16);

        final int[] result = {0, 0};

        result[0] = din.readInt();
        result[1] = din.readInt();

        din.close();

        return result;
    }


    public static Image rescale(
            final Image image, final int thumbWidth, final int thumbHeight) {

        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();

        Image thumb = Image.createImage(thumbWidth, thumbHeight);
        Graphics g = thumb.getGraphics();

        int dx, dy;
        int anchor = Graphics.LEFT | Graphics.TOP;

        for (int y = 0; y < thumbHeight; y++) {
            for (int x = 0; x < thumbWidth; x++) {
                g.setClip(x, y, 1, 1);
                dx = x * sourceWidth / thumbWidth;
                dy = y * sourceHeight / thumbHeight;
                g.drawImage(image, x - dx, y - dy, anchor);
            }
        }

        return thumb;
    }
}