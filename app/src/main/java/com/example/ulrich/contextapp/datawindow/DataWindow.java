package com.example.ulrich.contextapp.datawindow;

/**
 * Created by Anders on 23-11-2017.
 */

public class DataWindow {
    public float  minAcc, maxAcc, stDevMagAcc, avgMicAmplitude ;
    public String className;

    public DataWindow(float minAcc, float maxAcc, float stDevMagAcc, float avgMicAmplitude, String className)
    {

        this.minAcc = minAcc;
        this.maxAcc = maxAcc;
        this.stDevMagAcc = stDevMagAcc;
        this.avgMicAmplitude = avgMicAmplitude;
        this.className = className;
    }

    public String toString()
    {
        return "minAcc: " + minAcc + " maxAcc: " + maxAcc + " stDevMagAcc: " + stDevMagAcc + "AvgMicAmp:"+ avgMicAmplitude + " className: " + className;
    }
}
