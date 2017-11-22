package com.example.ulrich.contextapp.widgets;

/**
 * Created by anders on 11/21/17.
 */
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import java.util.*;
import java.util.Collections;


import weka.filters.*;
import weka.classifiers.*;
import weka.core.*;


public class AccelerometerWidget implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private ArrayList <Double> dataList = new ArrayList<Double>();

    public AccelerometerWidget(SensorManager manager){
        senSensorManager = manager;
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            float r = x*x + y*y + z*z;
            double k = Math.sqrt(r);

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 200) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                //TextView hej =(TextView)findViewById(R.id.hej);
                //hej.setText(k + "");
                dataList.add(k);
                if (speed > SHAKE_THRESHOLD) {

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    public void getMax(){
        double i = Collections.max(dataList);
        //TextView max =(TextView)findViewById(R.id.max);
        //max.setText(i + "");
    }

    public void getMin(){
        double i = Collections.min(dataList);
        //TextView min =(TextView)findViewById(R.id.min);
        //min.setText(i + "");
    }

    public void getStdDevOfMagnitude(){}



}
