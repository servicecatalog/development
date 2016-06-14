/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 14, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.sun.jersey.api.core.ResourceConfig;

/**
 * Rest Resource Config for Mock endpoints
 * 
 * @author miethaner
 */
@ApplicationPath("/mock")
public class MockResourceConfig extends ResourceConfig {

    @Override
    public Set<Class<?>> getRootResourceClasses() {
        Set<Class<?>> resource = new HashSet<Class<?>>();

        resource.add(RestMockResource.class);

        return resource;
    }

    @Override
    public Set<Class<?>> getProviderClasses() {
        return new HashSet<Class<?>>();
    }

    @Override
    public boolean getFeature(String arg0) {
        return false;
    }

    @Override
    public Map<String, Boolean> getFeatures() {
        return new HashMap<String, Boolean>();
    }

    @Override
    public Map<String, Object> getProperties() {
        return new HashMap<String, Object>();
    }

    @Override
    public Object getProperty(String arg0) {
        return null;
    }

}
