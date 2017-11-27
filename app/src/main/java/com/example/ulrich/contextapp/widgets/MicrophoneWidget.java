package com.example.ulrich.contextapp.widgets;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by Anders on 26-11-2017.
 */

public class MicrophoneWidget {
    private MediaRecorder mRecorder;

    public MicrophoneWidget()
    {

        mRecorder = new MediaRecorder();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mRecorder.setOutputFile("/dev/null");
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();

        mRecorder.getMaxAmplitude();;
    }

    public double getLastAmplitudeReading()
    {

//listening to the user
        double amplitude =  mRecorder.getMaxAmplitude();
        double amplitudeDb = 20 * Math.log10((double)Math.abs(amplitude));

        return amplitudeDb;
    }
}