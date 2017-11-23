package com.example.ulrich.contextapp.datawindow;

/**
 * Created by Anders on 23-11-2017.
 */

public class DataWindow {
    private float min, max, stDevMag;

    public DataWindow(float min, float max, float stDevMag)
    {
        this.min = min;
        this.max = max;
        this.stDevMag = stDevMag;
    }
}
