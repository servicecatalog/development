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
public class RestEndpoint<T extends Representation, K extends RequestParameters> {

    private static final String PARAM_VERSION = "version";

    private EndpointBackend<T, K> backend;

    /**
     * Creates new REST endpoints with the given backend
     * 
     * @param backend
     *            the endpoint backend
     */
    public RestEndpoint(EndpointBackend<T, K> backend) {
        this.backend = backend;
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Path(CommonParams.PATH_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@Context Request request, @InjectParam K params) {

        int version = getVersion(request);

        prepareData(version, params, true, null);

        T item = backend.getItem(params);

        reviseData(version, item);

        return Response.ok(item).build();
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCollection(@Context Request request,
            @InjectParam K params) {

        int version = getVersion(request);

        prepareData(version, params, false, null);

        Collection<T> items = backend.getCollection(params);

        RepresentationCollection<T> collection = new RepresentationCollection<T>(
                items);

        reviseData(version, collection);

        return Response.ok(collection).build();
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Path(CommonParams.PATH_ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postItem(@Context Request request,
            @Context UriInfo uriInfo, @InjectParam K params, T content) {

        int version = getVersion(request);

        prepareData(version, params, true, content);

        String newId = backend.postItem(params, content);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCollection(@Context Request request,
            @Context UriInfo uriInfo, @InjectParam K params, T content) {

        int version = getVersion(request);

        prepareData(version, params, false, content);

        String newId = backend.postCollection(params, content);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Path(CommonParams.PATH_ID)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putItem(@Context Request request, @InjectParam K params,
            T content) {

        int version = getVersion(request);

        prepareData(version, params, true, content);

        backend.putItem(params, content);

        return Response.noContent().build();
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCollection(@Context Request request,
            @InjectParam K params, T content) {

        int version = getVersion(request);

        prepareData(version, params, false, content);

        backend.putCollection(params, content);

        return Response.noContent().build();
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Path(CommonParams.PATH_ID)
    public Response deleteItem(@Context Request request, @InjectParam K params) {

        int version = getVersion(request);

        prepareData(version, params, true, null);

        backend.deleteItem(params);

        return Response.noContent().build();
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Path("")
    public Response deleteCollection(@Context Request request,
            @InjectParam K params) {

        int version = getVersion(request);

        prepareData(version, params, false, null);

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
    protected int getVersion(Request request) throws WebApplicationException {

        ContainerRequest cr = (ContainerRequest) request;
        Object property = cr.getProperties().get(PARAM_VERSION);

        if (property == null) {
            throw WebException.notFound().build(); // TODO add more info
        }

        return ((Integer) property).intValue();
    }

    /**
     * Prepares the data for the backend call
     * 
     * @param version
     *            the version to update
     * @param params
     *            the injected parameters
     * @param withId
     *            if true validate resource id
     * @param rep
     *            the representation (can be null)
     * @throws WebApplicationException
     */
    protected void prepareData(int version, K params, boolean withId,
            Representation rep) throws WebApplicationException {

        if (withId) {
            params.validateResourceId();
        }

        params.validateParameters();

        params.setVersion(version);
        params.update();

        if (rep != null) {
            rep.validateContent();

            rep.setVersion(version);
            rep.update();
        }
    }

    /**
     * Revises the data after the backend call
     * 
     * @param version
     *            the version to convert to
     * @param rep
     *            the representation
     */
    protected void reviseData(int version, Representation rep) {

        if (rep != null) {
            rep.setVersion(version);
            rep.convert();
        }
    }

}
