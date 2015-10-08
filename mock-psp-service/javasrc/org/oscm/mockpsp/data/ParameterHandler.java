/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 19.03.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.mockpsp.data;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Auxiliary class providing functionality to handle parameters.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class ParameterHandler {

    /**
     * * Writes the parameters contained in the requests parameter map to the
     * given writer.
     * 
     * @param writer
     *            The writer to write to.
     * @param parameterMap
     *            The map to be evaluated.
     */
    public static void addParametersToWriterInput(PrintWriter writer,
            Map<?, ?> parameterMap) {
        if (parameterMap == null) {
            return;
        }
        Set<?> keySet = parameterMap.keySet();
        if (keySet != null) {
            Iterator<?> parameterNames = keySet.iterator();

            while (parameterNames.hasNext()) {
                String paramName = (String) parameterNames.next();
                Object object = parameterMap.get(paramName);
                String[] output = (String[]) object;
                writer.printf("%s=%s&", paramName, output[0]);
            }
        }
    }

}
