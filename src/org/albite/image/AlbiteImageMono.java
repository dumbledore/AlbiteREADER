/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.image;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a RAW image in monochrome for space and speed efficiency.
 * Colouring should be implemented manually.
 * @author albus
 */
public class AlbiteImageMono extends AlbiteImage {
    private final byte[] monoData;

    public AlbiteImageMono(InputStream in)
            throws IOException, AlbiteImageException {

        DataInputStream din = new DataInputStream(in);

        loadHeader(din);
        
        monoData = new byte[width * height];

        /* reading RAW data */
        din.readFully(monoData, 0, monoData.length);

        din.close();
    }

    public byte[] getData() {
        return monoData;
    }
}
