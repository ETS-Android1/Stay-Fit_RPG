package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetector {

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private Context context;

    public interface Listener
    {
        void onStep();
    }
    private Listener listener;
    public void setListener(Listener l)
    {
        listener = l;
    }

    public StepDetector(Context context)
    {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if(listener != null)
                {
                    listener.onStep();
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
