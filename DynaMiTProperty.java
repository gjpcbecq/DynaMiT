/**
Contains tools for interacting with properties files and user interfaces.  

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS.  
All rights reserved.

@author G. Becq
@since 25 september 2013
*/
import ij.*; 
import java.util.*; // Properties
import java.io.*; // File
// import java.time.*; 

public class DynaMiTProperty {

    // Field
    
    // the logger string collects comments created while using methods. 
    String logger = ""; 
    public String SEP = Prefs.getFileSeparator(); 
    public String PLUGIN_PATH = IJ.getDirectory("plugins");
    // PLUGIN_PATH has a SEP at the end
    // HOME_PATH also    
    public String DYNAMIT_PATH = PLUGIN_PATH + "DynaMiT" + SEP;    
    public String HOME_PATH = IJ.getDirectory("home"); 
    // TODO check if IJ.getDirectory is the same as System.getProperty("user.dir")
    public String USER_PROP_BASENAME = "user.xml";
    public String USER_PROP_FILENAME = DYNAMIT_PATH + USER_PROP_BASENAME;
    public String DEFAULT_PROP_BASENAME = "default.xml";
    public String DEFAULT_PROP_FILENAME = DYNAMIT_PATH + DEFAULT_PROP_BASENAME;
    public static String LOGGER_BASENAME = "log.txt"; 
    public Properties DEFAULT_PROP;
    public Properties USER_PROP;
    // Definition of initial (keys, values) 
    // Definition of the keys names  
    public String PROP_RESULT_PATH = "RESULT_PATH"; 
    public String PROP_DATA_PATH = "DATA_PATH"; 
    public String PROP_REGRESS_THRESHOLD = "REGRESS_THRESHOLD"; // in $\micro m 
        // / s$    
    public String PROP_BANDWITH = "BANDWITH"; // in pixels
    public String PROP_PIXEL_SIZE = "PIXEL_SIZE"; // in $\micro m$ (1e-6 m)
    public String PROP_STACK_DELAY = "STACK_DELAY"; // in s 
    public String PROP_THRESHOLD_PAUSE = "THRESHOLD_PAUSE"; // in $\micro m / s$ 
    public String PROP_IMAGE_PROCESSING_METHOD = "IMAGE_PROCESSING_METHOD"; 
    // Definition of the default values
    public String DEFAULT_PROP_RESULT_PATH = HOME_PATH + "DynaMiT_result" 
        + SEP; 
    public String DEFAULT_PROP_DATA_PATH = HOME_PATH;     
    public String DEFAULT_PROP_REGRESS_THRESHOLD = "1";     
    public String DEFAULT_PROP_BANDWITH = "5"; 
    public String DEFAULT_PROP_PIXEL_SIZE = "0.267"; 
    public String DEFAULT_PROP_STACK_DELAY = "5.0"; 
    public String DEFAULT_PROP_IMAGE_PROCESSING_METHOD = "Median - Gaussian"; 
    public String DEFAULT_PROP_THRESHOLD_PAUSE = "0.001"; // in $\micro m / s$ 

    // Method

    public DynaMiTProperty() {
        loadDefaultProperties();
        loadUserProperties();
    } 
    
    public void createUserPropertiesFile() {
        // create User properties file
        File userPropFile = new File(USER_PROP_FILENAME);
        try {userPropFile.createNewFile();}
        catch (Exception e) {}
        // load properties from DEFAULT_PROP_FILENAME
        logger += "Load default properties:" + DEFAULT_PROP_FILENAME + "\n"; 
        loadDefaultProperties(); 
        // copy properties from Default to User
        logger += "Copying default to user" + "\n"; 
        logger += "DEFAULT_PROP: " + DEFAULT_PROP.toString() + "\n"; 
        USER_PROP = DEFAULT_PROP;
        logger += "USER_PROP in createUserProp: " + USER_PROP.toString() + "\n"; 
        // save properties to USER_PROP_FILENAME
        logger += "saving USER_PROP_FILENAME:" + USER_PROP_FILENAME + "\n"; 
        saveUserProperties(); 
        return ; 
    }
    
