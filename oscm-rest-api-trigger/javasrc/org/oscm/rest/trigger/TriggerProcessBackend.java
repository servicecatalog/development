/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.trigger.interfaces.TriggerProcessRest;
import org.oscm.rest.trigger.interfaces.TriggerProcessRestService;

/**
 * Backend class for the trigger process resource.
 * 
 * @author miethaner
 */
@Stateless
@LocalBean
public class TriggerProcessBackend implements TriggerProcessEndpointBackend {

    private TriggerProcessRestService service;

    @Override
    public TriggerProcessRepresentation getItem(TriggerRequestParameters params)
            throws WebApplicationException {

        TriggerProcessRest process = service.getProcess(params.getId());

        return new TriggerProcessRepresentation(process);
    }

    @Override
    public Collection<TriggerProcessRepresentation> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {

        Collection<TriggerProcessRest> processes = service.getProcesses();

        Collection<TriggerProcessRepresentation> representationList = new ArrayList<TriggerProcessRepresentation>();

        for (TriggerProcessRest d : processes) {
            representationList.add(new TriggerProcessRepresentation(d));
        }

        return representationList;
    }

    @Override
    public UUID postCollection(TriggerRequestParameters params,
            TriggerProcessRepresentation content)
            throws WebApplicationException {

        return service.createProcess(content);
    }

    @Override
    public void putItem(TriggerRequestParameters params,
            TriggerProcessRepresentation content)
            throws WebApplicationException {

        service.updateProcess(content);
    }

    @Override
    public void deleteItem(TriggerRequestParameters params)
            throws WebApplicationException {

        service.deleteProcess(params.getId());
    }

    @Override
    public void putApprove(TriggerRequestParameters params,
            TriggerProcessRepresentation content)
            throws WebApplicationException {

        service.approve(content);
    }

    @Override
    public void putReject(TriggerRequestParameters params,
            TriggerProcessRepresentation content)
            throws WebApplicationException {

        service.reject(content);
    }

    @Override
    public void putCancel(TriggerRequestParameters params,
            TriggerProcessRepresentation content)
            throws WebApplicationException {

        service.cancel(content);
    }

}
