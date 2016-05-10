/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.BadRequestException;

import org.oscm.rest.common.RepresentationWithVersion;

/**
 * Representation class of trigger processes.
 * 
 * @author miethaner
 */
public class TriggerProcess extends RepresentationWithVersion {

    @Override
    public void validateContent() throws BadRequestException {
        // TODO validate content
    }

    @Override
    public void update(int version) {
        setVersion(version);

        // nothing to update in version 1
    }

    @Override
    public void convert(int version) {
        setVersion(version);

        // nothing to convert in version 1
    }

}
