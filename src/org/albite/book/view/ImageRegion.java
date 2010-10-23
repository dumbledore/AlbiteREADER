package org.albite.book.view;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import javax.microedition.lcdui.Image;
import org.albite.image.AlbiteImage;
import org.albite.util.archive.zip.ArchiveZipEntry;

class ImageRegion {

    public static final int MARGIN = 10;
    public static WeakReference BROKEN_IMAGE;

    private ArchiveZipEntry entry;

    private WeakReference imageReference;

    public int altTextBufferPosition;
    public int altTextBufferLength;

    public ImageRegion(
            ArchiveZipEntry entry,
            final int altTextBufferPosition,
            final int altTextBufferLength) {

        this.entry = entry;
        this.altTextBufferPosition = altTextBufferPosition;
        this.altTextBufferLength = altTextBufferLength;
    }

    public final Image getImage(
            final int canvasWidth,
            final int canvasHeight,
            final int fontHeight) {
        
        boolean rescale = false;
        int width;
        int height;
        Image image = null;

        if (entry == null) {
            image = getBrokenImage();
        } else {
            /*
             * file found
             */
            if (imageReference != null) {
                image = (Image) imageReference.get();
            }

            if (image == null) {
                try {
                    InputStream in = entry.openInputStream();
                    try {
                        image = Image.createImage(Image.createImage(in));

                        int maxWidth = (canvasWidth - 4 * MARGIN - 1);
                        int maxHeight = (canvasHeight - 4 * MARGIN - 1);

                        if (altTextBufferLength > 0) {
                            maxHeight -= fontHeight;
                        }

                        width = image.getWidth();
                        height = image.getHeight();

                        float ratio = 1;

                        /*
                         * check width
                         */
                        if (width > maxWidth) {
                            ratio = (float) maxWidth / (float) width;
                            width = maxWidth;
                            height *= ratio;
                            rescale = true;
                        }

                        /*
                         * check height
                         */

                        if (height > maxHeight) {
                            ratio = (float) maxHeight / (float) height;
                            height = maxHeight;
                            width *= ratio;
                            rescale = true;
                        }

                        /*
                         * got to check width again for h may have modified it
                         */
                        if (width > maxWidth) {
                            ratio = (float) maxWidth / (float) width;
                            width = maxWidth;
                            height *= ratio;
                            rescale = true;
                        }

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
//                                e.printStackTrace();
                            }
                        }
                    } finally {
                        in.close();
                    }
                } catch (Exception e) {
                    image = getBrokenImage();
                }
                
                /*
                 * Cache the image
                 */
                imageReference = new WeakReference(image);
            }
        }

        return image;
    }

    private static Image getBrokenImage() {
        Image image = null;

        if (BROKEN_IMAGE != null) {
            image = (Image) BROKEN_IMAGE.get();
        }

        if (image == null) {
            try {
                image = Image.createImage("/res/broken_image.png");
            } catch (IOException e) {
                //broken image placeholder was not found, but one should still
                //display a placeholder
                image = Image.createImage(2, 2);
            }

            /*
             * Cache the image
             */
            BROKEN_IMAGE = new WeakReference(image);
        }

        return image;
    }
}