package com.example.ulrich.contextapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ulrich.contextapp.datawindow.DataWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class ClassifierActivity extends AppCompatActivity {
    private Aggregator aggregator;
    // private Classif classifier;
    private J48 classifier;
    private EditText textField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        //classifier = new Classif((AudioManager)getSystemService(Context.AUDIO_SERVICE), (SensorManager) getSystemService(Context.SENSOR_SERVICE), getAssets());

        aggregator = new Aggregator((SensorManager) getSystemService(Context.SENSOR_SERVICE), 200, true);

        textField = findViewById(R.id.predictedClassFld);
        setListeners();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastUpdate = System.currentTimeMillis();

                while (true) {
                    // Wait abit between readings
                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastUpdate) > 1000*45) {
                        lastUpdate = curTime;

                        final DataWindow d = aggregator.getLastDataWindow();


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textField.setText(classify(d) + "");
                            }
                        });


                    }
                }

            }
        });

        t.start();

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

        final Button button3 = (Button) findViewById(R.id.startClassifybtn);
        button3.setOnClickListener(new View.OnClickListener(){
            boolean isCollecting = false;
            public void onClick(View v) {
                isCollecting = !isCollecting;

                if(isCollecting){

                    // When starting to collect, update classname and start a new thread
                    aggregator.setCurrentClass("-");
                    Thread thread = new Thread(aggregator);
                    aggregator.setCollecting(true);
                    thread.start();

                    button3.setText("Stop classifying");
                }else{
                    aggregator.setCollecting(false);
                    button3.setText("Start classifying");
                }
            }
        });
    }

    public double classify(DataWindow window)
    {
        Log.d("DATAWINDOW FOR PREDICTION: ", window + "");
       // DataWindow window = new DataWindow(23,(float) 13.2,10,4,10,24,10,"?");
        Instance newInstance = new Instance(8);

        Attribute hour = new Attribute("hourOfDay");
        Attribute minAccAtt = new Attribute("minAcc");
        Attribute maxAccAtt = new Attribute("maxAcc");
        Attribute sdDevAccAtt = new Attribute("sdDevAcc");
        Attribute minMicAtt = new Attribute("minMic");
        Attribute maxMicAtt = new Attribute("maxMic");
        Attribute stDevMicAtt = new Attribute("stDevAcc");
        FastVector classNames = new FastVector(5);
        classNames.addElement("cycle");//here you set all the classes that appear at the top of the .arff file later
        classNames.addElement("walkNoisy");
        classNames.addElement("walkSilent");
        classNames.addElement("run");
        classNames.addElement("stand");
        Attribute classAtt = new Attribute("class", classNames);

        FastVector attributes = new  FastVector(8);
        attributes.addElement(hour);
        attributes.addElement(minAccAtt);
        attributes.addElement(maxAccAtt);
        attributes.addElement(sdDevAccAtt);
        attributes.addElement(minMicAtt);
        attributes.addElement(maxMicAtt);
        attributes.addElement(stDevMicAtt);
        attributes.addElement(classAtt);
        //Create the Instances object
        Instances data = new Instances("Best.Context.app", attributes, 0);
        newInstance.setDataset(data);
        newInstance.setValue(0, window.hourOfDay);
        newInstance.setValue(1, window.minAcc);
        newInstance.setValue(2, window.maxAcc);
        newInstance.setValue(3, window.stDevMagAcc);
        newInstance.setValue(4, window.minMic);
        newInstance.setValue(5, window.maxMic);
        //   newInstance.setValue(7, "run");
        newInstance.setValue(6, window.stDevMic);

        data.add(newInstance);

        Log.d("CLASSIFY:", "trying to classify!");

        double prediction = -1;

        try{
            Instances trainingData;
            ArffLoader loader = new ArffLoader();
            loader.setSource( getAssets().open( "data.arff"));
            trainingData = loader.getDataSet();
            trainingData.setClassIndex(trainingData.numAttributes()- 1);
            classifier = new J48();
            classifier.buildClassifier(trainingData);
            File file = new File(android.os.Environment.getExternalStorageDirectory()+ File.separator + "mod" + ".model");
//            weka.core.SerializationHelper.write("m1.model", classifier);

            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file));
            oos.writeObject(classifier);
            oos.flush();


            data.setClassIndex(data.numAttributes() - 1);
            //  ObjectInputStream ois = new ObjectInputStream(assetMgr.open("m.model"));
            //  classifier = (J48) ois.readObject();

            prediction =  classifier.classifyInstance(data.firstInstance());

        }catch (Exception e){
            e.printStackTrace();
        }

        return prediction;
    }
}
