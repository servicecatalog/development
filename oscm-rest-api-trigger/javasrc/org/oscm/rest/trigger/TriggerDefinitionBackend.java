/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 4, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.Collection;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.WebException;

/**
 * Backend class for the trigger definition resource.
 * 
 * @author miethaner
 */
public class TriggerDefinitionBackend implements
        EndpointBackend<TriggerDefinition, TriggerRequestParameters> {

    @Override
    public TriggerDefinition getItem(TriggerRequestParameters params)
            throws WebApplicationException {

        return null;
    }

    @Override
    public Collection<TriggerDefinition> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {

        return null;
    }

    @Override
    public String postItem(TriggerRequestParameters params,
            TriggerDefinition content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public String postCollection(TriggerRequestParameters params,
            TriggerDefinition item) throws WebApplicationException {

        return null;
    }

    @Override
    public void putItem(TriggerRequestParameters params, TriggerDefinition item)
            throws WebApplicationException {

    }

    @Override
    public void putCollection(TriggerRequestParameters params,
            TriggerDefinition content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void deleteItem(TriggerRequestParameters params)
            throws WebApplicationException {

    }

    @Override
    public void deleteCollection(TriggerRequestParameters params)
            throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

}
