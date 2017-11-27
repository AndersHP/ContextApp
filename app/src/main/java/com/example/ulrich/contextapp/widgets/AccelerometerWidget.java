package com.example.ulrich.contextapp.widgets;

/**
 * Created by anders on 11/21/17.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerWidget implements SensorEventListener{

    private long lastUpdate = 0;
    private float[] lastReading = new float[3];
    private int sample_freq;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    public AccelerometerWidget(int sample_freq,SensorManager senSensorManager){

        this.sample_freq = sample_freq;
        this.senSensorManager = senSensorManager;
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    public float[] getReading(){
        return lastReading;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            // Halve sample freq so aggregator doesn't get same value twice
            if ((curTime - lastUpdate) > sample_freq / 2){
                lastUpdate = curTime;

                lastReading =  sensorEvent.values;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

}
