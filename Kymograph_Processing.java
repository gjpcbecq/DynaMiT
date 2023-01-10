/**
Compute feature from one kymograph and add results into the result window.  

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

public class Kymograph_Processing implements PlugInFilter {
    ImagePlus imp; 
    int nStack; 
    double[] valueLeft; 
    double[] valueRight; 
    String log = ""; 
    boolean VERBOSE = false; 
    int I_BEG = 0, I_END = 1, I_SLOPE = 2, I_INTERCEPT = 3, 
        I_INTERCEPT2 = 4, N_RES = 5; 
    double Fs, resolution, thPause; 
    String sliceName; 
    TextWindow twSlope, twAnalyze;  
    float[] pointsNull = {0, 0}; 
    Roi polygonRoiNull = new PolygonRoi(pointsNull, pointsNull, 2, Roi.POLYGON); 
             
    public int setup(String arg, ImagePlus imp){
        this.imp = imp; 
        Fs = Double.valueOf(Macro.getValue(arg, "fs", "1.0"));
        resolution = Double.valueOf(Macro.getValue(arg, "resolution", "1.0")); 
        thPause = Double.valueOf(Macro.getValue(arg, "thpause", "0.01"));
        return DOES_ALL; 
    }
    
    public void run(ImageProcessor ip) {
        // use the BW or gray kymograph
        // propose the select pannel
        // add with t to the roi manager
        // launch the computing when the user click OK.
        TextWindow twSlope = new TextWindow("slope", "", 300, 800); 
        TextWindow twAnalyze = new TextWindow("analyze", "", 400, 200); 
        twSlope.setVisible(false); 
        twAnalyze.setVisible(false); 
        imp.deleteRoi(); 
        int iSlice = imp.getSlice(); 
        ImageStack ims = imp.getStack(); 
        sliceName = Integer.toString(iSlice) + 
            ", " + ims.getSliceLabel(iSlice);
        IJ.setTool("polyline");
        // ___________________________
        // Selection of the left part 
        NonBlockingGenericDialog nbgd = new NonBlockingGenericDialog(
            "Selection of the left polyline");
        nbgd.addMessage("Select the left polyline \n");
        DynaMiTDialogListener dlistener; 
        dlistener = new DynaMiTDialogListener(); 
        nbgd.addDialogListener(dlistener);
        nbgd.showDialog();
        // Stopped until OK in GenericDialog is pushed.
        Roi roiLeft; 
        if (nbgd.wasCanceled()) roiLeft = polygonRoiNull; 
        roiLeft = imp.getRoi(); 
        if (roiLeft == null) roiLeft = polygonRoiNull; 
        imp.deleteRoi(); 
        // ___________________________
        // ___________________________
        // Selection of the right part 
        NonBlockingGenericDialog nbgdPlus = new NonBlockingGenericDialog(
            "Selection of the right polyline");
        nbgdPlus.addMessage("Select the right polyline\n");
        nbgdPlus.addDialogListener(dlistener);
        nbgdPlus.showDialog();
        // Stopped until OK in GenericDialog is pushed.
        Roi roiRight; 
        if (nbgd.wasCanceled()) roiRight = polygonRoiNull; 
        roiRight = imp.getRoi(); 
        if (roiRight == null) roiRight = polygonRoiNull; 
        imp.deleteRoi(); 
        //_______________________________
        //_______________________________
        // Computation of the parameters
        double[][] resLeft, resRight, resNeg, resPos;
        double[] analyzeLeft, analyzeRight, analyzeNeg, analyzePos;
        resLeft = analyzeRoi(roiLeft, true); 
        resRight = analyzeRoi(roiRight, false); 
        analyzeLeft = analyzeRegress(resLeft, Fs, resolution, 
            thPause);
        analyzeRight = analyzeRegress(resRight, Fs, resolution, 
            thPause);
        boolean isLeftNeg; 
        int SPEEDPOLY = 0; 
        isLeftNeg = checkLeftNeg(analyzeLeft[SPEEDPOLY], 
            analyzeRight[SPEEDPOLY]); 
        if (isLeftNeg) {
            resNeg = resLeft; 
            resPos = resRight; 
            analyzeNeg = analyzeLeft; 
            analyzePos = analyzeRight; 
        }
        else {
            resNeg = resRight; 
            resPos = resLeft; 
            analyzeNeg = analyzeRight; 
            analyzePos = analyzeLeft; 
        }
        String strResNeg = regressToString(resNeg);
        String strResPos = regressToString(resPos);
        String strAnalyzeNeg = analyzeToString(analyzeNeg);
        String strAnalyzePos = analyzeToString(analyzePos);
        twSlope.append(sliceName); 
        twSlope.append("***** (+) end *****"); 
        twSlope.append(strResPos); 
        twSlope.append("***** (-) end *****"); 
        twSlope.append(strResNeg);
        twAnalyze.append(sliceName + ", " + strAnalyzePos + ", " 
            + strAnalyzeNeg); 
        twSlope.setVisible(true);
        twAnalyze.setVisible(true);
        if (VERBOSE) IJ.showMessage(log); 
        return; 
    }

    void addZeroValue(){
        twSlope.append(sliceName); 
        twSlope.append("***** (+) end *****");
        String strNull = "0, 0, 0, 0, 0"; 
        twSlope.append(strNull); 
        twSlope.append("***** (-) end *****"); 
        twSlope.append(strNull);
        strNull = "0, 0, 0, 0, 0, 0, 0, 0, 0, 0"; 
        twAnalyze.append(sliceName + ", " + strNull + ", " + strNull); 
        twSlope.setVisible(true);
        twAnalyze.setVisible(true);
        return; 
    }
    
    boolean checkLeftNeg(double speedLeft, double speedRight) {
        boolean isLeftNeg; 
        if (speedLeft < speedRight) isLeftNeg = true; 
        else isLeftNeg = false; 
        return isLeftNeg; 
    }
    
    double[][] analyzeRoi(Roi roi, boolean isLeft){
        int nSlope; 
        FloatPolygon fpoly = roi.getFloatPolygon();
        nSlope = fpoly.npoints - 1; 
        double[][] res = new double[nSlope][N_RES];
        // ibeg, iend, slope, intercept
        // $$ y_i  = \beta_{1, k} \, x_{i, k} + \beta_{0, k} $$
        // $$ x_{i, k} = i \, T_s - t_{d, k) $$
        // 
        // xpoints corresponds to beta_{0, k}
        // ypoints corresponds to tdeb_k
        // $$ beta_(1, k) = (beta_(0, k + 1) -  beta_{0, k)) / 
        // (tdeb_{k + 1} - tdeb_k) $$
        int i; 
        if (VERBOSE) {
            for (i=0; i < fpoly.npoints; i++){
                log += "x: " + fpoly.xpoints[i] + ", y: " + fpoly.ypoints[i] + 
                "\n"; 
            }
        }
        double delta_beta_0, beta_1, delta_t;  
        for (i = 0; i < nSlope; i++) {
            res[i][I_BEG] = fpoly.ypoints[i]; 
            res[i][I_END] = fpoly.ypoints[i + 1];
            delta_t = (res[i][I_END] - res[i][I_BEG]); 
            // If delta_t is zero or negative, it is set to a minimum of 1
            // This ensures a non negative or zero time.  
            if (delta_t <= 0) delta_t = 1;
            delta_beta_0 = (fpoly.xpoints[i + 1] - fpoly.xpoints[i]);
            if (isLeft) delta_beta_0 = - delta_beta_0;  
            beta_1 = delta_beta_0 / delta_t ; 
            res[i][I_SLOPE] = beta_1; 
            res[i][I_INTERCEPT] = fpoly.xpoints[i]; 
            res[i][I_INTERCEPT2] = fpoly.xpoints[i + 1]; 
            log += "delta: "+ IJ.d2s(delta_t) + 
                ", beta_0: " + IJ.d2s(delta_beta_0) + 
                ", beta_1: " + IJ.d2s(beta_1) + 
                ", tBeg: " + IJ.d2s(fpoly.xpoints[i]) + "\n";  
        }
        return res; 
    }
    
    String analyzeToString(double[] analyzeRes){
        String s = ""; 
        int nValue = analyzeRes.length;
        int nValueMinusOne = nValue - 1; 
        for (int i = 0; i < nValue; i++) {
            s += "" + IJ.d2s(analyzeRes[i]); 
            if (i == nValueMinusOne) {
            }
            else {
                s += ", ";
            }
        }
        return s; 
    }
        
    String regressToString(double[][] res) {
        /*
        return a string containing: 
        x_beg, x_end, y_beg, y_end, slope
        */
        String s = ""; 
        for (int i = 0; i < res.length; i++) {
            s += "" + IJ.d2s(res[i][I_INTERCEPT]) + ", " 
                + IJ.d2s(res[i][I_INTERCEPT2]) + ", " 
                + IJ.d2s(res[i][I_BEG]) + ", "
                + IJ.d2s(res[i][I_END]) + ", "
                + IJ.d2s(res[i][I_SLOPE]) + "\n"; 
        }
        return s; 
    }

    double[] analyzeRegress(double[][] res, double Fs, double resolution, 
        double thPause){
        /*
        return a double array containing statistics on this edge regression. 
        
        1 mean speed during polymerization
        2 mean speed during depolymerization
        3 mean speed during pause
        4 time during polymerization
        5 time during depolymerization
        6 time during pause
        7 time total 
        8 number of catastrophe
        9 number of rescue
        10 number of pause
        
        pause is obtained for: -thPause <= slope <= thPause
        
        a minimal delta_time is set to 1 pixel if delta_time == 0. 
        
        */
        int I_SPEEDPOLY = 0, I_SPEEDDEPOLY = 1, I_SPEEDPAUSE = 2, 
            I_TPOLY = 3, I_TDEPOLY = 4, I_TPAUSE = 5, I_TTOTAL = 6, 
            I_NCATA = 7, I_NRESCUE = 8, I_NPAUSE = 9, N_STAT = 10; 
        double[] stat = new double[N_STAT]; 
        int nTrend = res.length;
        double sumPoly = 0, sumDepoly = 0, sumPause = 0;
        int nSamplePoly = 0, nSampleDepoly = 0, nSamplePause = 0, nSample;
        double tPoly = 0, tDepoly = 0, tPause = 0;
        double slope;
        // threshold for pause event. 
        int iTrend; 
        int PAUSE = 0, POLY = 1, DEPOLY = 2, ATF = 3;  
        int[] state = new int[nTrend];
        // thPause is divided by Fs to be compatible with slope in the 
        // computation given in micrometers per pxl. 
        thPause = thPause / Fs; 
        for (iTrend = 0; iTrend < nTrend; iTrend++) {
            nSample = (int) res[iTrend][I_END] - (int) res[iTrend][I_BEG];
            // The minimal nSample is set to 1 in order to obtain the maximal 
            // slope between 2 pixels. 
            if (nSample <= 0) nSample = 1; 
            // Correction of the speed by taking into account the spatial 
            // resolution. 
            slope = res[iTrend][I_SLOPE] / resolution; 
            // In the next computation we weigth the speed by the time. 
            // is equivalent to compute the ratio of the 
            // total distance with the total time.
            // In this condition we have : 
            // $$ speed = \frac{\sum_i d_i}{\sum_i t_i} $$
            // another option is to take: 
            // $$ speed = \frac{1}{N} \sum_i \frac{d_i}{t_i} $$
            int currentState = 0; 
            if (slope < -thPause) currentState = DEPOLY;
            if (slope >= thPause) currentState = POLY; 
            if ((slope > -thPause) & (slope < thPause)) currentState = PAUSE; 
            switch (currentState) {
                // 2: DEPOLY
                case 2 :
                    sumDepoly += nSample * slope; 
                    nSampleDepoly += nSample; 
                    state[iTrend] = DEPOLY;
                    break;
                // 1: POLY
                case 1 : 
                    sumPoly += nSample * slope; 
                    nSamplePoly += nSample; 
                    state[iTrend] = POLY;
                    break;
                // 0: PAUSE OR ATF
                case 0 : 
                    if  (nSample > 1) {
                        sumPause += nSample * slope; 
                        nSamplePause += nSample; 
                        state[iTrend] = PAUSE; 
                    }
                    else state[iTrend] = ATF;
                    break; 
            }
        }
        log += "(" + sumDepoly + ", " + sumPoly + ", " + sumPause + ")\n"; 
        log += "(" + nSampleDepoly + ", " + nSamplePoly + ", " + 
            nSamplePause + ")\n"; 
        if (nSampleDepoly == 0) sumDepoly = 0;  
        else sumDepoly /= (nSampleDepoly / Fs);
        if (nSamplePoly == 0) sumPoly = 0; 
        else sumPoly /= (nSamplePoly / Fs);
        if (nSamplePause == 0) sumPause = 0; 
        else sumPause /= (nSamplePause / Fs);
        log += "(" + sumDepoly + ", " + sumPoly + ", " + sumPause + ")\n"; 
        int nPause = 0, nCata = 0, nRescue = 0;
        boolean condPause, condCata, condRescue;
        int sI, sIP1; 
        for (iTrend = 0; iTrend < nTrend - 1; iTrend++){
            sI = state[iTrend]; 
            sIP1 = state[iTrend + 1]; 
            condPause = (sI == PAUSE);  
            condCata = ((sI == PAUSE) & (sIP1 == DEPOLY)) | 
                ((sI == POLY) & (sIP1 == DEPOLY)); 
            condRescue = ((sI == PAUSE) & (sIP1 == POLY)) | 
                ((sI == DEPOLY) & (sIP1 == POLY)); 
            if (condPause) nPause += 1; 
            if (condCata) nCata += 1; 
            if (condRescue) nRescue += 1; 
        }
        // check if the last event is a pause 
        if (state[iTrend] == PAUSE) nPause += 1; 
        tDepoly = nSampleDepoly / Fs; 
        tPoly = nSamplePoly / Fs; 
        tPause = nSamplePause / Fs; 
        double tTotal = tDepoly + tPoly + tPause; 
        stat[I_SPEEDDEPOLY] = sumDepoly; 
        stat[I_SPEEDPOLY] = sumPoly; 
        stat[I_SPEEDPAUSE] = sumPause; 
        stat[I_TDEPOLY] = tDepoly;  
        stat[I_TPOLY] = tPoly;  
        stat[I_TPAUSE] = tPause;
        stat[I_TTOTAL] = tTotal;
        stat[I_NPAUSE] = nPause; 
        stat[I_NCATA] = nCata; 
        stat[I_NRESCUE] = nRescue; 
        return stat; 
    }


}

