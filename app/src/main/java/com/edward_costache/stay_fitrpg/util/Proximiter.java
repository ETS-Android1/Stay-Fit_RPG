package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Proximiter {

    public interface Listener
    {
        void onDistance(float cm);
    }
    private Listener listener;

    public void setListener(Listener l) {
        listener = l;
    }

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private Context context;

    public Proximiter(Context context)
    {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(listener != null)
                {
                    /*
                    This Overwritten method returns 3 different set of values stored in an Array called values. The indexing goes from 0 to 2 and it represents
                    x, y, z respectively. Here i am using the interface of the listener initialized above to pass these values onto a method called onDistance(),
                    these values can then be fetched and used when creating a Gyroscope object in another class.
                     */
                    listener.onDistance(event.values[0]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public void registerListener()
    {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void un_registerListener()
    {
        sensorManager.unregisterListener(sensorEventListener);
    }
}
