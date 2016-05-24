/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.Collection;

import javax.ws.rs.WebApplicationException;

/**
 * Backend class for the trigger process resource.
 * 
 * @author miethaner
 */
public class TriggerProcessBackend implements TriggerProcessEndpointBackend {

    @Override
    public TriggerProcess getItem(TriggerRequestParameters params)
            throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<TriggerProcess> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String postItem(TriggerRequestParameters params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String postCollection(TriggerRequestParameters params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putItem(TriggerRequestParameters params, TriggerProcess content)
            throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putCollection(TriggerRequestParameters params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteItem(TriggerRequestParameters params)
            throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteCollection(TriggerRequestParameters params)
            throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putApprove(TriggerRequestParameters params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putReject(TriggerRequestParameters params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putCancel(TriggerRequestParameters params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub

    }

}
