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

import org.oscm.rest.common.EndpointBackend;

/**
 * Backend class for the trigger process parameter resource.
 * 
 * @author miethaner
 */
public class TriggerProcessParameterBackend implements
        EndpointBackend<TriggerProcessParameter, TriggerRequestParameters> {

    @Override
    public TriggerProcessParameter getItem(TriggerRequestParameters params)
            throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<TriggerProcessParameter> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String postItem(TriggerRequestParameters params,
            TriggerProcessParameter content) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String postCollection(TriggerRequestParameters params,
            TriggerProcessParameter content) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putItem(TriggerRequestParameters params,
            TriggerProcessParameter content) throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putCollection(TriggerRequestParameters params,
            TriggerProcessParameter content) throws WebApplicationException {
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

}
