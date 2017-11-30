package com.example.ulrich.contextapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ClassifierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        Log.d("hey", "created new Classifier activity");

        setListeners();
    }

    private void setListeners()
    {
        final Button button2 = (Button) findViewById(R.id.btncollect);
        button2.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent myIntent = new Intent( ClassifierActivity.this, MainActivity.class);
                ClassifierActivity.this.startActivity(myIntent);
            }
        });
    }
}
