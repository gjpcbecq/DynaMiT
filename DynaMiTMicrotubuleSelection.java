/**
Microtubule selection 

Part of the code is due to students: Pierre Laurent, Morgane Daniel, and 
Anastasia Diep. 

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/
import ij.*; 
import java.util.*; 
import ij.gui.NonBlockingGenericDialog; 
import ij.gui.GenericDialog; 
import ij.gui.DialogListener; 
import ij.plugin.frame.RoiManager; 
import java.awt.AWTEvent; 
import java.io.File; 
import ij.gui.Roi; 
import ij.plugin.Straightener; 
import ij.process.ImageConverter; 
import ij.process.ImageProcessor; 
import ij.gui.NewImage; 
import ij.text.*; 

public class DynaMiTMicrotubuleSelection {
    
    // Field
    
    static String RESULT_PATH; 
    static boolean SHOW_LOGGER = false; 
    static String logger = ""; 
    static ImagePlus imp;
    static int BANDWITH; 
    static File fileResult; 
    static RoiManager roiMan; 
    static int nSlice; 
    static String filename; 
    static long time1, time2, dt; 
    static boolean SHOW = false; 
    static ImagePlus impStackKymoBW;
    static ImagePlus impStackKymoGray;
    static ImagePlus impStackMicrotubule;
    static String pathname; 
    static String basename; 
    static int DEFAULT_WIDTH = 1000; 
    static int widthMax; 
    
    // Method
    
    public static void run(String filenameZProj) {
        logger = DynaMiTUtil.loggerStart(logger, 
            "Microtubule Selection"); 
        widthMax = 0; 
        imp = new ImagePlus(filenameZProj);
        imp.show(); 
        // Check and create basename, filename and so on.
        String basenameZProj = DynaMiTUtil.getBasename(filenameZProj); 
        basename = DynaMiTUtil.getBasenameWithoutZProj(basenameZProj);
        pathname = DynaMiTUtil.getPathname(filenameZProj);
        filename = pathname + basename + ".tif";
        // Open ROIManager
        checkAndOpenROIManager(pathname); 
        IJ.setTool("polyline");
        // Creation of the GenericDialog. 
        NonBlockingGenericDialog nbgd = new NonBlockingGenericDialog(
            "ROI Selection");
        nbgd.addMessage("Select the microtubules of interests and click OK " +  
            "at the end of the selection \n");
        DynaMiTDialogListener dlistener; 
        dlistener = new DynaMiTDialogListener(); 
        nbgd.addDialogListener(dlistener);
        nbgd.showDialog();
        //------------------------------------------------
        // Stopped until OK in GenericDialog is pushed.
        //------------------------------------------------
        imp.close();
        if (nbgd.wasCanceled()) {
            roiMan.close();
            return; 
        }
        // exit if ROI manager is empty. 
        if (roiMan.getCount() == 0){
            roiMan.close();
            return; 
        }
        // Save the ROIs
        String filenameRoiMan = pathname + "RoiSet.zip"; 
        try {
            roiMan.runCommand("Save", filenameRoiMan);
            logger += "ROI saved into " + filenameRoiMan + " \n"; 
        }
        catch (Exception e) {
            logger += "Problem while recording ROI into " + filenameRoiMan + 
            " \n"; 
        } 
        roiMan.close();
        //____
        // Recovering Properties and processed images.  
        DynaMiTProperty dynaMiTProp = new DynaMiTProperty(); 
        String strBANDWITH = dynaMiTProp.USER_PROP.getProperty("BANDWITH");
        logger += "strBANDWIDTH: " + strBANDWITH + "\n";
        BANDWITH = (int) Double.parseDouble(strBANDWITH); 
        logger += "loading filename: " + filename + "\n"; 
        imp = new ImagePlus(filename);
        if (SHOW) {imp.show();} 
        nSlice = imp.getNSlices(); 
        //____
        //____
        // Computation on all ROI
        loopOnROI(); 
        //____
        if (SHOW) impStackKymoBW.show(); 
        if (SHOW) impStackKymoGray.show(); 
        if (SHOW) impStackMicrotubule.show(); 
        if (SHOW_LOGGER) IJ.showMessage(logger); 
        DynaMiTUtil.loggerEnd(logger, pathname); 
    }
    
    private static void loopOnROI() {
        int iROI ; 
        int nROI = roiMan.getCount(); 
        initImpStackMicrotubule(nROI); 
        initImpStackKymoGray(nROI); 
        initImpStackKymoBW(nROI);
        for (iROI = 0; iROI < nROI; iROI++){
            processOneROI(iROI); 
        }
        String filenameKymoGray, filenameKymoBW, filenameMicrotubule;
        String filenameResult, filenameStat; 
        filenameKymoGray = pathname + basename + "_KymoGray.tif"; 
        filenameKymoBW = pathname + basename + "_KymoBW.tif"; 
        filenameMicrotubule = pathname + basename + "_Microtubule.tif"; 
        filenameResult = pathname + basename + "_Res.csv"; 
        filenameStat = pathname + basename + "_Stats.csv";
        int heightImp = impStackKymoGray.getHeight(); 
        String argCanvasSize = "width=" + widthMax + " height=" + heightImp + 
            " position=Top-Left"; 
        IJ.run(impStackKymoGray, "Canvas Size...", argCanvasSize);
        IJ.run(impStackKymoBW, "Canvas Size...", argCanvasSize);
        argCanvasSize = "width=" + widthMax + 
            " height=" + impStackMicrotubule.getHeight()  + 
            " position=Top-Left"; 
        IJ.run(impStackMicrotubule, "Canvas Size...", argCanvasSize);
        IJ.saveAsTiff(impStackKymoGray, filenameKymoGray); 
        IJ.saveAsTiff(impStackKymoBW, filenameKymoBW); 
        IJ.saveAsTiff(impStackMicrotubule, filenameMicrotubule);
        return ; 
    }
    
    private static void processOneROI(int iROI){
        time1 = System.currentTimeMillis();
        if (SHOW) {imp.show();} 
        roiMan.select(imp, iROI); 
        Roi roi = imp.getRoi(); 
        String roiName = roi.getName(); 
        logger += "roiName: " + roiName + "\n"; 
        logger += "impName: " + imp.getTitle() + "\n"; 
        Straightener straightener = new Straightener();
        ImageStack roiStack = straightener.straightenStack(imp, roi, BANDWITH);
        String titleROI = "ROI_" + roiName; 
        ImagePlus impROI = new ImagePlus(titleROI, roiStack); 
        if (SHOW) {impROI.show();}
        // addROIToStackMicrotubule(iROI); 
        IJ.run(impROI, "Reslice [/]...", "output=1.000 start=Top avoid");
        ImagePlus impReslice =  WindowManager.getImage("Reslice of " + 
            titleROI);
        impReslice.hide(); 
        // while (IJ.macroRunning()) {;}
        addROIToStackMicrotubule(iROI, impROI); 
        // ImagePlus impReslice = WindowManager.getImage("Reslice of " + 
        //    titleROI);
        IJ.run(impReslice, "Z Project...", "start=1 stop=" + nSlice + 
            " projection=[Max Intensity]");
        impReslice.hide(); 
        if (SHOW) {impReslice.show();} 
        ImagePlus impKymo = WindowManager.getImage("MAX_Reslice of " + 
            titleROI);
        impKymo.hide(); 
        if (SHOW) {impKymo.show();} 
        addKymoGrayToStackKymoGray(iROI, impKymo); 
        int widthKymo = impKymo.getWidth(); 
        if (widthKymo > widthMax) widthMax = widthKymo; 
        ImagePlus impKymoBW = impKymo.duplicate(); 
        impKymoBW.setTitle(impKymo.getTitle() + "_BW"); 
        ImageConverter imc = new ImageConverter(impKymoBW);
        imc.convertToGray8();
        // beg of test
        IJ.run(impKymoBW, "Smooth", "");
        // while (IJ.macroRunning()) {;}
        IJ.setAutoThreshold(impKymoBW, "Huang dark");
        // end of test
        IJ.run(impKymoBW, "Convert to Mask", "");
        // while (IJ.macroRunning()) {;}
        addKymoBWToStackKymoBW(iROI, impKymoBW);
        time2 = System.currentTimeMillis();
        dt = (time2 - time1); 
        logger += "time taken to proceed: " + Long.toString(dt) + "\n"; 
        if (SHOW) {impKymoBW.show();}
        roiMan.runCommand("Show None");
    }
    
    static void analyzeOneKymo(ImagePlus impKymoBW){
        IJ.runPlugIn(impKymoBW, "Kymograph_Processing", ""); 
    }
    
    static void initImpStackMicrotubule(int nROI){
        int width, height; 
        String title = basename + "StackMicrotubule"; 
        width = DEFAULT_WIDTH; 
        height = BANDWITH * nROI; 
        impStackMicrotubule = NewImage.createByteImage(title, width, 
            height, nSlice, NewImage.FILL_BLACK); 
        if (SHOW) impStackMicrotubule.show(); 
    }

    static void initImpStackKymoGray(int nROI){
        int width, height; 
        String title = basename + "StackKymoGray"; 
        width = DEFAULT_WIDTH; 
        height = nSlice; 
        impStackKymoGray = NewImage.createByteImage(title, width, 
            height, nROI, NewImage.FILL_BLACK); 
    }

    static void checkAndOpenROIManager(String pathname){
        String filenameROI = pathname + "RoiSet.zip";
        logger += filenameROI + "\n"; 
        roiMan = RoiManager.getInstance(); 
        if (roiMan == null) {roiMan = new RoiManager(); }
        File f = new File(filenameROI); 
        if (f.exists()) {
            if (roiMan.getCount() > 0) {
                roiMan.runCommand("Select All"); 
                roiMan.runCommand("Delete"); 
            }
            roiMan.runCommand("Open", filenameROI);
            roiMan.runCommand("Select All"); 
        } 
        else {logger += pathname + "does not exist \n"; }
        roiMan.setVisible(true); 
        return; 
    }
    
    static void initImpStackKymoBW(int nROI){
        int width, height; 
        String title = basename + "StackKymoBW"; 
        width = DEFAULT_WIDTH; 
        height = nSlice; 
        impStackKymoBW = NewImage.createByteImage(title, width, 
            height, nROI, NewImage.FILL_BLACK); 
    }
    
    static void addROIToStackMicrotubule(int iROI, ImagePlus impMicrotubule){
        ImageStack imsSrc = impMicrotubule.getStack(); 
        ImageStack imsDest = impStackMicrotubule.getStack();
        int iSlice; 
        ImageProcessor ipSrc; 
        ImageProcessor ipDest; 
        int iShift = iROI * BANDWITH;
        for (iSlice = 1 ; iSlice < nSlice + 1; iSlice++) {
            ipSrc = imsSrc.getProcessor(iSlice); 
            ipDest = imsDest.getProcessor(iSlice);
            ipDest.insert(ipSrc, 0, iShift); 
            }
    }
    
    static void addKymoGrayToStackKymoGray(int iROI, ImagePlus impKymo){
        ImageProcessor ip = impKymo.getProcessor(); 
        String title; 
        title = impKymo.getTitle(); 
        ImageStack ims = impStackKymoGray.getStack();
        int iSlice = iROI + 1; 
        ImageProcessor ipDest = ims.getProcessor(iSlice);  
        ipDest.insert(ip, 0, 0); 
        ims.setSliceLabel(title, iSlice); 
    }

    static void addKymoBWToStackKymoBW(int iROI, ImagePlus impKymo){
        ImageProcessor ip = impKymo.getProcessor(); 
        String title; 
        title = impKymo.getTitle(); 
        ImageStack ims = impStackKymoBW.getStack();
        int iSlice = iROI + 1; 
        ImageProcessor ipDest = ims.getProcessor(iSlice);  
        ipDest.insert(ip, 0, 0); 
        ims.setSliceLabel(title, iSlice); 
    }
}
