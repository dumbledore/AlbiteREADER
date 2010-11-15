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
    private int x, y;
    
    public ImageButton(final String sURL, final int task) {
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

    public final boolean buttonPressed(final int x, final int y) {
        return (x >= this.x &&
                y >= this.y &&
                x < this.x + this.image.getWidth() &&
                y < this.y + this.image.getHeight()
            );
    }

    public final void setColor(final int color) {
        image.setColorTone(color);
    }

    public final void draw(final Graphics g, final int x, final int y) {
        image.draw(g, x, y);
    }

    public final void drawRotated(
            final Graphics g,
            final int x, final int y,
            final int orientation) {
        image.drawRotated(g, x, y, orientation);
    }

    public final int getWidth() {
        return image.getWidth();
    }

    public final int getHeight() {
        return image.getHeight();
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final void setX(final int x) {
        this.x = x;
    }

    public final void setY(final int y) {
        this.y = y;
    }

    public final int getTask() {
        return task;
    }
}
