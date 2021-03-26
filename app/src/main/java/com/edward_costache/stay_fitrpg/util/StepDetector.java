package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class StepDetector {

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private Context context;
    private int steps;

    public interface Listener
    {
        void onStep(int steps);
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
                    steps++;
                    listener.onStep(steps);
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
        Toast.makeText(context, "Step Detector Registered", Toast.LENGTH_SHORT).show();
    }

    public void un_registerListener()
    {
        sensorManager.unregisterListener(sensorEventListener);
        Toast.makeText(context, "Step Detector Unregistered", Toast.LENGTH_SHORT).show();
    }
}
