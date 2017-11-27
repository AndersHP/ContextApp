package com.example.ulrich.contextapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.ulrich.contextapp.arffcreator.ArffCreator;
import com.example.ulrich.contextapp.datawindow.DataWindow;
import com.example.ulrich.contextapp.widgets.AccelerometerWidget;
import com.example.ulrich.contextapp.widgets.MicrophoneWidget;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Anders on 26-11-2017.
 */

public class Aggregator implements Runnable
{
    private long lastUpdate = 0;
    private ArrayList<DataWindow> dataWindows= new ArrayList<>();
    private int currentIndex = 0;
    private final int SAMPLE_FREQUENCY = 40;
    private boolean collecting = false;
    private AccelerometerWidget accelerometerWidget;
    private MicrophoneWidget microphoneWidget;

    private double[] microphoneReadings = new double[128];
    private float[][] accelerometerReadings = new float[128][3];

    private String currentClass;

    public Aggregator(SensorManager sensorManager)
    {
        accelerometerWidget = new AccelerometerWidget(SAMPLE_FREQUENCY, sensorManager);
        microphoneWidget = new MicrophoneWidget();
    }

    public void setCurrentClass(String currentClass){
        this.currentClass = currentClass;
    }

    public void setCollecting(boolean shouldCollect)
    {
        collecting = shouldCollect;
    }


    private float getCurrentHour()
    {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    private void makeDataWindow()
    {
        // Calculate accelerometer min, max
        int N = currentIndex; //window size
        float minAcc = Float.MAX_VALUE;
        float maxAcc = Float.MIN_VALUE;
        float sumAcc = 0;
        for (int i = 0; i < N; i++) {
            float val = sample(accelerometerReadings[i][0],accelerometerReadings[i][1],accelerometerReadings[i][2]);
            if(val < minAcc) { minAcc = val; }
            if(val > maxAcc) { maxAcc = val; }
            sumAcc += val;
        }

        // Calculate stdDevAcc
        float mean = sumAcc/N;
        float summedDifference = 0;
        for (int i = 0; i < N; i++) {
            summedDifference += Math.pow(sample(accelerometerReadings[i][0],accelerometerReadings[i][1],accelerometerReadings[i][2]) - mean,2);
        }
        float stdDevAcc = (float)Math.sqrt(summedDifference / (N-1)); //standard deviation


        // Calculate microphone min, max
        float minMic = Float.MAX_VALUE;
        float maxMic = Float.MIN_VALUE;
        float sumMic = 0;
        for (int i = 0; i < N; i++) {
            float val = (float)microphoneReadings[i];
            if(val < minMic) { minMic = val; }
            if(val > maxMic) { maxMic = val; }
            sumMic += val;
        }


        // Calculate stdDevMic
        float mean2 = sumMic/N;
        float summedDifference2 = 0;
        for (int i = 0; i < N; i++) {
            summedDifference2 += Math.pow(microphoneReadings[i] - mean2,2);
        }
        float stdDevMic = (float)Math.sqrt(summedDifference2 / (N-1)); //standard deviation


        Log.d("newWindow", "Making new datawindow" );
        DataWindow newWindow = new DataWindow(getCurrentHour(), minAcc, maxAcc, stdDevAcc, minMic, maxMic, stdDevMic);
        dataWindows.add(newWindow);
        ArffCreator.saveArff(dataWindows,"runningdata",currentClass);
    }

    private float sample(float x, float y, float z){
        double x_squared = Math.pow(x,2);
        double y_squared = Math.pow(y,2);
        double z_squared = Math.pow(z,2);
        return (float)Math.sqrt(x_squared + y_squared + z_squared);
    }

    @Override
    public void run()
    {
        while (collecting)
        {
            // Wait abit between readings
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > SAMPLE_FREQUENCY) {
                lastUpdate = curTime;

                if (currentIndex > 127)
                    currentIndex = 0;


                double microphoneReading = microphoneWidget.getLastAmplitudeReading();
                float[] accelerometerReading = accelerometerWidget.getReading();
                microphoneReadings[currentIndex] = microphoneReading;
                accelerometerReadings[currentIndex] = accelerometerReading;
                Log.d("s"  ,    "new reading");
                //Log.d("asd ","" +accelerometerReading[0]);
                if (currentIndex == 63 || currentIndex == 127)
                {
                    makeDataWindow();
                }
                currentIndex++;
            }
        }
    }
}
