/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Super class for REST resources and their endpoints.
 * 
 * @author miethaner
 */
public abstract class RestResource {

    /**
     * Wrapper for backend GET commands. Prepares, validates and revises data
     * for commands and assembles responses.
     * 
     * @param request
     *            the request context
     * @param backend
     *            the backend command
     * @param params
     *            the request parameters
     * @param id
     *            true if id needs to be validated
     * @return the response with representation or -collection
     */
    protected <R extends Representation, P extends RequestParameters> Response get(
            Request request, RestBackend.Get<R, P> backend, P params, boolean id) {

        int version = getVersion(request);

        prepareData(version, params, id, null, false);

        Representation item = backend.get(params);

        reviseData(version, item);

        String tag = "";
        if (item.getETag() != null) {
            tag = item.getETag().toString();
        }

        return Response.ok(item).tag(tag).build();
    }

    /**
     * Wrapper for backend POST commands. Prepares, validates and revises data
     * for commands and assembles responses.
     * 
     * @param request
     *            the request context
     * @param backend
     *            the backend command
     * @param content
     *            the representation to create
     * @param params
     *            the request parameters
     * @return the response with the new location
     */
    protected <R extends Representation, P extends RequestParameters> Response post(
            Request request, RestBackend.Post<R, P> backend, R content, P params) {

        int version = getVersion(request);

        prepareData(version, params, false, content, true);

        Object newId = backend.post(content, params);

        ContainerRequest cr = (ContainerRequest) request;
        UriBuilder builder = cr.getAbsolutePathBuilder();
        URI uri = builder.path(newId.toString()).build();

        return Response.created(uri).build();
    }

    /**
     * Wrapper for backend PUT commands. Prepares, validates and revises data
     * for commands and assembles responses. Also overrides the id of the
     * representation with the id of the parameters.
     * 
     * @param request
     *            the request context
     * @param backend
     *            the backend command
     * @param content
     *            the representation to update
     * @param params
     *            the request parameters
     * @return the response without content
     */
    protected <R extends Representation, P extends RequestParameters> Response put(
            Request request, RestBackend.Put<R, P> backend, R content, P params) {

        int version = getVersion(request);

        prepareData(version, params, true, content, true);

        if (content != null) {
            content.setId(params.getId());
            content.setETag(params.getETag());
        }

        backend.put(content, params);

        return Response.noContent().build();
    }

    /**
     * Wrapper for backend DELETE commands. Prepares, validates and revises data
     * for commands and assembles responses.
     * 
     * @param request
     *            the request context
     * @param backend
     *            the backend command
     * @param params
     *            the request parameters
     * @return the response without content
     */
    protected <P extends RequestParameters> Response delete(Request request,
            RestBackend.Delete<P> backend, P params) {

        int version = getVersion(request);

        prepareData(version, params, true, null, false);

        backend.delete(params);

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
        Object property = cr.getProperties().get(CommonParams.PARAM_VERSION);

        if (property == null) {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_VERSION).build();
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
    protected void prepareData(int version, RequestParameters params,
            boolean withId, Representation rep, boolean withRep)
            throws WebApplicationException {

        if (withId) {
            params.validateId();
        }

        params.validateETag();
        params.validateParameters();

        params.setVersion(version);
        params.update();

        if (withRep) {

            if (rep == null) {
                throw WebException.badRequest()
                        .message(CommonParams.ERROR_MISSING_CONTENT).build();
            }

            rep.validateContent();

            rep.setVersion(new Integer(version));
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
            rep.setVersion(new Integer(version));
            rep.convert();
        }
    }
}
