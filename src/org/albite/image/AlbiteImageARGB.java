/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.image;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * This is a RAW image in ARGB format, so that it can have colour applied to it.
 * @author albus
 */
public class AlbiteImageARGB extends AlbiteImage {
    private final int[] argbData;
    private       int   color;

    /*
     * Image caching for rotation
     */
    private WeakReference   cache;
    private int             cacheColor;

    public AlbiteImageARGB(final InputStream in)
            throws IOException, AlbiteImageException {

        final DataInputStream din = new DataInputStream(in);

        loadHeader(din);

        argbData = new int[width * height];

        /* packing RAW data to alpha channel */
        for (int i=0; i<width * height; i++) {
            argbData[i] = din.readByte() << 24;
        }

        din.close();
    }

    /* Sets the color tone of the monochromatic image */
    public final void setColorTone(int colorTone) {
        color = colorTone & 0xFFFFFF;
        for (int i = 0; i < argbData.length; i++) {
            argbData[i] = (argbData[i] & 0xFF000000) + color;
        }
    }

    public final int[] getData() {
        return argbData;
    }

    public final void draw(final Graphics g, final int x, final int y) {
        g.drawRGB(
                argbData, 0, width,
                x, y, width, height, true);
    }

    public final void drawRotated(
            final Graphics g,
            final int x, final int y,
            final int orientation) {

        if (orientation == Sprite.TRANS_NONE) {
            cache = null;
            draw(g, x, y);
        } else {
            g.drawRegion(getCache(), 0, 0, width, height, orientation, x, y,
                    Graphics.LEFT | Graphics.TOP);
        }
    }

    private Image getCache() {
        WeakReference   cache = this.cache;

        if (cache != null) {
            Image image = (Image) cache.get();
            if (image != null) {
                if (cacheColor == color) {
                    return image;
                }
            }
        }

        /*
         * Update the cache
         */
        Image image = Image.createRGBImage(argbData, width, height, true);
        cache = new WeakReference(image);

        return image;
    }
}