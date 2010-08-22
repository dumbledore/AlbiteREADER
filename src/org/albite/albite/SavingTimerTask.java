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
public class SavingTimerTask extends TimerTask {
    AlbiteMIDlet    app;
    BookCanvas      bookCanvas;

    public SavingTimerTask(AlbiteMIDlet app, BookCanvas bookCanvas) {
        this.app = app;
        this.bookCanvas = bookCanvas;
    }

    public void run() {
        System.out.println("Autosave...");
        bookCanvas.saveBookOptions();
        bookCanvas.saveOptionsToRMS();
        app.saveOptionsToRMS();
    }
}
