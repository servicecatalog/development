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
 * @author miethaner
 *
 */
public class TriggerRequestParameters extends RequestParameters {

    @Override
    public void validateParameters() throws WebApplicationException {

    }

    @Override
    public void update() {
        // nothing to update
    }

}
