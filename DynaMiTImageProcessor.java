/**
Image processing methods

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/

import ij.*; 
import ij.plugin.*; 
import java.util.*; 
import ij.io.*;
import ij.gui.*; 

public class DynaMiTImageProcessor {
    
    // Field
    
    static int USE_STACK_REG; 
    static String RESULT_PATH; 
    static String IMAGE_PROCESSING_METHOD;
    static boolean SHOW = false; 
    static boolean SHOW_LOGGER = false; 
    static String logger = ""; 
    static DynaMiTProperty dynaMiTProp = new DynaMiTProperty(); 
    static String pathname; 

    // Method

    public static void run(String[] filenameToProcess) {
        logger = DynaMiTUtil.loggerStart(logger, 
            "Microtubule Image Processing"); 
        // one filename corresponds to a file containing an ImageStack
        int nFile = filenameToProcess.length;
        int iFile; 
        String filename; 
        if (nFile == 0) return; 
        for (iFile = 0; iFile < nFile; iFile++) {
            filename = filenameToProcess[iFile]; 
            processFile(filename); 
        }
        if (SHOW_LOGGER) {IJ.showMessage(logger);} 
        DynaMiTUtil.loggerEnd(logger, pathname); 
    }

    public static void processFile(String filename) {
        ImagePlus impOriginal = new ImagePlus(filename);
        int nSlices = impOriginal.getNSlices();
        impOriginal.close(); 
        if (nSlices == 0) return; 
        // USE_STACK_REG = (int) dynaMiTProp.USER_PROP.getProperty("USE_STACK_REG");
        RESULT_PATH = dynaMiTProp.USER_PROP.getProperty("RESULT_PATH");
        // RESULT_PATH = "D:\\Users\\guillaume\\Documents\\resultsDynaMiT"; 
        // USE_STACK_REG = (int) dynaMiT.USER_PROP.getProperty("USE_STACK_REG");
        // TODO Manage USE_STACK_REG
        // STACK_REG is used when stack are moving during the recording. 
        USE_STACK_REG = 0; 
        // IMAGE_PROCESSING_METHOD = (int) dynaMiT.USER_PROP.getProperty(
        //     "IMAGE_PROCESSING_METHOD");
        IMAGE_PROCESSING_METHOD = dynaMiTProp.USER_PROP.getProperty(
            "IMAGE_PROCESSING_METHOD");
        ImagePlus impResult; 
        if (IMAGE_PROCESSING_METHOD.equals("Median - Gaussian"))
            impResult = processFileMedianMinusGaussian(filename); 
        else if (IMAGE_PROCESSING_METHOD.equals("FFT")) 
            impResult = processFileFFT(filename); 
        else impResult = new ImagePlus(filename); 
        saveImpResult(RESULT_PATH, filename, impResult, 
            IMAGE_PROCESSING_METHOD); 
        // Apply Z Projection on impResult and save as *_ZProjection.tif
        impResult = doZProjection(impResult); 
        saveImpResult(RESULT_PATH, filename, impResult, 
            IMAGE_PROCESSING_METHOD + "_ZProjection"); 
        if (!SHOW) {impResult.close(); } 
        return; 
    }
        
    public static ImagePlus doZProjection(ImagePlus imp) {
        // Z projection
        ImagePlus impZProj; 
        String strParam = "start=1 stop=" + 
            imp.getImageStackSize() + " projection=[Max Intensity]";
        IJ.run(imp, "Z Project...", strParam);
        impZProj = WindowManager.getCurrentImage(); 
        // IJ.runPlugIn(imp, "AmContraste", "");
        IJ.run(impZProj, "Enhance Contrast...", "saturated=0 equalize");
        if (SHOW) {impZProj.show(); }
        return impZProj; 
    }

    public static ImagePlus processFileFFT(String filename) {
        ImagePlus impOriginal = new ImagePlus(filename);
        ImagePlus impResult = impOriginal.duplicate();
        IJ.run(impResult, "Bandpass Filter...", "filter_large=10 filter_small=3 " + 
            "suppress=None tolerance=5 autoscale saturate process");
        logger += "FFT param: \n" + 
            " Bandpass Filter..., \n" + 
            "  filter_large=10 filter_small=3 \n" + 
            "  suppress=None tolerance=5 autoscale saturate process \n";  
        if (SHOW) {impResult.show();} 
        if (USE_STACK_REG == 0) {}; 
        return impResult; 
    }    
    
    public static ImagePlus processFileMedianMinusGaussian(String filename) {
        ImagePlus impOriginal = new ImagePlus(filename);
        ImagePlus impBlurGaussian = impOriginal.duplicate();
        ImagePlus impMedian = impOriginal.duplicate();
        ImagePlus impResult = new ImagePlus(); 
        IJ.run(impBlurGaussian, "Gaussian Blur...", "stack sigma=10");
        if (SHOW) {impBlurGaussian.show();} 
        IJ.run(impMedian, "Median...", "stack radius=1 slice");
        logger += "Median - Gaussian param: \n" +  
            " Gaussian Blur... \n" + 
            "  stack sigma=10 \n" + 
            " Median... \n" + 
            "  stack radius=1 slice \n";  
        if (SHOW) {impMedian.show();} 
        ImageCalculator ic = new ImageCalculator();
        impResult = ic.run("Subtract create stack", impMedian, impBlurGaussian);
        if (SHOW) {impResult.show();}
        if (USE_STACK_REG == 0) {}; 
        if (!SHOW) {impBlurGaussian.close();}
        if (!SHOW) {impMedian.close();} 
        return impResult; 
    }
        
    public static String saveImpResult(String pathResBase, String filename, 
        ImagePlus impResult, String method) {
        String sep = Prefs.separator; 
        String basename, resultBasename, resultFilename;
        basename = DynaMiTUtil.getBasename(filename); 
        pathname = pathResBase + basename + sep; 
        DynaMiTUtil.checkCreateDir(pathname); 
        resultBasename = basename + "_" + method + ".tif"; 
        resultFilename = pathname + resultBasename;
        logger += "resultFilename: " + resultFilename + "\n";
        // FileSaver fsave = new FileSaver(impResult);
        try {
            IJ.saveAsTiff(impResult, resultFilename); 
            // fsave.saveAsTiff(resultFilename);
        }
        catch (Exception e) {
            logger += "Problem while saving " + resultFilename + "\n"; 
        }
        return resultFilename;
    }

}

