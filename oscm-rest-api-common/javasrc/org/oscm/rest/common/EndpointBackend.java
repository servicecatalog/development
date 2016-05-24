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
public interface EndpointBackend<T extends Representation, K extends RequestParameters> {

    /**
     * Gets the item from resource for the given id
     * 
     * @param params
     *            the query parameters and item id of the request
     * @return item as JSON
     * @throws WebApplicationException
     */
    public T getItem(K params) throws WebApplicationException;

    /**
     * Gets all valid items from resource
     * 
     * @param params
     *            the query parameters of the request
     * @return items as JSON
     * @throws WebApplicationException
     */
    public Collection<T> getCollection(K params) throws WebApplicationException;

    /**
     * Creates the given item for the given id
     * 
     * @param params
     *            the query parameters and item id of the request
     * @param content
     *            the new item
     * @return the new resource id
     * @throws WebApplicationException
     */
    public String postItem(K params, T content) throws WebApplicationException;

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
    public String postCollection(K params, T content)
            throws WebApplicationException;

    /**
     * Updates the given item with the given id
     * 
     * @param params
     *            the query parameters and item id of the request
     * @param content
     *            the updated item
     * @throws WebApplicationException
     */
    public void putItem(K params, T content) throws WebApplicationException;

    /**
     * Updates the given items
     * 
     * @param params
     *            the query parameters of the request
     * @param content
     *            the updated items
     * @throws WebApplicationException
     */
    public void putCollection(K params, T content)
            throws WebApplicationException;

    /**
     * Deletes the item with the given id
     * 
     * @param params
     *            the query parameters and item id of the request
     * @throws WebApplicationException
     */
    public void deleteItem(K params) throws WebApplicationException;

    /**
     * Deletes all valid items
     * 
     * @param params
     *            the query parameters of the request
     * @throws WebApplicationException
     */
    public void deleteCollection(K params) throws WebApplicationException;
}
