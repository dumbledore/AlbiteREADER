/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.image;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a RAW image in ARGB format, so that it can have colour applied to it.
 * @author albus
 */
public class AlbiteImageARGB extends AlbiteImage {
    private final int[] argbData;

    public AlbiteImageARGB(InputStream in) 
            throws IOException, AlbiteImageException {
        DataInputStream din = new DataInputStream(in);

        loadHeader(din);

        argbData = new int[width * height];

        /* packing RAW data to alpha channel */
        for (int i=0; i<width * height; i++) {
            argbData[i] = din.readByte() << 24;
        }

        din.close();
    }

    /* Sets the color tone of the monochromatic image */
    public void setColorTone(int color) {
        color = color & 0xFFFFFF;
        for (int i=0; i<argbData.length; i++)
            argbData[i] = (argbData[i] & 0xFF000000) + color;
    }

    public int[] getData() {
        return argbData;
    }
}
