/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 23, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.EndpointBackend;

/**
 * Specialized endpoint backend for trigger process
 * 
 * @author miethaner
 */
public interface TriggerProcessEndpointBackend extends
        EndpointBackend<TriggerProcessRepresentation, TriggerRequestParameters> {

    /**
     * Tries to approve the trigger process
     * 
     * @param params
     *            the query params of the request
     * @param content
     *            the trigger process
     * @throws WebApplicationException
     */
    public void putApprove(TriggerRequestParameters params,
            TriggerProcessRepresentation content) throws WebApplicationException;

    /**
     * Rejects the trigger process
     * 
     * @param params
     *            the query params of the request
     * @param content
     *            the trigger process
     * @throws WebApplicationException
     */
    public void putReject(TriggerRequestParameters params,
            TriggerProcessRepresentation content) throws WebApplicationException;

    /**
     * Cancels the trigger process
     * 
     * @param params
     *            the query params of the request
     * @param content
     *            the trigger process
     * @throws WebApplicationException
     */
    public void putCancel(TriggerRequestParameters params,
            TriggerProcessRepresentation content) throws WebApplicationException;

}
