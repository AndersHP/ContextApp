package com.example.ulrich.contextapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClassifierActivity extends AppCompatActivity {

    private Classif classifier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        classifier = new Classif((AudioManager)getSystemService(Context.AUDIO_SERVICE), (SensorManager) getSystemService(Context.SENSOR_SERVICE), getAssets());
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
