/**
Setup windows for showing or selecting directories and parameters. 

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/

import ij.*;
import ij.plugin.*; 
import ij.gui.GenericDialog; 
import java.awt.*; 
import java.io.*; // File

public class DynaMiT_Setup implements PlugIn {
    /*public void run(String arg) {
        DynaMiTProperty dynaMiTProp = new DynaMiTProperty();
        String pluginPath = dynaMiTProp.PLUGIN_PATH;
        String homePath = dynaMiTProp.HOME_PATH;
        GenericDialog genDiag = new GenericDialog();
        genDiag.
        return; 
    }
    */
    static double STACK_DELAY; 
    static double PIXEL_SIZE; 
    static double REGRESS_THRESHOLD; 
    static double BANDWITH; 
    static String DATA_PATH; 
    static String RESULT_PATH; 
    static String IMAGE_PROCESSING_METHOD; 
    static double THRESHOLD_PAUSE; 
    static char mu = '\u03BC'; 
    
    public void run(String arg) {
        initParams(); 
        DynaMiTActionListener actListenData = new DynaMiTActionListener(); 
        GenericDialog gd = new GenericDialog("Setup");
        GridBagLayout gbl = (GridBagLayout) gd.getLayout(); 
        GridBagConstraints gbc;
        gd.addStringField("Data path: ", DATA_PATH, 60);
        Button bChooseDataPath = new Button("..."); 
        bChooseDataPath.setActionCommand("getDirectoryData");
        bChooseDataPath.addActionListener(actListenData);
        gbc = gbl.getConstraints(gd); 
        gbc.gridx = 3; 
        gbc.gridy = 0; 
        gbl.setConstraints(bChooseDataPath, gbc); 
        gd.add(bChooseDataPath); 
        gd.addStringField("Result path: ", RESULT_PATH, 60); 
        Button bChooseResultPath = new Button("..."); 
        bChooseResultPath.setActionCommand("getDirectoryResult");
        bChooseResultPath.addActionListener(actListenData);
        gbc = gbl.getConstraints(gd); 
        gbc.gridx = 3; 
        gbc.gridy = 1; 
        gbl.setConstraints(bChooseResultPath, gbc); 
        gd.add(bChooseResultPath);
        // gd.addNumericField("Threshold for regression: ", REGRESS_THRESHOLD, 2, 
        //     4, "" + mu + "m");
        gd.addNumericField("Bandwith around microtubule: ", BANDWITH, 2, 4, 
            "pixel");
        gd.addNumericField("Pixel size: ", PIXEL_SIZE, 2, 4, "" + mu + "m");
        gd.addNumericField("Frame interval: ", STACK_DELAY, 2, 4, "s");
        String[] method = {"Median - Gaussian", "FFT"}; 
        gd.addChoice("processing method: ", method, IMAGE_PROCESSING_METHOD);
        gd.addNumericField("Threshold pause: ", THRESHOLD_PAUSE, 4, 6, 
            "" + mu + "m / s");
        gd.showDialog();
        if (gd.wasCanceled()) return;
        DATA_PATH = gd.getNextString();
        RESULT_PATH = gd.getNextString();
        checkOrCreate(RESULT_PATH); 
        // REGRESS_THRESHOLD = gd.getNextNumber();
        BANDWITH = gd.getNextNumber();
        PIXEL_SIZE = gd.getNextNumber(); 
        STACK_DELAY = gd.getNextNumber(); 
        IMAGE_PROCESSING_METHOD = gd.getNextChoice();
        THRESHOLD_PAUSE = gd.getNextNumber(); 
        saveUserProperties(); 
    }
    
    void initParams() {
        DynaMiTProperty dynaMiTProp = new DynaMiTProperty();
        STACK_DELAY = Double.valueOf(
            dynaMiTProp.USER_PROP.getProperty("STACK_DELAY")); 
        PIXEL_SIZE = Double.valueOf(
            dynaMiTProp.USER_PROP.getProperty("PIXEL_SIZE")); 
        // REGRESS_THRESHOLD = Double.valueOf(
        //    dynaMiTProp.USER_PROP.getProperty("REGRESS_THRESHOLD"));
        BANDWITH = Double.valueOf(
            dynaMiTProp.USER_PROP.getProperty("BANDWITH"));
        DATA_PATH = dynaMiTProp.USER_PROP.getProperty("DATA_PATH");
        RESULT_PATH = dynaMiTProp.USER_PROP.getProperty("RESULT_PATH");
        IMAGE_PROCESSING_METHOD = 
            dynaMiTProp.USER_PROP.getProperty("IMAGE_PROCESSING_METHOD");
        THRESHOLD_PAUSE = Double.valueOf(
            dynaMiTProp.USER_PROP.getProperty("THRESHOLD_PAUSE"));
        return; 
    }
    
    void checkOrCreate(String pathnameFolder) {
        File folder = new File(pathnameFolder);
        if (folder.exists()) return; 
        else { 
            try {folder.mkdir();}
            catch (Exception e) {} 
        }
    }
    
    void saveUserProperties() {
        DynaMiTProperty dynaMiTProp = new DynaMiTProperty();
        dynaMiTProp.USER_PROP.setProperty("STACK_DELAY", "" + STACK_DELAY); 
        dynaMiTProp.USER_PROP.setProperty("PIXEL_SIZE", "" + PIXEL_SIZE); 
        dynaMiTProp.USER_PROP.setProperty("REGRESS_THRESHOLD", "" + 
            REGRESS_THRESHOLD); 
        dynaMiTProp.USER_PROP.setProperty("BANDWITH", "" + BANDWITH); 
        dynaMiTProp.USER_PROP.setProperty("DATA_PATH", DATA_PATH); 
        dynaMiTProp.USER_PROP.setProperty("RESULT_PATH", RESULT_PATH);   
        dynaMiTProp.USER_PROP.setProperty("TRESHOLD_PAUSE", 
            "" + THRESHOLD_PAUSE);   
        dynaMiTProp.USER_PROP.setProperty("IMAGE_PROCESSING_METHOD", 
            IMAGE_PROCESSING_METHOD);   
        dynaMiTProp.saveUserProperties(); 
    }
}
