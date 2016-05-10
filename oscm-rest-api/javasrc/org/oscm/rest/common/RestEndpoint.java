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

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Common REST endpoint class.
 * 
 * @author miethaner
 */
public class RestEndpoint<T extends RepresentationWithVersion> extends
        RestResource {

    private static final String PARAM_VERSION = "version";

    @Context
    private Request request;

    private int version;
    private EndpointBackend<T> backend;

    /**
     * Creates new REST endpoints with the given backend
     * 
     * @param backend
     *            the endpoint backend
     */
    public RestEndpoint(EndpointBackend<T> backend) {
        ContainerRequest cr = (ContainerRequest) request;
        version = ((Integer) cr.getProperties().get(PARAM_VERSION)).intValue();

        this.backend = backend;
    }

    @Since(1)
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@BeanParam RequestParametersGet params) {

        params.validateResourceId();
        params.validateParameters();

        params.update(version);

        T item = backend.getItem(params);

        item.convert(version);

        return Response.ok(item).build();
    }

    @Since(1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCollection(@BeanParam RequestParametersGet params) {

        params.validateParameters();

        params.update(version);

        Collection<T> items = backend.getCollection(params);

        RepresentationCollection<T> collection = new RepresentationCollection<T>(
                items);

        collection.convert(version);

        return Response.ok(collection).build();
    }

    @Since(1)
    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postItem(@Context UriInfo uriInfo,
            @BeanParam RequestParametersPost params, T content) {

        params.validateResourceId();
        params.validateParameters();
        content.validateContent();

        params.update(version);
        content.update(version);

        String newId = backend.postCollection(params, content);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(1)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCollection(@Context UriInfo uriInfo,
            @BeanParam RequestParametersPost params, T content) {

        params.validateParameters();
        content.validateContent();

        params.update(version);
        content.update(version);

        String newId = backend.postCollection(params, content);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(1)
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putItem(@BeanParam RequestParametersPut params, T content) {

        params.validateResourceId();
        params.validateParameters();
        content.validateContent();

        params.update(version);
        content.update(version);

        backend.putItem(params, content);

        return Response.noContent().build();
    }

    @Since(1)
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCollection(@BeanParam RequestParametersPut params,
            T content) {

        params.validateParameters();
        content.validateContent();

        params.update(version);
        content.update(version);

        backend.putCollection(params, content);

        return Response.noContent().build();
    }

    @Since(1)
    @DELETE
    @Path("{id}")
    public Response deleteItem(@BeanParam RequestParametersDelete params) {

        params.validateResourceId();
        params.validateParameters();

        params.update(version);

        backend.deleteItem(params);

        return Response.noContent().build();
    }

    @Since(1)
    @DELETE
    public Response deleteCollection(@BeanParam RequestParametersDelete params) {

        params.validateParameters();

        params.update(version);

        backend.deleteCollection(params);

        return Response.noContent().build();
    }
}
