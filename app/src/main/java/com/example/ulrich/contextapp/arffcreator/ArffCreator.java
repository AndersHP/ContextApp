package com.example.ulrich.contextapp.arffcreator;

import android.util.Log;

import com.example.ulrich.contextapp.datawindow.DataWindow;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.FastVector;
/**
 * Created by Anders on 23-11-2017.
 */

public class ArffCreator {

    public static void saveArff(List<DataWindow> dataWindows, String fileName){
        Instances sparseDataset = createArff(dataWindows);
        ArffSaver arffSaverInstance = new ArffSaver();

        arffSaverInstance.setInstances(sparseDataset);
        File file = new File(android.os.Environment.getExternalStorageDirectory()+ File.separator + fileName + ".arff");
        try{
            arffSaverInstance.setFile(file);
            arffSaverInstance.writeBatch();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static Instances createArff(List<DataWindow> dataWindows)
    {
        Attribute hour = new Attribute("hourOfDay");
        Attribute minAccAtt = new Attribute("minAcc");
        Attribute maxAccAtt = new Attribute("maxAcc");
        Attribute sdDevAccAtt = new Attribute("sdDevAcc");
        Attribute minMicAtt = new Attribute("minMic");
        Attribute maxMicAtt = new Attribute("maxMic");
        Attribute stDevMicAtt = new Attribute("stDevAcc");

        FastVector classNames = new FastVector(5);
        classNames.addElement("stand");                    //here you set all the classes that appear at the top of the .arff file later
        classNames.addElement("walkNoisy");
        classNames.addElement("walkSilent");
        classNames.addElement("run");
        classNames.addElement("cycle");
        Attribute classAtt = new Attribute("class", classNames);
        // Declare the feature vector
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
        Instances data = new Instances("Window", attributes, 0);



        for(DataWindow window : dataWindows)
        {
            Instance newInstance = new Instance(8);
            newInstance.setValue(hour, window.hourOfDay);
            newInstance.setValue(minAccAtt, window.minAcc);
            newInstance.setValue(maxAccAtt, window.maxAcc);
            newInstance.setValue(sdDevAccAtt, window.stDevMagAcc);
            newInstance.setValue(minMicAtt, window.minMic);
            newInstance.setValue(maxMicAtt, window.maxMic);
            newInstance.setValue(stDevMicAtt, window.stDevMic);
            newInstance.setValue(classAtt, window.className);

            data.add(newInstance);
        }
        return data;
    }
}
