package com.example.ulrich.contextapp.actuators;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Anders on 02-12-2017.
 */

public class VolumeActuator {
    private AudioManager audioManager;

    public VolumeActuator(AudioManager audioManager){
        this.audioManager = audioManager;
    }

    public void setVolume(boolean high){

        // Only max volume or mute
        int volume = high ? audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) : 0;


        // AudioManager.FLAG_SHOW_UI                     AudioManager.FLAG_PLAY_SOUND
        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume,AudioManager.FLAG_SHOW_UI );
    }
}
