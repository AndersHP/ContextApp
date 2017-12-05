package com.example.ulrich.contextapp.datawindow;

/**
 * Created by Anders on 23-11-2017.
 */

public class DataWindow {
    public float hourOfDay, minAcc, maxAcc, stDevMagAcc, minMic, maxMic, stDevMic ;
    public String className;

    public DataWindow(float hourOfDay, float minAcc, float maxAcc, float stDevMagAcc, float minMic,float maxMic, float stDevMic, String className)
    {
        this.hourOfDay = hourOfDay;
        this.minAcc = minAcc;
        this.maxAcc = maxAcc;
        this.stDevMagAcc = stDevMagAcc;
        this.minMic = minMic;
        this.maxMic = maxMic;
        this.stDevMic = stDevMic;
        this.className = className;
    }

    public String toString()
    {
        return "Hour of day: " + hourOfDay + " minAcc: " + minAcc + " maxAcc: " + maxAcc + " stDevMagAcc: " + stDevMagAcc + " minMic: " + minMic + " maxMic: " + maxMic + " stDevmic: " + stDevMic + " className: " + className;
    }
}
