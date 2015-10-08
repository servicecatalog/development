/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 19.03.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.mockpsp.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Auxiliary class to store the parameters for a particular session in memory.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class ParameterStorage {

    private static Map<String, Map<?, ?>> paramStore = new HashMap<String, Map<?, ?>>();

    /**
     * Stores the parameters for a certain session.
     * 
     * @param sessionId
     *            The session identifier.
     * @param parameterMap
     *            The parameter map to be stored.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static synchronized void addSessionParams(String sessionId,
            Map<?, ?> parameterMap) {
        Map tempMap = new HashMap();
        tempMap.putAll(parameterMap);
        paramStore.put(sessionId, tempMap);
    }

    /**
     * Retrieves the session related parameters from the parameter store and
     * deletes them (read only once).
     * 
     * @param sessionId
     *            The identifier of the session.
     * @return The parameters stored for the session.
     */
    public static synchronized Map<?, ?> getSessionParamsOnce(String sessionId) {
        return paramStore.remove(sessionId);
    }

    /**
     * Retrieves the session related parameters from the parameter store
     * 
     * @param sessionId
     *            The identifier of the session.
     * @return The parameters stored for the session.
     */
    public static synchronized Map<?, ?> getSessionParams(String sessionId) {
        return paramStore.get(sessionId);
    }

}
