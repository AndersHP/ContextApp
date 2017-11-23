package com.example.ulrich.contextapp.arffcreator;

import com.example.ulrich.contextapp.datawindow.DataWindow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import weka.filters.unsupervised.instance.NonSparseToSparse;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

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

    }
}
