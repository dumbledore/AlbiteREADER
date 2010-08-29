/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.util.TimerTask;

/**
 *
 * @author albus
 */
public class ScrollingTimerTask extends TimerTask {
    public static final int SCROLL_PREV         = 0;
    public static final int SCROLL_NEXT         = 1;
    public static final int SCROLL_SAME_PREV    = 2;
    public static final int SCROLL_SAME_NEXT    = 3;
    public static final int SCROLL_BOOK_START   = 4;
    public static final int SCROLL_BOOK_END     = 5;
    
    private BookCanvas canvas;
    private int dx;
    private boolean fullPage;

    public ScrollingTimerTask(BookCanvas canvas, int scrollMode) {
        this.canvas = canvas;

        switch(scrollMode) {
            case SCROLL_PREV:
                dx = 55;
                fullPage = true;
                break;
            case SCROLL_NEXT:
                dx = -55;
                fullPage = true;
                break;
            case SCROLL_SAME_NEXT:
                dx = 5;
                fullPage = false;
                break;
            case SCROLL_SAME_PREV:
                dx = -5;
                fullPage = false;
                break;
            case SCROLL_BOOK_END:
                dx = 30;
                fullPage = false;
                break;
            case SCROLL_BOOK_START:
                dx = -30;
                fullPage = false;
                break;
            default:
                System.out.println("Wrong scrolling mode");
                dx = 0;
                fullPage = false;
                break;
        }
    }

    public void run() {
        canvas.scrollPages(dx, fullPage);
    }
}
