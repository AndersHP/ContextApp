package com.example.ulrich.contextapp;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import com.example.ulrich.contextapp.arffcreator.ArffCreator;
import com.example.ulrich.contextapp.datawindow.DataWindow;
import com.example.ulrich.contextapp.widgets.AccelerometerWidget;
import com.example.ulrich.contextapp.widgets.GPSWidget;
import com.example.ulrich.contextapp.widgets.MicrophoneWidget;

import java.util.ArrayList;

/**
 * Created by Anders on 26-11-2017.
 */

public class Aggregator implements Runnable
{
    private long lastUpdate = 0;
    private ArrayList<DataWindow> dataWindows= new ArrayList<>();
    private DataWindow lastDataWindow = null;
    private int currentIndex = 0;
    private final int SAMPLE_FREQUENCY;
    private boolean collecting = false;
    private AccelerometerWidget accelerometerWidget;
    private MicrophoneWidget microphoneWidget;
    private GPSWidget gpsWidget;
    private boolean classifying;

    private double[] gpsReadings = new double[128];
    private double[] microphoneReadings = new double[128];
    private float[][] accelerometerReadings = new float[128][3];

    private String currentClass;

    public Aggregator(SensorManager sensorManager, int sampleFrequency, boolean classifying, Context c)
    {
        this.classifying = classifying;
        SAMPLE_FREQUENCY = sampleFrequency;
        accelerometerWidget = new AccelerometerWidget(sampleFrequency, sensorManager);
        microphoneWidget = new MicrophoneWidget();
        gpsWidget = new GPSWidget(c);
    }

    public void setCurrentClass(String currentClass){
        this.currentClass = currentClass;
    }

    public void setCollecting(boolean shouldCollect)
    {
        collecting = shouldCollect;

        if(!collecting){
            clearReadings();
            if(microphoneWidget.isStarted())
                microphoneWidget.stop();
        }else{
            microphoneWidget.start();
        }

    }

    private void clearReadings(){
        microphoneReadings = new double[128];
        accelerometerReadings = new float[128][3];
        gpsReadings = new double[128];
        currentIndex = 0;
    }

    public DataWindow getLastDataWindow(){
        return lastDataWindow;
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
                double currentSpeed = gpsWidget.getCurrentSpeed();
                microphoneReadings[currentIndex] = microphoneReading;
                accelerometerReadings[currentIndex] = accelerometerReading;
                gpsReadings[currentIndex] = currentSpeed;

                if ((currentIndex == 63 || currentIndex == 127) && microphoneReadings[127] != 0f)
                {
                    makeDataWindow();
                }
                currentIndex++;
            }
        }
    }

    private void makeDataWindow()
    {
        // Calculate accelerometer min, max
        int N = 128; //window size
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


        float avgSum = 0;
        for(int i = 0; i< N; i++){
            avgSum += microphoneReadings[i];
        }
        float avgMicAmp = avgSum/N;

        // calc avg speed
        double speedSum = 0;
        for(int i = 0; i<N; i++)
        {
            speedSum += gpsReadings[i];
        }
        double avgSpeed = speedSum / N;

        Log.d("newWindow", "Making new datawindow" );
        DataWindow newWindow = new DataWindow(minAcc, maxAcc, stdDevAcc, avgMicAmp,avgSpeed, currentClass);
        dataWindows.add(newWindow);
        lastDataWindow = newWindow;

        // only create new file every 20 datawindow and only if not classifying
        if(!classifying && dataWindows.size() % 20 == 0)
            ArffCreator.saveArff(dataWindows,"VolumeAdjusterData");
    }

    private float sample(float x, float y, float z){
        double x_squared = Math.pow(x,2);
        double y_squared = Math.pow(y,2);
        double z_squared = Math.pow(z,2);
        return (float)Math.sqrt(x_squared + y_squared + z_squared);
    }
}
