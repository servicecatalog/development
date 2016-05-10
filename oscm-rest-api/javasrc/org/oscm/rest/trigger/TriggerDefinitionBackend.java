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
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.RequestParametersDelete;
import org.oscm.rest.common.RequestParametersGet;
import org.oscm.rest.common.RequestParametersPost;
import org.oscm.rest.common.RequestParametersPut;
import org.oscm.rest.common.WebException;
import org.oscm.rest.trigger.TriggerDefinition.Owner;

/**
 * Backend class for the trigger definition resource.
 * 
 * @author miethaner
 */
public class TriggerDefinitionBackend implements
        EndpointBackend<TriggerDefinition> {

    @Override
    public TriggerDefinition getItem(RequestParametersGet params)
            throws WebApplicationException {

        TriggerDefinition td1 = new TriggerDefinition(
                "12345678-1234-1234-1234-123456789012", "test",
                TriggerDefinition.Action.SUBSCRIBE_TO_SERVICE, false,
                "http://", new Owner("42", "fujitsu"));

        return td1;
    }

    @Override
    public Collection<TriggerDefinition> getCollection(
            RequestParametersGet params) throws WebApplicationException {

        TriggerDefinition td1 = new TriggerDefinition(
                "12345678-1234-1234-1234-123456789012", "test",
                TriggerDefinition.Action.SUBSCRIBE_TO_SERVICE, false,
                "http://", new Owner("42", "fujitsu"));
        TriggerDefinition td2 = new TriggerDefinition(
                "12345678-1234-5678-9012-123456789012", "test2",
                TriggerDefinition.Action.MODIFY_SUBSCRIPTION, false, "http://",
                new Owner("42", "fujitsu"));

        List<TriggerDefinition> tdl = new ArrayList<TriggerDefinition>();
        tdl.add(td1);
        tdl.add(td2);

        return tdl;
    }

    @Override
    public String postItem(RequestParametersPost params,
            TriggerDefinition content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public String postCollection(RequestParametersPost params,
            TriggerDefinition item) throws WebApplicationException {

        String id = item.getId() + "," + item.getName() + ","
                + item.getAction();

        return id;
    }

    @Override
    public void putItem(RequestParametersPut params, TriggerDefinition item)
            throws WebApplicationException {
        // no test action
    }

    @Override
    public void putCollection(RequestParametersPut params,
            TriggerDefinition content) throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

    @Override
    public void deleteItem(RequestParametersDelete params)
            throws WebApplicationException {
        // no test action
    }

    @Override
    public void deleteCollection(RequestParametersDelete params)
            throws WebApplicationException {
        throw WebException.notFound().build(); // TODO add more info
    }

}
