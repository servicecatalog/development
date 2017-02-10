/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 30.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author kulle
 * 
 */
public class LogFileHandler {

    public void readLogFile(PrintWriter writer) throws FileNotFoundException,
            IOException {
        String pathToLogfile = getLogfilePath();
        try (BufferedReader reader = new BufferedReader(new FileReader(
                pathToLogfile))) {
            String line = null;
            while (reader.ready() && (line = reader.readLine()) != null) {
                writer.print(line);
                writer.print("<br/>");
            }
        }
    }

    public void clearLogFile(PrintWriter writer) throws FileNotFoundException,
            IOException {

        String pathToLogfile = getLogfilePath();
        try (FileOutputStream fos = new FileOutputStream(pathToLogfile);) {
            fos.write("".getBytes());
        }

        writer.print("");
    }

    private String getLogfilePath() {
        String instanceRootDir = System.getProperty("com.sun.aas.instanceRoot");
        String pathToLogfile = instanceRootDir + File.separator + "logs"
                + File.separator + "ror-stub.log";
        return pathToLogfile;
    }

}
