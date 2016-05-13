/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Common REST endpoint class.
 * 
 * @author miethaner
 */
public class RestEndpoint<T extends Representation> {

    private static final String PARAM_VERSION = "version";

    private EndpointBackend<T> backend;

    /**
     * Creates new REST endpoints with the given backend
     * 
     * @param backend
     *            the endpoint backend
     */
    public RestEndpoint(EndpointBackend<T> backend) {
        this.backend = backend;
    }

    @Since(1)
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@Context Request request,
            @InjectParam RequestParametersGet params) {

        int version = getVersion(request);
        params.validateResourceId();
        params.validateParameters();

        params.setVersion(version);
        params.update();

        T item = backend.getItem(params);

        item.setVersion(version);
        item.convert();

        return Response.ok(item).build();
    }

    @Since(1)
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCollection(@Context Request request,
            @InjectParam RequestParametersGet params) {

        int version = getVersion(request);

        params.validateParameters();

        params.setVersion(version);
        params.update();

        Collection<T> items = backend.getCollection(params);

        RepresentationCollection<T> collection = new RepresentationCollection<T>(
                items);

        collection.setVersion(version);
        collection.convert();

        return Response.ok(collection).build();
    }

    @Since(1)
    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postItem(@Context Request request,
            @Context UriInfo uriInfo,
            @InjectParam RequestParametersPost params, T content) {

        int version = getVersion(request);

        params.validateResourceId();
        params.validateParameters();
        content.validateContent();

        params.setVersion(version);
        params.update();
        content.setVersion(version);
        content.update();

        String newId = backend.postCollection(params, content);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(1)
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCollection(@Context Request request,
            @Context UriInfo uriInfo,
            @InjectParam RequestParametersPost params, T content) {

        int version = getVersion(request);

        params.validateParameters();
        content.validateContent();

        params.setVersion(version);
        params.update();
        content.setVersion(version);
        content.update();

        String newId = backend.postCollection(params, content);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(1)
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putItem(@Context Request request,
            @InjectParam RequestParametersPut params, T content) {

        int version = getVersion(request);

        params.validateResourceId();
        params.validateParameters();
        content.validateContent();

        params.setVersion(version);
        params.update();
        content.setVersion(version);
        content.update();

        backend.putItem(null, content);

        return Response.noContent().build();
    }

    @Since(1)
    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCollection(@Context Request request,
            @InjectParam RequestParametersPut params, T content) {

        int version = getVersion(request);

        params.validateParameters();
        content.validateContent();

        params.setVersion(version);
        params.update();
        content.setVersion(version);
        content.update();

        backend.putCollection(null, content);

        return Response.noContent().build();
    }

    @Since(1)
    @DELETE
    @Path("{id}")
    public Response deleteItem(@Context Request request,
            @InjectParam RequestParametersDelete params) {

        int version = getVersion(request);

        params.validateResourceId();
        params.validateParameters();

        params.setVersion(version);
        params.update();

        backend.deleteItem(params);

        return Response.noContent().build();
    }

    @Since(1)
    @DELETE
    @Path("")
    public Response deleteCollection(@Context Request request,
            @InjectParam RequestParametersDelete params) {

        int version = getVersion(request);

        params.validateParameters();

        params.setVersion(version);
        params.update();

        backend.deleteCollection(params);

        return Response.noContent().build();
    }

    /**
     * Extracts the version number from the container request properties. Throws
     * Exception if property is null.
     * 
     * @param request
     *            the container request
     * @return the version number
     * @throws WebApplicationException
     */
    private int getVersion(Request request) throws WebApplicationException {

        ContainerRequest cr = (ContainerRequest) request;
        Object property = cr.getProperties().get(PARAM_VERSION);

        if (property == null) {
            throw WebException.notFound().build(); // TODO add more info
        }

        return ((Integer) property).intValue();
    }
}
