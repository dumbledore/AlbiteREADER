package org.albite.book.view;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.albite.ColorProfile;
import org.albite.font.BitmapFont;
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
                DataInputStream din = new DataInputStream(af.openInputStream());
                din.skipBytes(16); //skipping PNG header
                width = (short)din.readInt();
                height = (short)(din.readInt() + VERTICAL_MARGIN);
            } catch (IOException ioe) {
                //couldn't load image
                ioe.printStackTrace();
            }
        }
    }

    public void draw(Graphics g, ColorProfile cp, BitmapFont fontPlain, BitmapFont fontItalic, char[] chapterBuffer) {
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
                image = Image.createImage(af.openInputStream());
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

        g.drawImage(image, x, y, Graphics.LEFT | Graphics.TOP);
    }
}