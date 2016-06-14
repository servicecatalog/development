/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 14, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.mock;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.oscm.rest.common.RestResource;
import org.oscm.rest.mock.data.TriggerProcessRepresentation;

/**
 * Root resource for Mock endpoints
 * 
 * @author miethaner
 */
@Path("/process")
public class RestMockResource extends RestResource {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putProcess(TriggerProcessRepresentation content) {

        Logger logger = Logger.getLogger(getClass().getName());

        logger.info("Triggerprocess: " + content.getId() + ", "
                + content.getComment() + ", " + content.getDefinitionId()
                + ", " + content.getAuthorId() + ", " + content.getStatusRest());

        return Response.noContent().build();
    }

}
