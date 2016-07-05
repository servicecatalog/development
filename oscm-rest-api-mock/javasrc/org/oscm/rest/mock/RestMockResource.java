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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.oscm.rest.mock.data.TriggerProcessRepresentation;

import com.google.gson.Gson;

/**
 * Root resource for Mock endpoints
 * 
 * @author miethaner
 */
@Path("/process")
public class RestMockResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postProcess(String json) {

        Gson gson = new Gson();
        TriggerProcessRepresentation content = gson.fromJson(json,
                TriggerProcessRepresentation.class);

        Logger logger = Logger.getLogger(getClass().getName());

        logger.info("Triggerprocess: " + content.getId() + ", " + json);

        return Response.noContent().build();
    }

}
