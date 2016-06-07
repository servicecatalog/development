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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.WebException;
import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.DatabaseConflictException;
import org.oscm.rest.external.exceptions.DatabaseErrorException;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRest;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRestService;

/**
 * Backend class for the trigger definition resource.
 * 
 * @author miethaner
 */
@Stateless
@LocalBean
public class TriggerDefinitionBackend
        implements
        EndpointBackend<TriggerDefinitionRepresentation, TriggerRequestParameters> {

    private TriggerDefinitionRestService service;

    @Override
    public TriggerDefinitionRepresentation getItem(
            TriggerRequestParameters params) throws WebApplicationException {

        TriggerDefinitionRest definition;
        try {
            definition = service.getDefinition(params.getId().toString());
        } catch (DatabaseErrorException e) {
            throw WebException.conflict().build(); // TODO add more info
        } catch (AuthorizationException e) {
            throw WebException.forbidden().build(); // TODO add more info
        }

        return new TriggerDefinitionRepresentation(definition);
    }

    @Override
    public Collection<TriggerDefinitionRepresentation> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {

        Collection<TriggerDefinitionRest> definitions = service
                .getDefinitions();

        Collection<TriggerDefinitionRepresentation> representationList = new ArrayList<TriggerDefinitionRepresentation>();

        for (TriggerDefinitionRest d : definitions) {
            representationList.add(new TriggerDefinitionRepresentation(d));
        }

        return representationList;
    }

    @Override
    public UUID postCollection(TriggerRequestParameters params,
            TriggerDefinitionRepresentation item)
            throws WebApplicationException {

        try {
            return UUID.fromString(service.createDefiniton(item));
        } catch (DatabaseErrorException e) {
            throw WebException.conflict().build(); // TODO add more info
        } catch (DatabaseConflictException e) {
            throw WebException.notFound().build(); // TODO add more info
        }
    }

    @Override
    public void putItem(TriggerRequestParameters params,
            TriggerDefinitionRepresentation item)
            throws WebApplicationException {

        item.setId(params.getId());

        try {
            service.updateDefinition(item);
        } catch (DatabaseErrorException e) {
            throw WebException.conflict().build(); // TODO add more info
        } catch (DatabaseConflictException e) {
            throw WebException.notFound().build(); // TODO add more info
        } catch (AuthorizationException e) {
            throw WebException.forbidden().build(); // TODO add more info
        }
    }

    @Override
    public void deleteItem(TriggerRequestParameters params)
            throws WebApplicationException {

        try {
            service.deleteDefinition(params.getId().toString());
        } catch (DatabaseErrorException e) {
            throw WebException.conflict().build(); // TODO add more info
        } catch (DatabaseConflictException e) {
            throw WebException.notFound().build(); // TODO add more info
        } catch (AuthorizationException e) {
            throw WebException.forbidden().build(); // TODO add more info
        }
    }
}
