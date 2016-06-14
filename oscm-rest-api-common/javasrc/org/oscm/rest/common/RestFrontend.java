/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 * Interfaces for REST endpoints
 * 
 * @author miethaner
 */
public interface RestFrontend {

    /**
     * Interface for HTTP GET methods.
     * 
     * @author miethaner
     *
     * @param <P>
     *            request parameters
     */
    public interface Get<P extends RequestParameters> {

        /**
         * Gets the corresponding representation of the entry with the id in
         * params and wraps it in the response.
         * 
         * @param request
         *            the request context
         * @param params
         *            the request parameters
         * @return the response with the representation
         * @throws WebApplicationException
         */
        public Response getItem(Request request, P params)
                throws WebApplicationException;

        /**
         * Gets all valid entry representations and wraps them in the response.
         * 
         * @param request
         *            the request context
         * @param params
         *            the request parameters
         * @return the response with the representations
         * @throws WebApplicationException
         */
        public Response getCollection(Request request, P params)
                throws WebApplicationException;
    }

    /**
     * Interface for HTTP POST methods.
     * 
     * @author miethaner
     *
     * @param <R>
     *            a representation
     * @param <P>
     *            request parameters
     */
    public interface Post<R extends Representation, P extends RequestParameters> {

        /**
         * Creates a new entry from the given representation and returns its id
         * within the location header of the response.
         * 
         * @param request
         *            the request context
         * @param content
         *            the representation to create
         * @param params
         *            the request parameters
         * @return the response with the location
         * @throws WebApplicationException
         */
        public Response postCollection(Request request, R content, P params)
                throws WebApplicationException;
    }

    /**
     * Interface for HTTP PUT methods.
     * 
     * @author miethaner
     *
     * @param <R>
     *            a representation
     * @param <P>
     *            request parameters
     */
    public interface Put<R extends Representation, P extends RequestParameters> {

        /**
         * Updates the entry with the id in params with the given
         * representation.
         * 
         * @param request
         *            the request context
         * @param content
         *            the representation to update
         * @param params
         *            the request parameters
         * @return the response without content
         * @throws WebApplicationException
         */
        public Response putItem(Request request, R content, P params)
                throws WebApplicationException;
    }

    /**
     * Interface for HTTP DELETE methods.
     * 
     * @author miethaner
     *
     * @param <P>
     *            request parameters
     */
    public interface Delete<P extends RequestParameters> {

        /**
         * Deletes the entry with the id in params.
         * 
         * @param request
         *            the request context
         * @param params
         *            the request parameters
         * @return the response without content
         * @throws WebApplicationException
         */
        public Response deleteItem(Request request, P params)
                throws WebApplicationException;
    }

    /**
     * Interface for all standard methods.
     * 
     * @author miethaner
     *
     * @param <R>
     *            a representation
     * @param <P>
     *            request parameters
     */
    public interface Crud<R extends Representation, P extends RequestParameters>
            extends Get<P>, Post<R, P>, Put<R, P>, Delete<P> {
    }

}
