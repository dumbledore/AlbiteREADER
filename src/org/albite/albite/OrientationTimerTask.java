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
public class OrientationTimerTask extends TimerTask {
    private BookCanvas bookCanvas;
    private int orientation;

    public OrientationTimerTask(BookCanvas bookCanvas, int orientation) {
        this.bookCanvas = bookCanvas;
        this.orientation = orientation;
    }

    public void run() {
        bookCanvas.setOrientation(orientation);
    }
}
