/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 18.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public abstract class Generator implements DefaultValues {

    /**
     * 
     * @return
     * 
     */
    public abstract String getData();

    // has the user defined map
    private static Map<String, String> userSettings = new HashMap<String, String>();

    /**
     * 
     * @param fileName
     * @param data
     * @param addNewLine
     */
    public void write(final String fileName, final String data,
            boolean addNewLine) {

        BufferedWriter bufferedWriter = null;
        try {
            final File file = createFile(fileName);
            final FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
            if (addNewLine)
                bufferedWriter.newLine();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param name
     * @return
     */
    private File createFile(final String name) {
        File file = null;
        try {
            file = new File(name);
            if (!file.exists()) {
                final boolean result = file.createNewFile();
                if (!result) {
                    JOptionPane.showMessageDialog(null,
                            "Unable to create the file name\t" + name, "Error",
                            JOptionPane.OK_OPTION);
                }
            }
            if (!file.canWrite()) {
                JOptionPane.showMessageDialog(null,
                        "Write permission missing on file\t" + name, "Error",
                        JOptionPane.OK_OPTION);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Map<String, String> getMap() {
        return userSettings;
    }

    /**
     * 
     * @param key
     * @return
     */
    public static String getUserSetting(String key) {
        if (SetupHelper.has(key)) {
            if (userSettings.containsKey(key)) {
                return userSettings.get(key);
            } else {
                return getDefaultValue(key);
            }
        }
        throw new IllegalArgumentException(
                "the key provided for looking up the properties is not vaild "
                        + key);
    }

    /**
     * @param key
     * @return
     */
    private static String getDefaultValue(final String key) {
        switch (SetupHelper.getInt(key)) {

        case INT_PATH:
            return PATH_VALUE;

        case INT_USER_PREFIX:
            return DEFAULT_USER_PREFIX;

        case INT_DEFAULT_PASSWORD:
            return DEFAULT_PASSWORD_VALUE;

        case INT_CLIENT_NAME:
            return DEFAULT_CLIENT_NAME;

        case INT_DATE_FORMAT:
            return DEFAULT_DATE_FORMAT;

        case INT_NUMBER_USER:
            return "" + DEFAULT_NUMBER_USER;

        case INT_EMAIL:
            return DEFAULT_EMAIL;

        case INT_PRICEMODEL:
            return DEFAULT_PRICEMODEL;

        case INT_ORGANIZATION:
            return DEFAULT_ORGANIZATION;

        case INT_VERSION:
            return DEFAULT_VERSION;

        case INT_TECHNICALPRODUCT_TKEY:
            return DEFAULT_TECHNICALPRODUCT_TKEY;

        case INT_USEDPAYMENT_TKEY:
            return DEFAULT_USEDPAYMENT_TKEY;

        case INT_PRODUCT:
            return DEFAULT_PRODUCT;

        case INT_DESCRIPTION:
            return DEFAULT_DESCRIPTION;

        case INT_DTYPE:
            return DEFAULT_DTYPE;

        case INT_PERIOD:
            return DEFAULT_PERIOD;

        case INT_PERIODHANDLING:
            return DEFAULT_PERIODHANDLING;

        case INT_PRICEPERPERIOD:
            return DEFAULT_PRICEPERPERIOD;

        case INT_UNUSED:
            return DEFAULT_UNUSED;

        case INT_TECHNICALPRODUCTIDENTIFIER:
            return DEFAULT_TECHNICALPRODUCTIDENTIFIER;

        case INT_PRODUCTINSTANCEID:
            return DEFAULT_PRODUCTINSTANCEID;

        case INT_SUBCRIPTIONID:
            return DEFAULT_SUBCRIPTIONID;

        case INT_ADMIN:
            return DEFAULT_ADMIN;

        case INT_CURRENTSTATUS:
            return DEFAULT_CURRENTSTATUS;

        case INT_SUBSCRIPTION_TKEY:
            return DEFAULT_SUBSCRIPTION_TKEY;

        case INT_USER_TKEY:
            return DEFAULT_USER_TKEY;

        case INT_EVENTPRICE:
            return DEFAULT_EVENTPRICE;

        case INT_BASEURL:
            return DEFAULT_BASEURL;

        case INT_EVENTIDENTIFIER:
            return DEFAULT_EVENTIDENTIFIER;

        case INT_ADDRESS:
            return DEFAULT_ADDRESS;

        case INT_COLLECTIONTYPE:
            return DEFAULT_COLLECTIONTYPE;

        default:
            return null;
        }
    }

}
