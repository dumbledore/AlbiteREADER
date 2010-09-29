package org.albite.book.view;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.util.archive.ArchivedFile;

class RegionImage extends Region {

    public static short VERTICAL_MARGIN = 10;

    ArchivedFile af;
    public int altTextBufferPosition;
    public int altTextBufferLength;
   
    public RegionImage(ArchivedFile af, int altTextBufferPosition, int altTextBufferLength) {
        super((short)0, (short)VERTICAL_MARGIN, (short)48, (short)(48+VERTICAL_MARGIN));
        this.af = af;
        this.altTextBufferPosition = altTextBufferPosition;
        this.altTextBufferLength = altTextBufferLength;

        if (af != null) {
            //file found
            try {
                //read dimensions from PNG header
                int[] dimensions = af.getAsImageDimensions();
                width = (short) dimensions[0];
                height = (short) (dimensions[1] + VERTICAL_MARGIN);
            } catch (IOException ioe) {
                //couldn't load image
                ioe.printStackTrace();
            }
        }
    }

    public void draw(Graphics g, ColorScheme cp, AlbiteFont fontPlain, AlbiteFont fontItalic, char[] chapterBuffer) {
        Image image;
        if (af == null) {
            try {
                image = Image.createImage("/res/broken_image.png");
            } catch (IOException ioe) {
                ioe.printStackTrace();
                //broken image placeholder was not found, but one should still
                //display a placeholder
                image = Image.createImage(10, 10);
            }
        } else {
            //file found
            try {
                image = Image.createImage(af.getAsImage());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                //couldn't load image
                try {
                    image = Image.createImage("/res/broken_image.png");
                } catch (IOException ioee) {
                    ioee.printStackTrace();
                    //broken image placeholder was not found, but one should still
                    //display a placeholder
                    image = Image.createImage(10, 10);
                }
            }
        }
        g.setColor(cp.colors[ColorScheme.COLOR_FRAME]);
        g.drawRect(x - 5, y - 5, image.getWidth() + 9, image.getHeight() + 9);
        g.drawImage(image, x, y, Graphics.LEFT | Graphics.TOP);
    }
}