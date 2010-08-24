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
public class ClockTimerTask extends TimerTask {
    private final BookCanvas bookCanvas;

    public ClockTimerTask(BookCanvas bookCanvas) {
        this.bookCanvas = bookCanvas;
    }

    public void run() {
        bookCanvas.updateClock();
    }
}
