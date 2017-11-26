package com.example.ulrich.contextapp;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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

    AccelerometerWidget widget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // initialize dropdown
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"run", "walk", "stand"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            boolean isCollecting = false;
            public void onClick(View v) {

                // TODO:
                // Tell widget to start collecting data
                String className = dropdown.getSelectedItem().toString();

               // widget = new AccelerometerWidget((SensorManager)getSystemService(Context.SENSOR_SERVICE));


                // change button text
                Log.d("Button", "button pressed");
                isCollecting = !isCollecting;
                if(isCollecting){
                    widget = new AccelerometerWidget((SensorManager)getSystemService(Context.SENSOR_SERVICE),className);
                    button.setText("Stop collecting");
                }else{
                    widget = null;
                    button.setText("Start collecting");
                }
            }
        });
    }
}
