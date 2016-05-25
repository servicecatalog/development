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
import org.oscm.rest.common.WebException;

/**
 * Backend class for the trigger action resource.
 * 
 * @author miethaner
 */
public class TriggerActionBackend implements
        EndpointBackend<TriggerAction, TriggerRequestParameters> {

    @Override
    public TriggerAction getItem(TriggerRequestParameters params)
            throws WebApplicationException {

        return null;
    }

    @Override
    public Collection<TriggerAction> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {

        return null;
    }

    @Override
    public String postItem(TriggerRequestParameters params,
            TriggerAction content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public String postCollection(TriggerRequestParameters params,
            TriggerAction content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void putItem(TriggerRequestParameters params, TriggerAction content)
            throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void putCollection(TriggerRequestParameters params,
            TriggerAction content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void deleteItem(TriggerRequestParameters params)
            throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void deleteCollection(TriggerRequestParameters params)
            throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

}
