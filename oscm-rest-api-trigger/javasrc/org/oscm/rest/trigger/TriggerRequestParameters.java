/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 24, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.UUID;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.RequestParameters;

/**
 * InjectParam class for trigger endpoints
 * 
 * @author miethaner
 */
public class TriggerRequestParameters extends RequestParameters {

    @QueryParam(TriggerParams.PARAM_OWNER_ID)
    private UUID owner_id;

    @QueryParam(TriggerParams.PARAM_AUTHOR_ID)
    private UUID author_id;

    public UUID getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(UUID owner_id) {
        this.owner_id = owner_id;
    }

    public UUID getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(UUID author_id) {
        this.author_id = author_id;
    }

    @Override
    public void validateParameters() throws WebApplicationException {
        // TODO validate parameters
    }

    @Override
    public void update() {
        // nothing to update
    }

}
