package com.example.ulrich.contextapp.datawindow;

/**
 * Representing one datapoint of a sliding window
 */
public class DataWindow {
    public float  minAcc, maxAcc, stDevMagAcc, avgMicAmplitude ;
    public double avgSpeed;
    public String className;

    public DataWindow(float minAcc, float maxAcc, float stDevMagAcc, float avgMicAmplitude, double avgSpeed, String className)
    {
        this.minAcc = minAcc;
        this.maxAcc = maxAcc;
        this.stDevMagAcc = stDevMagAcc;
        this.avgMicAmplitude = avgMicAmplitude;
        this.avgSpeed = avgSpeed;
        this.className = className;
    }

    public String toString()
    {
        return "minAcc: " + minAcc + " maxAcc: " + maxAcc + " stDevMagAcc: " + stDevMagAcc + "AvgMicAmp:"+ avgMicAmplitude + "avgSpeed: " + avgSpeed +  " className: " + className;
    }
}
