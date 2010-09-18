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

    private Image canvas;
    private Page page; //the page it is rendering or accessing for input (through getRegionAt())

    public PageCanvas(int width, int height) {
        canvas = Image.createImage(width, height);
        page = null;
    }

    public final void renderPage(ColorProfile cp) {

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
        return canvas.getWidth();
    }

    public final int getHeight() {
        return canvas.getHeight();
    }

    public final void setPage(Page page) {
        this.page = page;
    }
}
