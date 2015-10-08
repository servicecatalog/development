/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 11.10.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.example.generator;

import java.io.File;

import org.apache.axis2.wsdl.WSDL2Java;

/**
 * @author weiser
 * 
 */
public class JavaGenerator {

    /**
     * Generates the provisioning service skeleton and the event and session
     * service stubs.
     * 
     * @param args
     *            one argument - the path where the required wsdl and xsd files
     *            are located.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String projectPath = new File("").getAbsolutePath();
        String wsdlBasePath = projectPath;
        if (args != null && args.length > 0 && args[0] != null) {
            wsdlBasePath = args[0];
        }
        System.out.println(projectPath);
        System.out.println(wsdlBasePath);

        String[] serverArgs = new String[8];
        serverArgs[0] = "-uri";
        serverArgs[1] = wsdlBasePath + File.separator
                + "ProvisioningService.wsdl";
        serverArgs[2] = "-o";
        serverArgs[3] = projectPath;
        serverArgs[4] = "-p";
        serverArgs[5] = "org.oscm.example.server";
        serverArgs[6] = "-ss";
        serverArgs[7] = "-sd";
        WSDL2Java.main(serverArgs);

        String[] clientArgs = new String[6];
        clientArgs[0] = "-uri";
        clientArgs[1] = wsdlBasePath + File.separator + "SessionService.wsdl";
        clientArgs[2] = "-o";
        clientArgs[3] = projectPath;
        clientArgs[4] = "-p";
        clientArgs[5] = "org.oscm.example.client";
        WSDL2Java.main(clientArgs);

        clientArgs[1] = wsdlBasePath + File.separator + "EventService.wsdl";
        WSDL2Java.main(clientArgs);
    }
}
