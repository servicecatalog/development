/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 27.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.ror.EntityCache;
import org.oscm.ror.log.LogEntry;

/**
 * @author kulle
 * 
 */
@Named
@RequestScoped
@Path("/endpoint")
public class EndpointResource {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EndpointResource.class);

    @Context
    private UriInfo uriInfo;

    @QueryParam("Version")
    private String version;

    @QueryParam("Locale")
    private String locale;

    @QueryParam("Action")
    private String action;

    @Inject
    private EntityCache cache;

    @Inject
    private FinishSystemCreationTimer systemCreationTimer;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("GetLPlatformDescriptorConfiguration")
    public Response getLPlatformDescriptorConfiguration() {
        GetLPlatformDescriptorConfiguration getLPlatformDescriptorConfiguration = new GetLPlatformDescriptorConfiguration();
        LPlatformDescriptor lplatformdescriptor = new LPlatformDescriptor();
        getLPlatformDescriptorConfiguration
                .setLplatformdescriptor(lplatformdescriptor);
        return Response.ok(getLPlatformDescriptorConfiguration,
                MediaType.APPLICATION_XML).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("CreateLPlatform")
    public Response createLPlatform() {
        LOGGER.debug((new LogEntry(uriInfo)).toString());
        CreateLPlatform lplatform = new CreateLPlatform();
        lplatform.setResponseMessage("Processing completed");
        lplatform.setResponseStatus("SUCCESS");
        cache.put(lplatform.getClass(), lplatform);
        systemCreationTimer.finishSystemCreation(lplatform);
        return Response.ok(lplatform, MediaType.APPLICATION_XML).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("ListLPlatformDescriptor")
    public Response listLPlatformDescriptor() {
        LOGGER.debug((new LogEntry(uriInfo)).toString());
        ListLPlatformDescriptor lplatformdescriptor = new ListLPlatformDescriptor();
        lplatformdescriptor.setResponseMessage("Processing completed");
        lplatformdescriptor.setResponseStatus("SUCCESS");
        List<LPlatformDescriptor> lplatforms = new ArrayList<LPlatformDescriptor>();
        LPlatformDescriptor lplatform = new LPlatformDescriptor();
        lplatforms.add(lplatform);
        lplatformdescriptor.setLplatformdescriptors(lplatforms);
        cache.put(lplatformdescriptor.getClass(), lplatformdescriptor);
        return Response.ok(lplatformdescriptor, MediaType.APPLICATION_XML)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("ListLPlatform")
    public Response listLPlatform() {
        LOGGER.debug((new LogEntry(uriInfo)).toString());
        ListLPlatform listlplatform = new ListLPlatform();
        listlplatform.setResponseMessage("Processing completed");
        listlplatform.setResponseStatus("SUCCESS");
        List<LPlatform> lplatforms = new ArrayList<LPlatform>();
        LPlatform lplatform = new LPlatform();
        lplatforms.add(lplatform);
        listlplatform.setLplatforms(lplatforms);
        cache.put(listlplatform.getClass(), listlplatform);
        return Response.ok(listlplatform, MediaType.APPLICATION_XML).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("GetLPlatformStatus")
    public Response getLPlatformStatus(
            @QueryParam(value = "lplatformId") String lplatformId) {
        LOGGER.debug((new LogEntry(uriInfo)).toString());
        CreateLPlatform lplatform = cache.findLplatform(lplatformId);
        return Response.ok(new LplatformStatus(lplatform),
                MediaType.APPLICATION_XML).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public void handleAllPostRequests() {
        LOGGER.debug((new LogEntry(uriInfo)).toString());
    }

}
