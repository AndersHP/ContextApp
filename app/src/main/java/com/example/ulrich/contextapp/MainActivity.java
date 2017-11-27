package com.example.ulrich.contextapp;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;


import com.example.ulrich.contextapp.widgets.AccelerometerWidget;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Aggregator aggregator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aggregator = new Aggregator((SensorManager) getSystemService(Context.SENSOR_SERVICE));

        // initialize dropdown
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"run", "walkNoisy", "walkSilent", "cycle"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);


        final Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            boolean isCollecting = false;
            public void onClick(View v) {

                // TODO:
                // Tell widget to start collecting data
                String className = dropdown.getSelectedItem().toString();


                // change button text
                Log.d("Button", "button pressed");
                isCollecting = !isCollecting;
                if(isCollecting){
                    aggregator.startCollecting(className);
                    button.setText("Stop collecting");
                }else{
                    aggregator.stopCollecting();
                    button.setText("Start collecting");
                }
            }
        });

    }
}
