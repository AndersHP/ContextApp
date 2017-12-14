package com.example.ulrich.contextapp.arffcreator;


import com.example.ulrich.contextapp.datawindow.DataWindow;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
        Attribute minAccAtt = new Attribute("minAcc");
        Attribute maxAccAtt = new Attribute("maxAcc");
        Attribute sdDevAccAtt = new Attribute("sdDevAcc");
        Attribute avgMicAmplitude = new Attribute("avgMicAmplitude");
        Attribute avgSpeed = new Attribute("avgSpeed");

        FastVector classNames = new FastVector(6);

        classNames.addElement("cycle");//here you set all the classes that appear at the top of the .arff file later
        classNames.addElement("walkNoisy");
        classNames.addElement("walkSilent");
        classNames.addElement("run");
        classNames.addElement("stationarySilent");
        classNames.addElement("stationaryNoisy");

        Attribute classAtt = new Attribute("class", classNames);
        // Declare the feature vector
        FastVector attributes = new  FastVector(6);
        attributes.addElement(minAccAtt);
        attributes.addElement(maxAccAtt);
        attributes.addElement(sdDevAccAtt);
        attributes.addElement(avgMicAmplitude);
        attributes.addElement(avgSpeed);
        attributes.addElement(classAtt);
        //Create the Instances object
        Instances data = new Instances("Best.Context.app", attributes, 0);

        for(DataWindow window : dataWindows)
        {
            Instance newInstance = new Instance(6);
            newInstance.setValue(minAccAtt, window.minAcc);
            newInstance.setValue(maxAccAtt, window.maxAcc);
            newInstance.setValue(sdDevAccAtt, window.stDevMagAcc);
            newInstance.setValue(avgMicAmplitude, window.avgMicAmplitude);
            newInstance.setValue(avgSpeed, window.avgSpeed);
            newInstance.setValue(classAtt, window.className);

            data.add(newInstance);
        }
        return data;
    }
}
