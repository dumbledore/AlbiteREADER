/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Albus Dumbledore
 */
public class ImageButton {
    final public static int TASK_NONE           = 0;
    final public static int TASK_MENU           = 1;
    final public static int TASK_LIBRARY        = 2;
    final public static int TASK_DICTIONARY     = 3;
    final public static int TASK_FONTSIZE       = 4;
    final public static int TASK_COLORPROFILE   = 5;

    String name;
    int task;
    
    int[] ARGBdata;

    protected int   width;
    protected int   height;

    public int x,y;

    public ImageButton(String sURL, int task) throws IOException {
        this.name = sURL;
        this.task = task;
        
        //read image files
        
        InputStream in = this.getClass().getResourceAsStream(sURL);
        if (in == null)
            throw new IOException("Image does not exist: " + sURL);
        DataInputStream din = new DataInputStream(in);

        width  = din.readShort();
        height = din.readShort();

        ARGBdata = new int[width * height];

        //if EOF is risen, then probably the supplied dimensions from the
        //header were invalid!
        for (int i=0; i<width * height; i++)
            ARGBdata[i] = din.readByte() << 24;

        din.close();
    }

    public boolean buttonPressed(int x, int y) {
        return (x >= this.x &&
                y >= this.y &&
                x < this.x + this.width &&
                y < this.y + this.height
            );
    }

    public void draw(Graphics g, int x, int y) {
        g.drawRGB(ARGBdata, 0, width, x, y, width, height, true);
    }

    public void setColor(int color) {
        color = color & 0xFFFFFF;
        for (int i=0; i<ARGBdata.length; i++)
            ARGBdata[i] = (ARGBdata[i] & 0xFF000000) + color;
    }
}
