/**
Select windows for selecting images to process. 

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/

import ij.*;
import ij.plugin.*; 
import ij.gui.*; 
import java.awt.*; 

public class Microtubule_Image_Processing implements PlugIn {
    String title = "Image Processor"; 
    String DATA_PATH; 
    
    public void run(String arg) {
        String strListFile = ""; 
        GenericDialog gd = new GenericDialog(title);
        GridBagLayout gbl = (GridBagLayout) gd.getLayout(); 
        GridBagConstraints gbc;
        Panel p = new Panel(); 
        Button b = new Button("Select"); 
        b.setActionCommand("addFileToProcess");
        DynaMiTActionListener dal = new DynaMiTActionListener(); 
        b.addActionListener(dal); 
        //gbc = gbl.getConstraints(gd); 
        //gbc.gridx = 0; 
        //gbc.gridy = 0; 
        //gbl.setConstraints(b, gbc); 
        p.add(b); 
        List l = new List(10);
        // gbc.gridx = 1; 
        // gbc.gridy = 0; 
        // gbl.setConstraints(l, gbc); 
        p.add(l); 
        gd.addPanel(p); 
        gd.showDialog();
        if (gd.wasCanceled()) return;
        // Generation of the list of file to process
        String[] filenameToProcess = l.getItems();
        if (filenameToProcess.length == 0) return; 
        DynaMiTImageProcessor.run(filenameToProcess); 
        return ; 
    }
}
