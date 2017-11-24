package com.example.ulrich.contextapp;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.example.ulrich.contextapp.widgets.AccelerometerWidget;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccelerometerWidget widget = new AccelerometerWidget((SensorManager)getSystemService(Context.SENSOR_SERVICE));
    }
}
