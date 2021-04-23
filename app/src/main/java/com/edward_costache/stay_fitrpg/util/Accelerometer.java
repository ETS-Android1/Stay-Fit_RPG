package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Code adapted from Richvale Consulting, YouTube channel https://www.youtube.com/watch?v=OPsVr44uCb8&t=853s
 */
public class Accelerometer {

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    public interface Listener
    {
        void onTranslation(float tx, float ty, float tz);
    }
    private Listener listener;
    public void setListener(Listener l)
    {
        listener = l;
    }

    public Accelerometer(Context context, int sensorType)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if(listener != null)
                {
                    /*
                    This Overwritten method returns 3 different set of values stored in an Array called values. The indexing goes from 0 to 2 and it represents
                    x, y, z respectively. Here i am using the interface of the listener initialized above to pass these values onto a method called onTranslate(),
                    these values can then be fetched and used when creating a Accelerometer object in another class.
                     */
                    listener.onTranslation(event.values[0], event.values[1], event.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public void registerListener()
    {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void un_registerListener()
    {
        sensorManager.unregisterListener(sensorEventListener);
    }
}
