package com.example.ulrich.contextapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.ulrich.contextapp.actuators.VolumeActuator;
import com.example.ulrich.contextapp.datawindow.DataWindow;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class ClassifierActivity extends AppCompatActivity {

    private Aggregator aggregator;
    private Classifier classifier;
    private EditText textField;
    // Maps to turn a prediction into a class name, and a class name into a boolean indicating a high or low volume
    private HashMap<Double, String> classMap = new HashMap<>();
    private HashMap<String, Boolean> volumeMap = new HashMap<>();
    private VolumeActuator volumeActuator;
    boolean isCollecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);

        classMap.put(0.0, "cycle");
        classMap.put(1.0, "walkNoisy");
        classMap.put(2.0, "walkSilent");
        classMap.put(3.0, "run");
        classMap.put(4.0, "stationarySilent");
        classMap.put(5.0, "stationaryNoisy");

        volumeMap.put("cycle",Boolean.TRUE);
        volumeMap.put("walkNoisy",Boolean.TRUE);
        volumeMap.put("walkSilent",Boolean.FALSE);
        volumeMap.put("run",Boolean.TRUE);
        volumeMap.put("stationarySilent",Boolean.FALSE);
        volumeMap.put("stationaryNoisy",Boolean.TRUE);

        aggregator = new Aggregator((SensorManager) getSystemService(Context.SENSOR_SERVICE), 200, true, this);
        volumeActuator = new VolumeActuator((AudioManager) getSystemService(Context.AUDIO_SERVICE));

        // Train model in code based on the data in the asset folder
        trainModel();

        textField = findViewById(R.id.predictedClassFld);
        setListeners();
    }

    private void startLabelUpdaterThread(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastUpdate = System.currentTimeMillis();

                while (isCollecting) {
                    // Wait abit between readings
                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastUpdate) > 1000*30) {
                        lastUpdate = curTime;

                        final DataWindow d = aggregator.getLastDataWindow();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String predictedClass = classMap.get(classify(d));
                                textField.setText(predictedClass);
                                volumeActuator.setVolume(volumeMap.get(predictedClass));
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
        final Button button3 = (Button) findViewById(R.id.startClassifybtn);
        button2.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                isCollecting = false;
                button3.setText("Start classifying");
                aggregator.setCollecting(false);
                Intent intent = new Intent( ClassifierActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        
        button3.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                isCollecting = !isCollecting;

                if(isCollecting){

                    // When starting to collect, update classname and start a new thread
                    aggregator.setCurrentClass("-");
                    Thread thread = new Thread(aggregator);
                    aggregator.setCollecting(true);
                    thread.start();
                    startLabelUpdaterThread();
                    button3.setText("Stop classifying");
                }else{
                    aggregator.setCollecting(false);
                    button3.setText("Start classifying");
                }
            }
        });
    }

    private void trainModel()
    {
        try{
            Instances trainingData;
            ArffLoader loader = new ArffLoader();
            loader.setSource( getAssets().open( "data.arff"));
            trainingData = loader.getDataSet();
            trainingData.setClassIndex(trainingData.numAttributes()- 1);
            classifier = new J48();
            classifier.buildClassifier(trainingData);
            File file = new File(android.os.Environment.getExternalStorageDirectory()+ File.separator + "mod" + ".model");

            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file));
            oos.writeObject(classifier);
            oos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double classify(DataWindow window)
    {
        Log.d("DATAWINDOW FOR PRED: ", window + "");
        Instance newInstance = new Instance(6);

        Attribute minAccAtt = new Attribute("minAcc");
        Attribute maxAccAtt = new Attribute("maxAcc");
        Attribute sdDevAccAtt = new Attribute("sdDevAcc");
        Attribute avgMicAmp = new Attribute("avgMicAmp");
        Attribute avgSpeed = new Attribute("avgSpeed");
        FastVector classNames = new FastVector(6);
        classNames.addElement("cycle");//here you set all the classes that appear at the top of the .arff file later
        classNames.addElement("walkNoisy");
        classNames.addElement("walkSilent");
        classNames.addElement("run");
        classNames.addElement("stationarySilent");
        classNames.addElement("stationaryNoisy");
        Attribute classAtt = new Attribute("class", classNames);

        FastVector attributes = new  FastVector(8);
        attributes.addElement(minAccAtt);
        attributes.addElement(maxAccAtt);
        attributes.addElement(sdDevAccAtt);
        attributes.addElement(avgMicAmp);
        attributes.addElement(avgSpeed);
        attributes.addElement(classAtt);
        //Create the Instances object
        Instances data = new Instances("Best.Context.app", attributes, 0);
        newInstance.setDataset(data);
        newInstance.setValue(1, window.minAcc);
        newInstance.setValue(2, window.maxAcc);
        newInstance.setValue(3, window.stDevMagAcc);
        newInstance.setValue(4, window.avgMicAmplitude);
        newInstance.setValue(5, window.avgSpeed);

        data.add(newInstance);

        Log.d("CLASSIFY:", "trying to classify!");

        double prediction = -1;
        try{
            data.setClassIndex(data.numAttributes() - 1);
            prediction =  classifier.classifyInstance(data.firstInstance());
        }catch (Exception e){
            e.printStackTrace();
        }

        return prediction;
    }
}
