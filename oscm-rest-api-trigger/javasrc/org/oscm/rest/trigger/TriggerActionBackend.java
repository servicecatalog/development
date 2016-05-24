/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.WebException;

/**
 * Backend class for the trigger process identifiers resource.
 * 
 * @author miethaner
 */
public class TriggerActionBackend implements
        EndpointBackend<TriggerAction, TriggerRequestParameters> {

    public static final UUID ID_SUBSCRIBE = UUID
            .fromString("14269f7a-2184-11e6-b67b-9e71128cae77");
    public static final UUID ID_UNSUBSCRIBE = UUID
            .fromString("1426a308-2184-11e6-b67b-9e71128cae77");
    public static final UUID ID_MODIFY = UUID
            .fromString("1426a45c-2184-11e6-b67b-9e71128cae77");

    private static final String SUBSCRIBE_TO_SERVICE = "SUBSCRIBE_TO_SERVICE";
    private static final String UNSUBSCRIBE_FROM_SERVICE = "UNSUBSCRIBE_FROM_SERVICE";
    private static final String MODIFY_SUBSCRIPTION = "MODIFY_SUBSCRIPTION";

    private static Map<UUID, TriggerAction> actions = null;

    public TriggerActionBackend() {
        if (actions == null) {
            Map<UUID, TriggerAction> actions = new HashMap<UUID, TriggerAction>();
            actions.put(ID_SUBSCRIBE, new TriggerAction(ID_SUBSCRIBE,
                    SUBSCRIBE_TO_SERVICE));
            actions.put(ID_UNSUBSCRIBE, new TriggerAction(ID_UNSUBSCRIBE,
                    UNSUBSCRIBE_FROM_SERVICE));
            actions.put(ID_MODIFY, new TriggerAction(ID_MODIFY,
                    MODIFY_SUBSCRIPTION));

            TriggerActionBackend.actions = actions;
        }
    }

    @Override
    public TriggerAction getItem(TriggerRequestParameters params)
            throws WebApplicationException {

        UUID id = params.getId();

        if (!actions.containsKey(id)) {
            throw WebException.notFound().build(); // TODO add more info
        }

        return actions.get(id);
    }

    @Override
    public Collection<TriggerAction> getCollection(
            TriggerRequestParameters params) throws WebApplicationException {

        return actions.values();
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
