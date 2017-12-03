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

    public void setVolume(){

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

        int minVolum = 0; // Tror jeg


        // AudioManager.FLAG_SHOW_UI                     AudioManager.FLAG_PLAY_SOUND
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume,AudioManager.FLAG_SHOW_UI ); // Siger bip når volume ændres
    }
}
