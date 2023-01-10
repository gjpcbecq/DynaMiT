/**
Selection of microtubules on the intensity image. 

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/
import ij.*;
import ij.plugin.*; 
import ij.io.*; 

public class Microtubule_Selection implements PlugIn {
    String title = "Microtubule Selection"; 
    String RESULT_PATH; 
    
    public void run(String arg) {
        initParams(); 
        String filename; 
        OpenDialog od = new OpenDialog(
            "Select a file containing a Z projection", 
            RESULT_PATH, "*ZProjection.tif"); 
        filename = od.getPath(); 
        if (filename == null) return;
        DynaMiTMicrotubuleSelection.run(filename); 
    }

    void initParams() {
        DynaMiTProperty dynaMiTProp = new DynaMiTProperty();
        RESULT_PATH = dynaMiTProp.USER_PROP.getProperty("RESULT_PATH");
        return; 
    }    
}
