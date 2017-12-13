package com.example.ulrich.contextapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.util.Log;

import com.example.ulrich.contextapp.actuators.VolumeActuator;
import com.example.ulrich.contextapp.datawindow.DataWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Created by Anders on 04-12-2017.
 */

public class Classif
{
    private VolumeActuator volumeActuator;
    private Aggregator aggregator;
    private String lastPredictedClass = "";
    private AssetManager assetMgr;
    private Classifier classifier;

    public Classif(AudioManager audioManager, SensorManager sensorManager, AssetManager assetMgr)
    {
        this.assetMgr = assetMgr;
        aggregator = new Aggregator(sensorManager, 200, true);

        volumeActuator = new VolumeActuator(audioManager);
        // Only classify every 0.2 * 128 second
       // aggregator = new Aggregator(sensorManager, 200, true);
        startClassifying();
    }

    public void startRecievingData()
    {
        aggregator.setCurrentClass("-");
        Thread thread = new Thread(aggregator);
        aggregator.setCollecting(true);
        thread.start();
    }

    public void startClassifying()
    {
        DataWindow window = new DataWindow(22,10,4,10,"?");
        Instance newInstance = new Instance(5);

        Attribute minAccAtt = new Attribute("minAcc");
        Attribute maxAccAtt = new Attribute("maxAcc");
        Attribute sdDevAccAtt = new Attribute("sdDevAcc");
        Attribute avgMicAmp = new Attribute("avgMicAmp");
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
        attributes.addElement(classAtt);
        //Create the Instances object
        Instances data = new Instances("Best.Context.app", attributes, 0);
        newInstance.setDataset(data);
        newInstance.setValue(1, window.minAcc);
        newInstance.setValue(2, window.maxAcc);
        newInstance.setValue(3, window.stDevMagAcc);
        newInstance.setValue(4, window.avgMicAmplitude);

        data.add(newInstance);

        Log.d("CLASSIFY:", "trying to classify!");



        try{
            Instances trainingData;
            ArffLoader loader = new ArffLoader();
            loader.setSource( assetMgr.open( "data.arff"));
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

            double d = classifier.classifyInstance(data.firstInstance());
       //     ois.close();
            Log.d("CLASSIFY:", "SUCCES, predicted class:" + d);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
