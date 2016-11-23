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

import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;
import org.oscm.app.v2_0.exceptions.ConfigurationException;

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
     * @return the list of ServerInformation which is contain id, name, status,
     *         type, public IPs and private IPs
     * @throws APPlatformException
     * @throws ConfigurationException
     * @throws AuthenticationException
     */
    public List<? extends ServerInformation> getServerDetails(String instanceId)
            throws AuthenticationException, ConfigurationException,
            APPlatformException;

    /**
     * Return access information
     * 
     * @return access information
     * @throws APPlatformException
     * @throws ConfigurationException
     * @throws AuthenticationException
     */
    public String getAccessInfo(String instanceId)
            throws AuthenticationException, ConfigurationException,
            APPlatformException;

    /**
     * Returns the localized message for the given key, locale and arguments.
     * For now it is not used so please return null.
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
