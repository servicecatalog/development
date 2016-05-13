/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 29, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.util.Collection;

import javax.ws.rs.WebApplicationException;

/**
 * Common endpoint backend interface.
 * 
 * @author miethaner
 */
public interface EndpointBackend<T extends Representation> {

    /**
     * Gets the item from resource for the given id
     * 
     * @param params
     *            the query parameters of the request
     * @return item as JSON
     * @throws WebApplicationException
     */
    public T getItem(RequestParametersGet params)
            throws WebApplicationException;

    /**
     * Gets all valid items from resource
     * 
     * @param params
     *            the query parameters of the request
     * @return items as JSON
     * @throws WebApplicationException
     */
    public Collection<T> getCollection(RequestParametersGet params)
            throws WebApplicationException;

    /**
     * Creates the given item for the given id
     * 
     * @param params
     *            the query parameters of the request
     * @param content
     *            the new item
     * @return the new resource id
     * @throws WebApplicationException
     */
    public String postItem(RequestParametersPost params, T content)
            throws WebApplicationException;

    /**
     * Creates the given items
     * 
     * @param params
     *            the query parameters of the request
     * @param content
     *            the new items
     * @return the new resource id
     * @throws WebApplicationException
     */
    public String postCollection(RequestParametersPost params, T content)
            throws WebApplicationException;

    /**
     * Updates the given item with the given id
     * 
     * @param params
     *            the query parameters of the request
     * @param content
     *            the updated item
     * @throws WebApplicationException
     */
    public void putItem(RequestParametersPut params, T content)
            throws WebApplicationException;

    /**
     * Updates the given items
     * 
     * @param params
     *            the query parameters of the request
     * @param content
     *            the updated items
     * @throws WebApplicationException
     */
    public void putCollection(RequestParametersPut params, T content)
            throws WebApplicationException;

    /**
     * Deletes the item with the given id
     * 
     * @param params
     *            the query parameters of the request
     * @throws WebApplicationException
     */
    public void deleteItem(RequestParametersDelete params)
            throws WebApplicationException;

    /**
     * Deletes all valid items
     * 
     * @param params
     *            the query parameters of the request
     * @throws WebApplicationException
     */
    public void deleteCollection(RequestParametersDelete params)
            throws WebApplicationException;
}
