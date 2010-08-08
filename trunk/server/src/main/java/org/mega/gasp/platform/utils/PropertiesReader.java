/***
 * GASP: a gaming services platform for mobile multiplayer games.
 * Copyright (C) 2004 CNAM/INT
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact: gasp-team@objectweb.org
 *
 * Author: Romain Pellerin
 */
package org.mega.gasp.platform.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;

/**
 * Class to read the properties files of GASP configuration.
 * 
 * @author PELLERIN Romain (pellerin@cnam.fr) - MEGA Project 
 */
public class PropertiesReader {
    private URL url;
    private InputStream is;
    private Properties prop;
    private Category cat;
    private String conf_home;
    private final int minimalApplicationTimeout = 60000;
    private final int minimalPlatformTimeout = 300000;
    
    public PropertiesReader(String path){
        conf_home = path + "conf/";
        cat = Category.getInstance("PropertiesReader");
        prop = new Properties();
    }
    
    /**
     * Returns the package name of the application to serve.
     * 
     * @param appID
     * @return String the application package
     */
    public String getApplicationPackage(int appID){
        try {
            File f = new File(conf_home+"apps.properties");
            is = new FileInputStream(f);
            prop.load(is);
            is.close();
            return prop.getProperty("app"+appID+".package"); 
        } catch (FileNotFoundException e) {
            cat.fatal("apps.properties not found");
        } catch (IOException e) {
            cat.fatal("IOException on file: apps.properties");
        }
        return null;
    }
    
    
    /**
     * Returns the application timeout.
     * 
     * @param appID
     * @return int the application timeout
     */
    public int getApplicationTimeout(int appID){
        try {
            File f = new File(conf_home+"apps.properties");
            is = new FileInputStream(f);
            prop.load(is);
            is.close();
            
            int timeoutSpecified;
            try{ timeoutSpecified = Integer.parseInt(prop.getProperty("app"+appID+".timeout"));
            } catch (NumberFormatException e){ 
                cat.info("application n°"+appID+", illegible timeout move timeout to 60000ms");
                return minimalApplicationTimeout;
            }
            
            if (timeoutSpecified < minimalApplicationTimeout){
                cat.info("application n°"+appID+", too small timeout specified move to 60000ms");
                return minimalApplicationTimeout; 
            }
            else if (timeoutSpecified > minimalPlatformTimeout){
                cat.info("platform timeout, too small timeout specified move to 60000ms");
                return minimalPlatformTimeout; 
            }
            
            
            return timeoutSpecified;
        } catch (FileNotFoundException e) {
            cat.fatal("apps.properties not found");
        } catch (IOException e) {
            cat.fatal("IOException on file: apps.properties");
        }
        return minimalApplicationTimeout;
    }
 
    
    /**
     * Returns the database url from platform.properties.
     * 
     * @return String the db url
     */
    public String getDBUrl(){
       try {
            File f = new File(conf_home+"platform.properties");
            is = new FileInputStream(f);
            prop.load(is);
            is.close();
            return prop.getProperty("db.url");
        } catch (FileNotFoundException e) {
            cat.fatal("platform.properties not found");
        } catch (IOException e) {
            cat.fatal("IOException on file: platform.properties");
        } catch (Exception e){
            e.printStackTrace();
        }    
        return null;
    }
    
    
    /**
     * Returns the platform timeout for sessions from platform.properties.
     * 
     * @return String the db url
     */
    public int getPlatformTimeout(){
       try {
            File f = new File(conf_home+"platform.properties");
            is = new FileInputStream(f);
            prop.load(is);
            is.close();
            
            int timeoutSpecified;
            try{ timeoutSpecified = Integer.parseInt(prop.getProperty("platform.timeout"));
            } catch (NumberFormatException e){ 
                cat.info("platform timeout, illegible timeout move timeout to 300000ms (5min)");
                return minimalPlatformTimeout;
            }
            
            if (timeoutSpecified < minimalPlatformTimeout){
                cat.info("platform timeout, too small timeout specified move to 300000ms (5min)");
                return minimalPlatformTimeout; 
            }
            
            
            return timeoutSpecified;
        } catch (FileNotFoundException e) {
            cat.fatal("platform.properties not found");
        } catch (IOException e) {
            cat.fatal("IOException on file: platform.properties");
        } catch (Exception e){
            e.printStackTrace();
        }    
        return minimalPlatformTimeout;
    }

    /**
     * Checks the path of logging from log4j.properties.
     */
    public void checkLog4jLoggingPath(){
        try {
            File f = new File(conf_home+"log4j.properties");
            is = new FileInputStream(f);
            prop.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            cat.fatal("log4j.properties not found");
        } catch (IOException e) {
            cat.fatal("IOException on file: log4j.properties");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the type of the application model.
     * 0: automatic management (start, stop the game watching the number of player)
     * 1: manual management (the proprietary of the application instance start, stop the aI)
     * 2: mapcycle
     * 
     * @param appID
     * @return the type of the application model
     */
    public byte getApplicationModel(int appID) {
        try {
            File f = new File(conf_home+"apps.properties");
            is = new FileInputStream(f);
            prop.load(is);
            is.close();
            String model = prop.getProperty("app"+appID+".model");
            cat.debug("app"+appID+".model> "+model);
            if (model.equals("auto")) return 0;
            if (model.equals("manual")) return 1;
            if (model.equals("mapcycle")) return 2;
            else return 0; // model auto by default 
        } catch (FileNotFoundException e) {
            cat.fatal("apps.properties not found");
        } catch (IOException e) {
            cat.fatal("IOException on file: apps.properties");
        }
        return -1;
    }
    
}
