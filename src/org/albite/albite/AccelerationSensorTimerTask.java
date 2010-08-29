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
public class AccelerationSensorTimerTask extends SensorTimerTask {

    public AccelerationSensorTimerTask(SensorLoader sensorLoader, SensorConnection sensor) {
        super(sensorLoader, sensor);
    }

    protected int getOrientation(Data data[]) {

        float x = (float)(data[0].getDoubleValues()[0]);
        float y = (float)(data[1].getDoubleValues()[0]);
        int ang = aTan2(x, y);

        if (
                ((-180 <= ang) && (ang <= -135)) ||
                ((135 <= ang) && (ang <= 180))
                ) {
            return ORIENTATION_0;
        }
        
        if ((-45 <= ang) && (ang <= 45)) {
            return ORIENTATION_180;
        }

        if ((45 <= ang) && (ang <= 135)) {
            return ORIENTATION_90;
        }

        if ((-135 <= ang) && (ang <= -45)) {
            return ORIENTATION_270;
        }
        
        return ORIENTATION_0;
    }

    private final int aTan2(float arg1, float arg2) {
        float f1 = 0.78539816339744828F;
        float f2 = 3F * f1;
        float f3 = Math.abs(arg1);
        float f4;
        if(arg2 >= 0.0F)
        {
            float f5 = (arg2 - f3) / (arg2 + f3);
            f4 = f1 - f1 * f5;
        } else
        {
            float f6 = (arg2 + f3) / (f3 - arg2);
            f4 = f2 - f1 * f6;
        }
        return (int)Math.floor(Math.toDegrees(arg1 >= 0.0F ? f4 : -f4));
    }
}
