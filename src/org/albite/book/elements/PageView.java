/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.elements;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.albite.albite.BookCanvas;
import org.albite.albite.ColorProfile;

/**
 *
 * @author Albus Dumbledore
 */
public class PageView {

    BookCanvas bookCanvas;
    Image canvas;

    public Page page; //the page it is rendering or accessing for input
    public int x;

    public PageView(BookCanvas bookCanvas) {
        this.bookCanvas = bookCanvas;

        //TODO: some good point for tests here
//        canvas = Image.createImage(100, bookCanvas.getHeight() - BookCanvas.MENU_HEIGHT - BookCanvas.STATUS_BAR_HEIGHT);
        canvas = Image.createImage(bookCanvas.getWidth() - (2*BookCanvas.MARGIN_WIDTH), bookCanvas.getHeight() - BookCanvas.MENU_HEIGHT - BookCanvas.STATUS_BAR_HEIGHT);

        page = null;
        x = 0;
    }

    public void renderPage() {

        final ColorProfile cp = bookCanvas.getCurrentProfile();
        final int color_bg = cp.canvasBackgroupColor;
        final int color_dummy = cp.canvasTextDummyColor;

        final Graphics g = getImage().getGraphics();

        final int w = canvas.getWidth();
        final int h = canvas.getHeight();

        g.setColor(color_bg);
        g.fillRect(0, 0, w, h);

        if (page.pageMode == Page.PAGE_MODE_NORMAL || page.pageMode == Page.PAGE_MODE_IMAGE) {
            for (int i=0; i<page.regions.size(); i++) {
                //drawing regions in a normal page
                Region region = (Region)page.regions.elementAt(i);
                region.draw(g, cp);
            }
        } else {
            g.setColor(color_dummy);
            switch(page.pageMode) {
                case Page.PAGE_MODE_LEAVES_CHAPTER_NEXT:
                    {
                        int width = bookCanvas.fontItalic.charsWidth(Page.LABEL_LEAVES_CHAPTER_NEXT);
                        bookCanvas.fontItalic.drawChars(g, color_dummy, Page.LABEL_LEAVES_CHAPTER_NEXT, (w-width)/2, h/2-20);
                    }
                    break;

                case Page.PAGE_MODE_LEAVES_CHAPTER_END_OF_BOOK:
                    {
                        int width = bookCanvas.fontItalic.charsWidth(Page.LABEL_LEAVES_CHAPTER_END_OF_BOOK);
                        bookCanvas.fontItalic.drawChars(g, color_dummy, Page.LABEL_LEAVES_CHAPTER_END_OF_BOOK, (w-width)/2, h/2-20);
                    }
                    break;

                case Page.PAGE_MODE_LEAVES_CHAPTER_PREV:
                    {
                        int width = bookCanvas.fontItalic.charsWidth(Page.LABEL_LEAVES_CHAPTER_PREV);
                        bookCanvas.fontItalic.drawChars(g, color_dummy, Page.LABEL_LEAVES_CHAPTER_PREV, (w-width)/2, h/2-20);
                    }
                    break;

                case Page.PAGE_MODE_LEAVES_CHAPTER_START_OF_BOOK:
                    {
                        int width = bookCanvas.fontItalic.charsWidth(Page.LABEL_LEAVES_CHAPTER_START_OF_BOOK);
                        bookCanvas.fontItalic.drawChars(g, color_dummy, Page.LABEL_LEAVES_CHAPTER_START_OF_BOOK, (w-width)/2, h/2-20);
                    }
                    break;

                case Page.PAGE_MODE_IMAGE:
                    //TODO: drawing a page containing a single image
                    break;
            }
        }
    }

    public Image getImage() {
        return canvas;
    }

    public int getWidth() {
        return getImage().getWidth();
//        return 220;
    }

    public int getHeight() {
        return getImage().getHeight();
//        return 250;
    }
}
