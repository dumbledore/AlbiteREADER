/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.util.TimerTask;
import javax.microedition.sensor.Data;
import javax.microedition.sensor.SensorConnection;

/**
 *
 * @author albus
 */
public abstract class SensorTimerTask extends TimerTask {
    
    static final int ORIENTATION_0      = BookCanvas.ORIENTATION_0;
    static final int ORIENTATION_90     = BookCanvas.ORIENTATION_90;
    static final int ORIENTATION_180    = BookCanvas.ORIENTATION_180;
    static final int ORIENTATION_270    = BookCanvas.ORIENTATION_270;

    int orientation = -13;

    SensorLoader sensorLoader;
    SensorConnection sensor;

    public SensorTimerTask(SensorLoader sensorLoader, SensorConnection sensor) {
        this.sensorLoader = sensorLoader;
        this.sensor = sensor;
    }

    public void run() {
        try {
            Data data[] = sensor.getData(1, 0, true, false, false);
            int lastOrientation = orientation;
            orientation = getOrientation(data);

            /*
             * If orientation has changed, set up timer task for it
             */
            if (orientation != lastOrientation) {
                sensorLoader.scheduleOrientationTimerTask(orientation);
            }
        } catch (Exception e) {}
    }

    protected abstract int getOrientation(Data data[]);

    public synchronized int getOrientation() {
        return orientation;
    }
}
