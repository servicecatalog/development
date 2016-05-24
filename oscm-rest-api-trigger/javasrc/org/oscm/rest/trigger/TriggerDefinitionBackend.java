/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 4, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ws.rs.WebApplicationException;

import org.oscm.internal.intf.TriggerDefinitionService;
import org.oscm.internal.vo.VOTriggerDefinition;
import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.WebException;
import org.oscm.rest.trigger.TriggerDefinition.Links;
import org.oscm.rest.trigger.TriggerDefinition.Owner;

/**
 * Backend class for the trigger definition resource.
 * 
 * @author miethaner
 */
public class TriggerDefinitionBackend implements
        EndpointBackend<TriggerDefinition, TriggerRequestParameters> {

    @EJB(name = "TriggerDefinitionServiceBean")
    private TriggerDefinitionService service;

    @Override
    public TriggerDefinition getItem(TriggerRequestParameters params)
            throws WebApplicationException {

        TriggerDefinition td1 = new TriggerDefinition(
                UUID.fromString("12345678-1234-1234-1234-123456789012"),
                "test", new Boolean(false), "http://", new Links("42",
                        "SUBSCRIBE_TO_SERVICE"),
                new Owner(UUID
                        .fromString("12345678-1234-1234-1234-123456789012"),
                        "fujitsu"));
        return td1;
    }

    @Override
    public Collection<TriggerDefinition> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {

        Collection<VOTriggerDefinition> voDefinitions = service
                .getTriggerDefinitions();
        Collection<TriggerDefinition> repDefintions = new ArrayList<TriggerDefinition>();

        for (VOTriggerDefinition definition : voDefinitions) {
            repDefintions.add(new TriggerDefinition(definition));
        }

        return repDefintions;
    }

    @Override
    public String postItem(TriggerRequestParameters params,
            TriggerDefinition content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public String postCollection(TriggerRequestParameters params,
            TriggerDefinition item) throws WebApplicationException {

        String id = item.getId() + "," + item.getDescription();

        return id;
    }

    @Override
    public void putItem(TriggerRequestParameters params, TriggerDefinition item)
            throws WebApplicationException {
        // no test action
    }

    @Override
    public void putCollection(TriggerRequestParameters params,
            TriggerDefinition content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void deleteItem(TriggerRequestParameters params)
            throws WebApplicationException {
        // no test action
    }

    @Override
    public void deleteCollection(TriggerRequestParameters params)
            throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

}
