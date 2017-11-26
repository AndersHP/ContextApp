package com.example.ulrich.contextapp.arffcreator;

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

    public static void saveArff(List<DataWindow> dataWindows, String fileName, String className){
        Instances sparseDataset = createArff(dataWindows,className);
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

    private static Instances createArff(List<DataWindow> dataWindows, String className)
    {
        Attribute minAtt = new Attribute("min");
        Attribute maxAtt = new Attribute("max");
        Attribute sdAtt = new Attribute("sd");
        FastVector classNames = new FastVector(3);
        classNames.addElement("stand");                    //here you set all the classes that appear at the top of the .arff file later
        classNames.addElement("walk");
        classNames.addElement("run");
        Attribute classAtt = new Attribute("class", classNames);
// Declare the feature vector
        FastVector attributes = new  FastVector(4);
        attributes.addElement(minAtt);
        attributes.addElement(maxAtt);
        attributes.addElement(sdAtt);
        attributes.addElement(classAtt);
//Create the Instances object
        Instances data = new Instances("Window", attributes, 0);



        for(DataWindow window : dataWindows)
        {
            Instance newInstance = new Instance(4);
            newInstance.setValue(minAtt, window.min);
            newInstance.setValue(maxAtt, window.max);
            newInstance.setValue(sdAtt, window.stDevMag);
            newInstance.setValue(classAtt, className);
            //double[] vals = new double[data.numAttributes()];
            //vals[0] = window.min;
            //vals[1] = window.max;
            //vals[2] = window.stDevMag;
            data.add(newInstance    );

        }
        return data;
    }
}
