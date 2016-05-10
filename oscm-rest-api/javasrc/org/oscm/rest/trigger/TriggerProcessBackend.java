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
import org.oscm.rest.common.RequestParametersDelete;
import org.oscm.rest.common.RequestParametersGet;
import org.oscm.rest.common.RequestParametersPost;
import org.oscm.rest.common.RequestParametersPut;

/**
 * Backend class for the trigger process resource.
 * 
 * @author miethaner
 */
public class TriggerProcessBackend implements EndpointBackend<TriggerProcess> {

    @Override
    public TriggerProcess getItem(RequestParametersGet params)
            throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<TriggerProcess> getCollection(RequestParametersGet params)
            throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String postItem(RequestParametersPost params, TriggerProcess content)
            throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String postCollection(RequestParametersPost params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putItem(RequestParametersPut params, TriggerProcess content)
            throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void putCollection(RequestParametersPut params,
            TriggerProcess content) throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteItem(RequestParametersDelete params)
            throws WebApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteCollection(RequestParametersDelete params)
            throws WebApplicationException {
        // TODO Auto-generated method stub

    }

}
