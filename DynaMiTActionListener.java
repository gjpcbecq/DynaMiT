/**
Definition of Action Listeners

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/

import java.awt.event.*; 
import java.awt.*; 
import ij.io.*; 
import ij.*; 

public class DynaMiTActionListener implements ActionListener {
    String DATA_PATH; 
    
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand(); 
        if (ac == "getDirectoryData") {
            getDirectoryData(e); 
        }
        if (ac == "getDirectoryResult") {
            getDirectoryResult(e); 
        }
        if (ac == "addFileToProcess") {
            addFileToProcess(e); 
        }
        return; 
    }
    
    void getDirectoryData(ActionEvent e) {
        DirectoryChooser dc = new DirectoryChooser("Select the data directory");
        String pathname = dc.getDirectory();
        Button b = (Button) e.getSource(); 
        Container c = b.getParent(); 
        TextField tf = (TextField) c.getComponent(1); 
        tf.setText(pathname); 
        tf.setVisible(true);
        return; 
    }
    
    void getDirectoryResult(ActionEvent e) {
        DirectoryChooser dc = new DirectoryChooser(
            "Select the result directory");
        String pathname = dc.getDirectory();
        Button b = (Button) e.getSource(); 
        Container c = b.getParent(); 
        TextField tf = (TextField) c.getComponent(4); 
        tf.setText(pathname); 
        tf.setVisible(true);
        return; 
    }


    void addFileToProcess(ActionEvent e) {
        initParams(); 
        //IJ.showMessage("addFileToProcess: " + DATA_PATH); 
        OpenDialog od = new OpenDialog("Select images to process", DATA_PATH, 
            ""); 
        //, DATA_PATH);
        String pathname = od.getPath();
        if (pathname == null) return;
        Button b = (Button) e.getSource(); 
        Container c = b.getParent(); 
        List l = (List) c.getComponent(1);
        l.add(pathname); 
        //l.setVisible(true); 
        return; 
    }

    void initParams() {
        DynaMiTProperty dynaMiTProp = new DynaMiTProperty();
        DATA_PATH = dynaMiTProp.USER_PROP.getProperty("DATA_PATH");
        return; 
    }

}
