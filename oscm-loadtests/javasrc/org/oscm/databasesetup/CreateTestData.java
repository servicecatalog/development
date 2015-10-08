/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 18.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * @author pravi
 * 
 */
public class CreateTestData implements DefaultValues {

    public static void main(String[] args) {
        final CreateTestData ctd = new CreateTestData();
        ctd.updateFromPropertiesFile();
        performTask(args, ctd);
    }

    private static void performTask(String[] args, final CreateTestData ctd) {
        int task = getTask(args);
        switch (task) {
        case 1:// csv file
            ctd.createCSVFile();
            break;

        case 2: // ldif file
            ctd.createLdifFile();
            break;

        case 3: // clean the xml file
            ctd.createXMLFile();
            break;

        case 4: // clean ldap
            break;

        case 5:// clean to database
            break;

        default: // perform all
            ctd.createCSVFile();
            ctd.createLdifFile();
            ctd.createXMLFile();
        }
    }

    private static int getTask(String[] args) {
        int task = -1;

        if (args.length > 1) {
            try {
                task = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "Please provide the argument in numeric [from 1 to 5 for specific task ]form :\t",
                                "Error", JOptionPane.OK_OPTION);
            }
        }
        return task;
    }

    /**
     */
    private void createLdifFile() {
        final String ldifFileName = Generator
                .getUserSetting(Generator.KEY_PATH) + fileName + ".ldif";
        Generator ldifGenrator = new LDIFGenerator();
        final String values = ldifGenrator.getData();
        ldifGenrator.write(ldifFileName, values, true);
    }

    /**
     * @return
     */
    private void createCSVFile() {
        final String csvFileName = Generator.getUserSetting(Generator.KEY_PATH)
                + fileName + ".csv";
        Generator csvGenrator = new CSVGenerator();
        final String data = csvGenrator.getData();
        csvGenrator.write(csvFileName, data, true);
    }

    /**
     */
    private void createXMLFile() {
        final String xmlFileName = Generator.getUserSetting(Generator.KEY_PATH)
                + fileName + ".xml";
        Generator xmlGenrator = new XMLGenerator();
        final String values = xmlGenrator.getData();
        xmlGenrator.write(xmlFileName, values, true);
    }

    private void updateFromPropertiesFile() {
        FileInputStream inputStream = null;
        try {
            final Properties props = new Properties();
            inputStream = new FileInputStream(BASE_PATH + setupPropertiesFile);
            props.load(inputStream);
            updateValues(props);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore, wanted to close anyway
                }
            }
        }
    }

    /**
     * 
     * @param properties
     */
    private void updateValues(final Properties properties) {
        final Enumeration<?> enume = properties.keys();
        while (enume.hasMoreElements()) {
            final String key = (String) enume.nextElement();
            final String value = properties.getProperty(key);
            final Map<String, String> map = Generator.getMap();
            map.put(key, value);
        }
    }

}
