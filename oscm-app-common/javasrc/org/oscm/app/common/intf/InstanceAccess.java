/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 2016/11/11                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common.intf;

import java.io.Serializable;
import java.util.List;

import org.oscm.app.common.data.ServerInfo;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.AuthenticationException;
import org.oscm.app.v1_0.exceptions.ConfigurationException;

/**
 * @author tateiwamext
 *
 *         Interface of instance to the specific controller implementation used
 *         by common controller code (to avoid dependencies on specific
 *         implementations).
 */
public interface InstanceAccess extends Serializable {

    /**
     * Return server information from IaaS
     * 
     * @return the list of ServerInfo
     * @throws APPlatformException
     * @throws ConfigurationException
     * @throws AuthenticationException
     */
    public List<ServerInfo> getServerDetails(String instanceId)
            throws AuthenticationException, ConfigurationException,
            APPlatformException;

    /**
     * Returns the localized message for the given key, locale and arguments.
     * 
     * @param locale
     *            the locale
     * @param key
     *            the message key
     * @param arguments
     *            optional arguments
     * @return the localized message
     */
    public String getMessage(String locale, String key, Object... arguments);
}
