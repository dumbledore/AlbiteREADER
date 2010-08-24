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

    BookCanvas bookCanvas;
    Image canvas;

    Page page; //the page it is rendering or accessing for input (through getRegionAt())

    public PageCanvas(BookCanvas bookCanvas) {
        this.bookCanvas = bookCanvas;
        int orientation = bookCanvas.getOrientation();
        if (orientation == BookCanvas.ORIENTATION_0 || orientation == BookCanvas.ORIENTATION_180) {
            canvas = Image.createImage(bookCanvas.getWidth() - (2*BookCanvas.MARGIN_WIDTH), bookCanvas.getHeight() - BookCanvas.MENU_HEIGHT - bookCanvas.getStatusBarHeight());
        } else {
            canvas = Image.createImage(bookCanvas.getHeight() - BookCanvas.MENU_HEIGHT - bookCanvas.getStatusBarHeight(), bookCanvas.getWidth() - (2*BookCanvas.MARGIN_WIDTH));
        }

        page = null;
    }

    public final void renderPage() {

        final ColorProfile cp = bookCanvas.getCurrentProfile();
        final int color_bg = cp.getColor(ColorProfile.CANVAS_BACKGROUND_COLOR);

        final Graphics g = getImage().getGraphics();

        final int w = canvas.getWidth();
        final int h = canvas.getHeight();

        g.setColor(color_bg);
        g.fillRect(0, 0, w, h);

        page.draw(g, cp);
    }

    public final Image getImage() {
        return canvas;
    }

    public final int getWidth() {
        return getImage().getWidth();
//        return 220;
    }

    public final int getHeight() {
        return getImage().getHeight();
//        return 250;
    }
}
