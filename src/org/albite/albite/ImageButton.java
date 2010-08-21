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
    public static final int TASK_NONE           = 0;
    public static final int TASK_MENU           = 1;
    public static final int TASK_LIBRARY        = 2;
    public static final int TASK_DICTIONARY     = 3;
    public static final int TASK_FONTSIZE       = 4;
    public static final int TASK_COLORPROFILE   = 5;

    private String name;
    private int task;
    
    private int[] ARGBdata;

    private int width;
    private int height;

    private int x,y;

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
            //packing RAW data to alpha channel
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTask() {
        return task;
    }
}
