/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import org.albite.image.AlbiteImageARGB;
import org.albite.image.AlbiteImageException;

/**
 *
 * @author Albus Dumbledore
 */
public class ImageButton {

    private int task;
    
    private AlbiteImageARGB image;

    private int x,y;

    public ImageButton(String sURL, int task) {
        this.task = task;
        
        /* read image file */
        InputStream in = this.getClass().getResourceAsStream(sURL);
        if (in == null) {
            throw new RuntimeException("Image does not exist: " + sURL);
        }

        try {
            image = new AlbiteImageARGB(in);
        } catch (IOException ioe) {
            throw new RuntimeException("Could not load imagebutton.");
        } catch (AlbiteImageException aie) {
            throw new RuntimeException("Could not load imagebutton.");
        }
    }

    public boolean buttonPressed(int x, int y) {
        return (x >= this.x &&
                y >= this.y &&
                x < this.x + this.image.getWidth() &&
                y < this.y + this.image.getHeight()
            );
    }

    public void setColor(int color) {
        image.setColorTone(color);
    }

    public void draw(Graphics g, int x, int y) {
        g.drawRGB(image.getData(), 0, image.getWidth(), x, y, image.getWidth(), image.getHeight(), true);
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
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