    public void loadDefaultProperties() {
        try {
            logger += "loading: " + DEFAULT_PROP_FILENAME + "\n"; 
            File defaultPropFile = new File(DEFAULT_PROP_FILENAME); 
            FileInputStream defaultIn = new FileInputStream(defaultPropFile);
            DEFAULT_PROP = new Properties(); 
            DEFAULT_PROP.loadFromXML(defaultIn);
            defaultIn.close(); 
            logger += "closing: " + DEFAULT_PROP_FILENAME + "\n";
        }
        catch  (IOException e) {
            createDefaultPropertiesFile(); 
            loadDefaultProperties(); 
        }
        return ; 
    }

    public void loadUserProperties() {
        // check if the file named USER_PROP_FILENAME exists
        // if not, the file is created from DEFAULT_PROP_FILENAME
        File userPropFile = new File(USER_PROP_FILENAME); 
        logger += userPropFile.getAbsolutePath() + "\n"; 
        if (!userPropFile.exists()) {
            logger += "USER_PROP_FILENAME does not exist \n"; 
            createUserPropertiesFile(); 
        }
        try {
            logger += "USER_PROP_FILENAME: " + USER_PROP_FILENAME + 
                " exists !\n"; 
            FileInputStream userIn = new FileInputStream(userPropFile);
            USER_PROP = new Properties(); 
            USER_PROP.loadFromXML(userIn);
            userIn.close();
        }
        catch (IOException e){
            logger += "Problem with USER_PROP_FILENAME: " + USER_PROP_FILENAME + 
                "\n"; 
        }
        return ; 
    }

    public void saveUserProperties() {
        String comment = "User Properties"; 
        try {
            FileOutputStream userOut = new FileOutputStream(USER_PROP_FILENAME);
            logger += "USER_PROP in saveUserProperties: " + 
                USER_PROP.toString() + "\n"; 
            USER_PROP.storeToXML(userOut, comment);
            userOut.close(); 
        }
        catch (IOException e){
            logger += "Problem for writing in " + USER_PROP_FILENAME + "\n"; 
        }
        return ; 
    }
        
    public String getResultPath() {
        return USER_PROP.getProperty(PROP_RESULT_PATH); 
    }
    
    public void setResultPath(String resultPath) {
        USER_PROP.setProperty(PROP_RESULT_PATH, resultPath);
        saveUserProperties(); 
        return; 
    }

    public void setDataPath(String dataPath) {
        USER_PROP.setProperty(PROP_DATA_PATH, dataPath);
        saveUserProperties(); 
        return; 
    }
    
    public void createDefaultPropertiesFile() {
        DEFAULT_PROP = new Properties(); 
        DEFAULT_PROP.setProperty(PROP_RESULT_PATH, DEFAULT_PROP_RESULT_PATH);
        DEFAULT_PROP.setProperty(PROP_DATA_PATH, DEFAULT_PROP_DATA_PATH);
        DEFAULT_PROP.setProperty(PROP_REGRESS_THRESHOLD, 
            DEFAULT_PROP_REGRESS_THRESHOLD);
        DEFAULT_PROP.setProperty(PROP_REGRESS_THRESHOLD, 
            DEFAULT_PROP_REGRESS_THRESHOLD);
        DEFAULT_PROP.setProperty(PROP_BANDWITH, DEFAULT_PROP_BANDWITH);
        DEFAULT_PROP.setProperty(PROP_PIXEL_SIZE, DEFAULT_PROP_PIXEL_SIZE);
        DEFAULT_PROP.setProperty(PROP_STACK_DELAY, DEFAULT_PROP_STACK_DELAY);
        DEFAULT_PROP.setProperty(PROP_THRESHOLD_PAUSE, 
            DEFAULT_PROP_THRESHOLD_PAUSE);
        DEFAULT_PROP.setProperty(PROP_IMAGE_PROCESSING_METHOD, 
            DEFAULT_PROP_IMAGE_PROCESSING_METHOD);
        File defaultPropFile = new File(DEFAULT_PROP_FILENAME); 
        try {
            defaultPropFile.createNewFile();
        }
        catch (Exception e) {};
        String comment = "Default File"; 
        try {
            FileOutputStream defaultOut = 
                new FileOutputStream(DEFAULT_PROP_FILENAME);
            DEFAULT_PROP.storeToXML(defaultOut, comment);
            defaultOut.close(); 
        }
        catch (IOException e){
            logger += "Problem for writing " + DEFAULT_PROP_FILENAME + "\n"; 
        }
        return; 
    }
    
    public String getLogger() {
        return logger; 
    }
    
}

