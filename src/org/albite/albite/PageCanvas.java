/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.book.view.Page;

/**
 *
 * @author Albus Dumbledore
 */
public class PageCanvas {

    private static Image buffer = null;
    private int orientation;

    private Image canvas;
    private Page page; //the page it is rendering or accessing for input (through getRegionAt())

    public PageCanvas(int width, int height, int orientation) {
        this.orientation = orientation;

        if (orientation != BookCanvas.ORIENTATION_0
                && (buffer == null
                    || buffer.getWidth() != height
                    || buffer.getHeight() != width)
                    ) {
            /*
             * Rotation will be necessary
             */
            if (orientation == BookCanvas.ORIENTATION_180) {
                buffer = Image.createImage(width, height);
            } else {
                buffer = Image.createImage(height, width);
            }
        } else {
            /*
             * No rotation needed. Better clear the buffer.
             */
            buffer = null;
        }

        canvas = Image.createImage(width, height);
        page = null;
    }

    public final void renderPage(ColorScheme cp) {

        final int color_bg = cp.colors[ColorScheme.COLOR_BACKGROUND];

        final Image img = (buffer == null ? canvas : buffer);

        final Graphics g = img.getGraphics();
        final int w = img.getWidth();
        final int h = img.getHeight();

        g.setColor(color_bg);
        g.fillRect(0, 0, w, h);

        page.draw(g, cp);

        /*
         * Rotate, if necessary
         */
        if (orientation != BookCanvas.ORIENTATION_0) {
            final Graphics gx = canvas.getGraphics();
            gx.drawRegion(img, 0, 0, w, h,
                    orientation, 0, 0,
                    Graphics.LEFT | Graphics.TOP);
        }
    }

    public final Image getImage() {
        return canvas;
    }

    public final int getWidth() {
        return canvas.getWidth();
    }

    public final int getHeight() {
        return canvas.getHeight();
    }

    public final int getPageWidth() {
        return (buffer == null ? canvas.getWidth() : buffer.getWidth());
    }

    public final int getPageHeight() {
        return (buffer == null ? canvas.getHeight() : buffer.getHeight());
    }

    public final void setPage(Page page) {
        this.page = page;
    }
}
