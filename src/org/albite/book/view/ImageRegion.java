package org.albite.book.view;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.image.AlbiteImage;
import org.albite.util.archive.ArchivedFile;

class ImageRegion extends Region {

    public static short VERTICAL_MARGIN = 10;

    ArchivedFile af;
    public int altTextBufferPosition;
    public int altTextBufferLength;
   
    public ImageRegion(
            final ArchivedFile af,
            final int altTextBufferPosition,
            final int altTextBufferLength) {

        super((short) 0, (short) VERTICAL_MARGIN,
                (short) 48, (short) (48 + VERTICAL_MARGIN));
        this.af = af;
        this.altTextBufferPosition = altTextBufferPosition;
        this.altTextBufferLength = altTextBufferLength;

        if (af != null) {
            //file found
            try {
                //read dimensions from PNG header
                DataInputStream din = af.openDataInputStream();
                try {
                    int[] dimensions = AlbiteImage.getPNGDimensions(din);
                    width = (short) dimensions[0];
                    height = (short) (dimensions[1] + VERTICAL_MARGIN);
                } finally {
                    din.close();
                }
            } catch (IOException ioe) {}
        }
    }

    public void draw(
            final Graphics g,
            final ColorScheme cp,
            final AlbiteFont fontPlain,
            final AlbiteFont fontItalic,
            final char[] chapterBuffer) {

        Image image;

        boolean imageOK = false;

        if (af == null) {
            try {
                image = Image.createImage("/res/broken_image.png");
            } catch (IOException ioe) {
                //broken image placeholder was not found, but one should still
                //display a placeholder
                image = Image.createImage(10, 10);
            }
        } else {
            //file found
            try {
                InputStream in = af.openInputStream();
                try {
                    image = Image.createImage(Image.createImage(in));
                    imageOK = true;
                } finally {
                    in.close();
                }
            } catch (IOException ioe) {
                //couldn't load image
                try {
                    image = Image.createImage("/res/broken_image.png");
                } catch (IOException ioee) {

                    /*
                     * broken image placeholder was not found,
                     * but one should still display a placeholder
                     */
                    image = Image.createImage(10, 10);
                }
            }
        }

        g.setColor(cp.colors[ColorScheme.COLOR_FRAME]);
        if (imageOK) {
            /*
             * Draws the rectangle only if the image is OK
             */
            g.drawRect(
                    x - 5, y - 5, image.getWidth() + 9, image.getHeight() + 9);
        }
        g.drawImage(image, x, y, Graphics.LEFT | Graphics.TOP);
    }
}