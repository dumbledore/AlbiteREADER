package org.albite.book.view.region;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.albite.ColorScheme;
import org.albite.book.model.element.ImageElement;
import org.albite.font.AlbiteFont;
import org.albite.image.AlbiteImage;

/*
 * TODO: Handle out of memory errors!
 */

public class ImageRegion extends Region {

    public static short VERTICAL_MARGIN = 10;

    public ImageRegion(
            final ImageElement imgel) {

        super(imgel,
                (short) 0, (short) VERTICAL_MARGIN,
                (short) 48, (short) (48 + VERTICAL_MARGIN));

        if (imgel != null) {
            //file found
            try {
                //TODO: Handle JPEG + GIF + PNG

                //read dimensions from PNG header
                DataInputStream din = imgel.entry.openDataInputStream();
                
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
            final AlbiteFont fontItalic) {

        Image image;

        boolean imageOK = false;
        ImageElement imgel = (ImageElement) element;
        if (imgel.entry == null) {
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
                InputStream in = imgel.entry.openInputStream();
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