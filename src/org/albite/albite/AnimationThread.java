/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

/**
 *
 * @author Albus Dumbledore
 */
public class AnimationThread extends Thread {
    private boolean running = true;
    private BookCanvas canvas;
    private boolean readyToSleep = true;

    //here are the states
    private int animateScrollPage = 0; //-2,2 -> fullpage, -1,1 ->samepage

    public AnimationThread(BookCanvas canvas) {
        this.canvas = canvas;
    }

    public synchronized void suspend() {
        //this clears all states
        animateScrollPage = 0;
        readyToSleep = true;
    }

    public synchronized void animateScrollPage(int i) {
        interrupt();
        animateScrollPage = i;
    }

    public void run() {
        while(running) {
            try {
                if (readyToSleep)
                    sleep(Long.MAX_VALUE);
                if (animateScrollPage != 0) {
                    switch(animateScrollPage) {
                        case 2:
//                            canvas.scrollPages(15, true);
                            canvas.scrollPages(55, true);
                            break;
                        case -2:
//                            canvas.scrollPages(-15, true);
                            canvas.scrollPages(-55, true);
                            break;
                        case 1:
                            canvas.scrollPages(5, false);
                            break;
                        case -1:
                            canvas.scrollPages(-5, false);
                            break;
                        case 3:
                            canvas.scrollPages(30, false);
                            break;
                        case -3:
                            canvas.scrollPages(-30, false);
                            break;
                    }
    //                canvas.repaint();
                }
                try {
                    sleep(20);
                } catch (InterruptedException ie) {}
            } catch (InterruptedException ie) {
                //we've been awoken!
                readyToSleep = false;
            }
        }
    }

    public void stop() {
        running = false;
    }
}