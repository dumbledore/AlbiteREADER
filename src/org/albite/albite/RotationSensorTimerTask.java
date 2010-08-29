/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import javax.microedition.sensor.Data;
import javax.microedition.sensor.SensorConnection;

/**
 *
 * @author albus
 */
public class RotationSensorTimerTask extends SensorTimerTask {

    public RotationSensorTimerTask(SensorLoader sensorLoader, SensorConnection sensor) {
        super(sensorLoader, sensor);
    }

    protected int getOrientation(Data data[]) {
        int angY = data[1].getIntValues()[0];

        if (
                ((0 <= angY) && (angY <= 45)) ||
                ((315 <= angY) && (angY <= 360))
                ) {
            return ORIENTATION_0;
        }
        
        if ((135 <= angY) && (angY <= 225)) {
            return ORIENTATION_180;
        }

        if ((45 <= angY) && (angY <= 135)) {
            return ORIENTATION_270;
        }

        if ((225 <= angY) && (angY <= 315)) {
            return ORIENTATION_90;
        }

        return ORIENTATION_0;
    }
}
