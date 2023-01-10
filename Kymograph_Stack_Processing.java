/**
Compute feature from a kymograph stack.  

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/

import ij.plugin.filter.*; 
import ij.*; 
import ij.process.*; 
import ij.text.*; 
import ij.plugin.*; 
import ij.gui.*; 
import ij.io.*; 

public class Kymograph_Stack_Processing implements PlugIn {
    ImagePlus imp; 
    String logger = ""; 
    TextWindow twRes, twStat, twResOne, twStatOne;  
    double Fs, resolution, thPause; 
    String pathname; 
    DynaMiTProperty dynaMiTProp = new DynaMiTProperty();
    
    public void run(String arg) {
        /** 
        Use the BW or gray kymograph
        Propose the select pannel
        Add with "t" to the roi manager
        Launch the computing when the user click OK. 
        */ 

        // ___________________________
        // For all stack, select left and right.
        ImageWindow imw = WindowManager.getCurrentWindow();
        if (imw == null) {
            IJ.showMessage("There are no images open."); 
            return;
        } 
        imp = imw.getImagePlus(); 
        FileInfo finfo = imp.getOriginalFileInfo(); 
        pathname = finfo.directory; 
        logger = DynaMiTUtil.loggerStart(logger, 
            "Kymograph Stack Processing"); 
        String filename = imp.getTitle() ; 
        twRes = new TextWindow("Res All", "", 400, 200); 
        twRes.setVisible(false);
        String titleRes = "X_BEG, X_END, Y_BEG, Y_END, SLOPE"; 
        twRes.append(titleRes); 
        twStat = new TextWindow("Stat All", "", 400, 200); 
        twStat.setVisible(false); 
        String titleStat = "I_MIT, NAME, " + 
            "SPEED_POLY_(+), SPEED_DEPOLY_(+), SPEED_PAUSE_(+), " + 
            "TIME_POLY_(+), TIME_DEPOLY_(+), TIME_PAUSE_(+), " + 
            "TIME_TOTAL_(+), N_CATA_(+), N_RESCUE_(+), N_PAUSE_(+), " + 
            "SPEED_POLY_(-), SPEED_DEPOLY_(-), SPEED_PAUSE_(-), " + 
            "TIME_POLY_(-), TIME_DEPOLY_(-), TIME_PAUSE_(-), " + 
            "TIME_TOTAL_(-), N_CATA_(-), N_RESCUE_(-), N_PAUSE_(-)";  
        twStat.append(titleStat); 
        TextWindow twStatOne; 
        int nSlice = imp.getNSlices();
        int iSlice; 
        initParams();     
        String argKP = ""; 
        argKP += "fs=" + Fs + " "; 
        argKP += "resolution=" + resolution + " "; 
        argKP += "thpause=" + thPause + " "; 
        logger += argKP + "\n"; 
        for (iSlice = 1; iSlice < nSlice + 1; iSlice++){
            imp.setSlice(iSlice);
            imp.show(); 
            IJ.runPlugIn(imp, "Kymograph_Processing", argKP);
            twResOne = (TextWindow) WindowManager.getWindow("slope");
            twRes.append(twResOne.getTextPanel().getText()); 
            twResOne.close(); 
            twStatOne = (TextWindow) WindowManager.getWindow("analyze"); 
            twStat.append(twStatOne.getTextPanel().getText()); 
            twStatOne.close(); 
        }
        twStat.getTextPanel().saveAs(pathname + "stat.csv"); 
        twRes.getTextPanel().saveAs(pathname + "slope.csv");
        DynaMiTUtil.loggerEnd(logger, pathname); 
        return; 
    }
    
    void initParams(){
        String timeDelay, pixelSize; 
        timeDelay = dynaMiTProp.USER_PROP.getProperty("STACK_DELAY");
        pixelSize = dynaMiTProp.USER_PROP.getProperty("PIXEL_SIZE"); 
        Fs = 1 / Double.valueOf(timeDelay);
        resolution = 1 / Double.valueOf(pixelSize);
        thPause = Double.valueOf(
            dynaMiTProp.USER_PROP.getProperty("THRESHOLD_PAUSE"));
        logger += "time between image: " + timeDelay + " s \n";
        logger += "pixel size: " + pixelSize + " um \n"; 
        return; 
    }
}
