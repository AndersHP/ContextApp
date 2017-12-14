package com.example.ulrich.contextapp.widgets;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by Anders on 26-11-2017.
 */

public class MicrophoneWidget {
    private MediaRecorder mRecorder;
    private boolean isStarted = false;

    public MicrophoneWidget()
    {
        mRecorder = new MediaRecorder();
    }

    public double getLastAmplitudeReading()
    {
        double amplitude =  mRecorder.getMaxAmplitude();
        return amplitude;
    }

    public void start()
    {
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

        mRecorder.getMaxAmplitude(); // first reading is always 0, so call it once after setting up
        isStarted = true;
    }

    public void stop()
    {
        mRecorder.stop();
        mRecorder.reset();
        isStarted = false;
    }

    public boolean isStarted(){
        return isStarted;
    }
}