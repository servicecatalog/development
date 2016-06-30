/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 24, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.RequestParameters;

/**
 * InjectParam class for trigger endpoints
 * 
 * @author miethaner
 */
public class TriggerParameters extends RequestParameters {

    @Override
    public void validateParameters() throws WebApplicationException {
        // nothing to validate
    }

    @Override
    public void update() {
        // nothing to update
    }

}
