package com.example.ulrich.contextapp.widgets;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaRecorder;

/**
 * Created by Anders on 26-11-2017.
 */

public class MicrophoneWidget {
    private MediaRecorder mRecorder;

    public MicrophoneWidget()
    {
        mRecorder = new MediaRecorder();
    }

    public double getLastAmplitudeReading()
    {
        return mRecorder.getMaxAmplitude();
    }
}
