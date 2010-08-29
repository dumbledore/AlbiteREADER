/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.util.Timer;
import javax.microedition.io.Connector;
import javax.microedition.sensor.SensorConnection;

/**
 *
 * @author albus
 */
public class SensorLoader {
    private static final int DATA_UPDATE_INTERVAL = 1000;
    private static final int ORIENTATION_WAIT_INTERVAL = 650;

    private SensorConnection sensor;
    private BookCanvas bookCanvas;
    private Timer timer;
    private OrientationTimerTask orientationTimerTask;
    private SensorTimerTask sensorTimerTask;
    private boolean rotationSensorAvailable;

    private SensorLoader(SensorConnection sensor, BookCanvas bookCanvas,
            boolean rotationSensorAvailable) {
        this.sensor = sensor;
        this.bookCanvas = bookCanvas;
        this.rotationSensorAvailable = rotationSensorAvailable;
        this.timer = new Timer();
    }

    public static SensorLoader getSensorLoader(BookCanvas bookCanvas) {
        SensorLoader sl = null;

        /* Try loading an acceleration sensor */
        try {
            SensorConnection sensor =
                    (SensorConnection)Connector.open("sensor:acceleration");
            sl = new SensorLoader(sensor, bookCanvas, false);
        } catch (Exception e2) {
            /* No usable sensors found */
        }

        return sl;
    }

    protected synchronized final void
            scheduleOrientationTimerTask(int orientation) {
        if (orientationTimerTask != null) {
            orientationTimerTask.cancel();
        }

        orientationTimerTask 
                = new OrientationTimerTask(bookCanvas, orientation);
        timer.schedule(orientationTimerTask, ORIENTATION_WAIT_INTERVAL);
    }

    public synchronized final void stopMonitoring() {
        if (orientationTimerTask != null) {
            orientationTimerTask = null;
        }

        if (sensorTimerTask != null) {
            sensorTimerTask = null;
        }

        timer.cancel();
    }

    public synchronized final void startMonitoring() {

        if (sensorTimerTask == null) {
            sensorTimerTask = new SensorTimerTask(this, sensor);

            timer.schedule(sensorTimerTask, 
                    DATA_UPDATE_INTERVAL, DATA_UPDATE_INTERVAL);
        }
    }
}
