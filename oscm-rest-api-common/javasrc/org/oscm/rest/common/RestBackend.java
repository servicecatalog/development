/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;

/**
 * Interfaces for backend commands.
 * 
 * @author miethaner
 */
public interface RestBackend {

    /**
     * Interface for GET commands.
     * 
     * @author miethaner
     *
     * @param <R>
     *            a representation
     * @param <P>
     *            request parameters
     */
    public interface Get<R extends Representation, P extends RequestParameters> {

        /**
         * Backend command called by GET frontend methods. Reads the entities
         * specified by the parameters and returns them.
         * 
         * @param params
         *            the request parameters
         * @return the requested representation or -collection
         * @throws WebApplicationException
         */
        public R get(P params) throws WebApplicationException;
    }

    /**
     * Interface for POST commands.
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
         * Backend command called by POST frontend methods. Creates the entity
         * specified by the representation and parameters and returns the its
         * new id.
         * 
         * @param content
         *            the representation to create
         * @param params
         *            the request parameters
         * @return the id object
         * @throws WebApplicationException
         */
        public Object post(R content, P params) throws WebApplicationException;
    }

    /**
     * Interface for PUT commands.
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
         * Backend command called by PUT frontend methods. Updates the entity
         * specified by the representation and parameters.
         * 
         * @param content
         *            the representation to update
         * @param params
         *            the request parameters
         * @throws WebApplicationException
         */
        public void put(R content, P params) throws WebApplicationException;
    }

    /**
     * Interface for DELETE commands.
     * 
     * @author miethaner
     *
     * @param <P>
     *            request parameters
     */
    public interface Delete<P extends RequestParameters> {

        /**
         * Backend command called by DELETE frontend methods. Deletes the entity
         * specified by the parameters.
         * 
         * @param params
         *            the request parameters
         * @throws WebApplicationException
         */
        public void delete(P params) throws WebApplicationException;
    }

}
