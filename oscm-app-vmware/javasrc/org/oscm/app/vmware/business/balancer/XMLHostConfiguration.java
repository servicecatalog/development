/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2013 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 30.01.2013                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.business.balancer;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Same as the default apache XML configuration entity, but with disabled
 * delimiter functionality.
 * 
 * @author soehnges
 */
public class XMLHostConfiguration extends XMLConfiguration {

    private static final long serialVersionUID = -2209965493418921967L;

    public XMLHostConfiguration() {
        super.setDelimiterParsingDisabled(true);
        AbstractConfiguration.setDefaultListDelimiter((char) 0);
    }
}
