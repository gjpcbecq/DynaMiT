/**
Utility functions shared between classes 

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/
import ij.*; 
import java.io.File; 
import java.util.*; 

public class DynaMiTUtil {
    
    public static String logger;  
    public static String sep = Prefs.separator;
    public static String BREAKLOG = 
        "__________________________________________________\n"; 
    static char mu = '\u03BC'; 
     
    public static String getBasename(String filename) {
        /*
        Return the basename of a file.
        
        The basename of a file is the name without the extension and the path. 
        For example, applying getBasename onto "C:\\System\\example.txt" will 
        return "example". 
        */
        int iSep, iDot; 
        String basenameExt, basename; 
        try {
            iSep = filename.lastIndexOf(sep);
            basenameExt = filename.substring(iSep + 1); 
            iDot = basenameExt.lastIndexOf('.');
            basename = basenameExt.substring(0, iDot);
        }
        catch (Exception e) {
            logger += "Problem with filename: " + filename + "\n"; 
            basename = ""; 
        }
        return basename; 
    }

    public static String getPathname(String filename) {
        /*
        Return the pathname of a file.
        
        The pathname of a file is the string before the basename. 
        For example, applying getPathname onto "C:\\System\\example.txt" will 
        return "C:\\System\\". 
        */
        int iSep; 
        String pathname; 
        try {
            iSep = filename.lastIndexOf(sep);
            pathname = filename.substring(0, iSep + 1); 
        }
        catch (Exception e) {
            logger += "Problem with filename: " + filename + "\n"; 
            pathname = ""; 
        }
        return pathname; 
    }

    public static String getBasenameWithoutZProj(String basenameZProj) {
        /*
        Remove _ZProj at the end of the basenameZProj
        */
        int iUnderscore; 
        String basename; 
        try {
            iUnderscore = basenameZProj.lastIndexOf("_");
            basename = basenameZProj.substring(0, iUnderscore); 
        }
        catch (Exception e) {
            logger += "Problem with filename: " + basenameZProj + "\n"; 
            basename = ""; 
        }
        return basename; 
    }
    
    public static void checkCreateDir(String pathname) {
        /*
        Check if a directory exists in case it does not it creates it. 
        */
        File f = new File(pathname);
        if (!f.exists()) {
            f.mkdirs(); 
        }
        return; 
    }
    
    public static String loggerStart(String logger, String title) {
        logger += BREAKLOG;
        logger += title + "\n"; 
        Date dNow = new Date();
        logger += "" + dNow.toString() + "\n"; 
        return logger; 
    }
    
    public static void loggerEnd(String logger, String pathname){
        String filenameParam = pathname + DynaMiTProperty.LOGGER_BASENAME; 
        logger += BREAKLOG;
        IJ.append(logger, filenameParam);
        return ; 
    }

}
