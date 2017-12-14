package com.example.ulrich.contextapp.actuators;

import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Anders on 02-12-2017.
 */

public class VolumeActuator {
    private AudioManager audioManager;

    public VolumeActuator(AudioManager audioManager){
        this.audioManager = audioManager;
    }

    public void setVolume(boolean high){
        // If high is true, use max volume, else use volume 1
        int volume = high ? audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) : 1;

        Log.d("VOLUME:" ,"setting volume: " + volume);

        // AudioManager.FLAG_SHOW_UI                     AudioManager.FLAG_PLAY_SOUND
        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume,AudioManager.FLAG_SHOW_UI );
    }
}
