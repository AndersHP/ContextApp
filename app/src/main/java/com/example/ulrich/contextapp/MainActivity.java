package com.example.ulrich.contextapp;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
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
        Log.d("MAINACTIVIT",    "MANACTIVITY ONCREATE CALLED");
        aggregator = new Aggregator((SensorManager) getSystemService(Context.SENSOR_SERVICE));

        setListeners();
    }

    private void setListeners()
    {

        // initialize dropdown
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"run", "walkNoisy", "walkSilent", "cycle","stand"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);


        final Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            boolean isCollecting = false;
            public void onClick(View v) {

                String className = dropdown.getSelectedItem().toString();

                isCollecting = !isCollecting;
                if(isCollecting){

                    // When starting to collect, update classname and start a new thread
                    aggregator.setCurrentClass(className);
                    Thread thread = new Thread(aggregator);
                    aggregator.setCollecting(true);
                    thread.start();

                    button.setText("Stop collecting");
                }else{
                    aggregator.setCollecting(false);
                    button.setText("Start collecting");
                }
            }
        });

        final Button button2 = (Button) findViewById(R.id.btnclassify);
        button2.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent myIntent = new Intent(MainActivity.this, ClassifierActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }
}
