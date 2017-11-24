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
        FastVector atts = new FastVector();
        atts.addElement(new Attribute("min"));
        atts.addElement(new Attribute("max"));
        atts.addElement(new Attribute("stdDev"));
        FastVector attVals = new FastVector();
        attVals.addElement("running");
        attVals.addElement("walking");
        attVals.addElement("standing");
        atts.addElement(new Attribute("class",attVals));


        Instances data = new Instances("ContextApp", atts, 0);

        for(DataWindow window : dataWindows)
        {
            double[] vals = new double[data.numAttributes()];
            vals[0] = window.min;
            vals[1] = window.max;
            vals[2] = window.stDevMag;
            data.add(new Instance(1.0, vals));

        }
        return data;
    }
}
