package com.example.ulrich.contextapp.widgets;

/**
 * Created by anders on 11/21/17.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.example.ulrich.contextapp.datawindow.DataWindow;
import com.example.ulrich.contextapp.arffcreator.ArffCreator;
import java.util.ArrayList;
import java.lang.Math;

public class AccelerometerWidget implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private ArrayList<DataWindow> dataWindows= new ArrayList<>();
    private float[] xReadings = new float[128];
    private float[] yReadings = new float[128];
    private float[] zReadings = new float[128];
    private int currentIndex = 0;
    private final int SAMPLE_FREQUENCY = 40;

    public AccelerometerWidget(SensorManager manager){
        senSensorManager = manager;
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            // Only add data point for each SAMPLE_FREQUENCY ms
            if ((curTime - lastUpdate) > SAMPLE_FREQUENCY){
                lastUpdate = curTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                if(currentIndex > 127)
                    currentIndex = 0;

                xReadings[currentIndex] = x;
                yReadings[currentIndex] = y;
                zReadings[currentIndex] = z;

                // When adding a datapoint to the middle or the end of the x,y,z reading arrays -
                // Make a new DataWindow object (the last check is to avoid making a DataWindow for-
                // the first 64 readings.)
                Log.d("hey","currentIndex: "+ currentIndex + " : " + (xReadings[127] != 0L));
                if((currentIndex == 63 || currentIndex == 127) && xReadings[127] != 0L)
                {
                    makeDataWindow();
                }

                currentIndex++;
            }
        }
    }


    private void makeDataWindow()
    {
        // Calculate min, max
        int N = 128; //window size
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float sum = 0;
        for (int i = 0; i < N; i++) {
            float val = sample(xReadings[i],yReadings[i],zReadings[i]);
            if(val < min) { min = val; }
            if(val > max) { max = val; }
            sum += val;
        }

        // Calculate stdDev
        float mean = sum/N;
        float summedDifference = 0;
        for (int i = 0; i < N; i++) {
            summedDifference += Math.pow(sample(xReadings[i],yReadings[i],zReadings[i]) - mean,2);
        }
        float stdDev = (float)Math.sqrt(summedDifference / (N-1)); //standard deviation

        Log.d("newWindow", "Making new datawindow: " + min + ":" + max + ":" +stdDev );
        // Add new DataWindow
        DataWindow newWindow = new DataWindow(min, max,stdDev);
        dataWindows.add(newWindow);
        ArffCreator.saveArff(dataWindows,"runningdata","running");
    }

    private float sample(float x, float y, float z){
        double x_squared = Math.pow(x,2);
        double y_squared = Math.pow(y,2);
        double z_squared = Math.pow(z,2);
        return (float)Math.sqrt(x_squared + y_squared + z_squared);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

}
