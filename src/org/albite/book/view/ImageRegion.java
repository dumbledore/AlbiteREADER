package org.albite.book.view;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;
import org.albite.image.AlbiteImage;
import org.albite.util.archive.zip.ArchiveZipEntry;

class ImageRegion {

    public static final int MARGIN = 10;

    private ArchiveZipEntry entry;

    public int altTextBufferPosition;
    public int altTextBufferLength;

    public ImageRegion(
            ArchiveZipEntry entry,
            final int altTextBufferPosition,
            final int altTextBufferLength) {

//        super(0, VERTICAL_MARGIN,
//                48, (48 + VERTICAL_MARGIN));
        this.entry = entry;
        this.altTextBufferPosition = altTextBufferPosition;
        this.altTextBufferLength = altTextBufferLength;

        System.out.println("Image null? " + (entry == null));
    }

    public final Image load(
            final int canvasWidth,
            final int canvasHeight,
            final int fontHeight) {
        
        boolean rescale = false;
        int width;
        int height;
        Image image;

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
                    image = Image.createImage(2, 2);
                }
            }
        }

        return image;

//        /*
//         * Work with actual image's w/h
//         */
//
//        width  = image.getWidth();
//        height = image.getHeight();
//
//        /*
//         * Add margin values
//         */
//        width  += 4 * MARGIN + 2;
//        height += 4 * MARGIN + 2;
//
//        /*
//         * Center horizontally
//         */
//        x = ((canvasWidth - width) / 2);

        /*
         * y is always assumed to be 0,
         * as it is set later in the draw method of TextPage,
         * which is a result of the fact that one'd like the
         * page (image + text) vertically centered, too.
         */
    }

//    public void draw(
//            final Graphics g,
//            final ColorScheme cp,
//            final AlbiteFont fontPlain,
//            final AlbiteFont fontItalic,
//            final char[] chapterBuffer) {
//
//        if (imageFrame) {
//            /*
//             * Draws the rectangle only if showing the actual image,
//             * i.e. doesn't draw frame around the "broken image" placeholders
//             */
//            g.setColor(cp.colors[ColorScheme.COLOR_FRAME]);
//            g.drawRect(
//                    x + MARGIN, MARGIN,
//                    x + image.getWidth()  + 2 * MARGIN,
//                        image.getHeight() + 2 * MARGIN);
////                    x - 5, y - 5, image.getWidth() + 9, image.getHeight() + 9);
//        }
//
//        g.drawImage(image,
//                x + 2 * MARGIN + 1,
//                    2 * MARGIN + 1,
//                Graphics.LEFT | Graphics.TOP);
//    }
}