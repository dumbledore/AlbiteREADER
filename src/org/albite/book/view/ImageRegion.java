package org.albite.book.view;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.albite.ColorScheme;
import org.albite.font.AlbiteFont;
import org.albite.image.AlbiteImage;
import org.albite.util.archive.zip.ArchiveZipEntry;

class ImageRegion extends Region {

    public static final short VERTICAL_MARGIN = 10;

    private static short MAX_HEIGHT = 100;
    private static short MAX_WIDTH  = 100;

    ArchiveZipEntry entry;
    private boolean rescale = false;

    public int altTextBufferPosition;
    public int altTextBufferLength;

    public ImageRegion(
            ArchiveZipEntry entry,
            final int altTextBufferPosition,
            final int altTextBufferLength) {

        super((short) 0, (short) 0,
                (short) 48, (short) 48);
//        super((short) 0, (short) VERTICAL_MARGIN,
//                (short) 48, (short) (48 + VERTICAL_MARGIN));
        this.entry = entry;
        this.altTextBufferPosition = altTextBufferPosition;
        this.altTextBufferLength = altTextBufferLength;

        System.out.println("Image null? " + (entry == null));
        if (entry != null) {
            //file found
            try {
                //read dimensions from PNG header
                InputStream in = entry.openInputStream();
                try {
                    try {
//                    int[] dimensions = AlbiteImage.getPNGDimensions(din);
//                    width = (short) dimensions[0];
//                    height = (short) (dimensions[1] + VERTICAL_MARGIN);
                        Image img = Image.createImage(in);
                        width = (short) img.getWidth();
                        height = (short) img.getHeight();

                        float ratio = 1;

                        /*
                         * check width
                         */
                        if (width > MAX_WIDTH) {
                            ratio = (float) MAX_WIDTH / (float) width;
                            width = MAX_WIDTH;
                            height *= ratio;
                            rescale = true;
                        }

                        /*
                         * check height
                         */
                        if (height > MAX_HEIGHT) {
                            ratio = (float) MAX_HEIGHT / (float) height;
                            height = MAX_HEIGHT;
                            width *= ratio;
                            rescale = true;
                        }

                    /*
                     * got to check width again for h may have modified it
                     */
                    if (width > MAX_WIDTH) {
                        ratio = (float) MAX_WIDTH / (float) width;
                        width = MAX_WIDTH;
                        height *= ratio;
                        rescale = true;
                    }

                    System.out.println("Image " + width + " x " + height);
                    } catch (IOException e) {
                        entry = null;
                        e.printStackTrace();
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ioe) {
                entry = null;
                ioe.printStackTrace();
            }
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

        if (entry == null) {
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
                InputStream in = entry.openInputStream();
                try {
                    image = Image.createImage(Image.createImage(in));
                    imageOK = true;

                    /*
                     * Try to resize, if image is too big
                     */
                    if (rescale) {
                        try {
                            Image rescaled =
                                    AlbiteImage.rescale(image, width, height);
                            image = rescaled;
                        } catch (Exception e) {
                            /*
                             * The rescaled version will be used,
                             * ONLY if the rescaling succeeded,
                             * i.e. there was enough memory for it.
                             */
                            e.printStackTrace();
                        }
                    }
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                //couldn't load image (this includes out of mem error)
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

    public static void setMaxDimensions(final int w, final int h) {
        MAX_WIDTH = (short) (w - 2 * VERTICAL_MARGIN);
        MAX_HEIGHT = (short) (h - 2 * VERTICAL_MARGIN);
    }
}