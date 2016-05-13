/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.BadRequestException;

/**
 * InjectParam class for post requests
 * 
 * @author miethaner
 */
public class RequestParametersPost extends RequestParameters {

    @Override
    public void validateParameters() throws BadRequestException {
        // TODO Auto-generated method stub
    }

    @Override
    public void update() {
        // nothing to update in version 1
    }
}
