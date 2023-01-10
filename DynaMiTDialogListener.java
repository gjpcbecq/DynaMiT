/**
Implements a DialogListener doing nothing only sending true

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/

import ij.gui.DialogListener; 
import ij.gui.GenericDialog; 
import java.awt.AWTEvent; 

public class DynaMiTDialogListener 
    implements DialogListener {
    public boolean dialogItemChanged(GenericDialog gd, java.awt.AWTEvent e) {
        return true; 
    }
}
