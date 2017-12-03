package com.example.ulrich.contextapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ulrich.contextapp.actuators.VolumeActuator;

public class ClassifierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        Log.d("hey", "created new Classifier activity");

        VolumeActuator v = new VolumeActuator((AudioManager)getSystemService(Context.AUDIO_SERVICE));
        v.setVolume();
        setListeners();
    }

    private void setListeners()
    {
        final Button button2 = (Button) findViewById(R.id.btncollect);
        button2.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent( ClassifierActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}
