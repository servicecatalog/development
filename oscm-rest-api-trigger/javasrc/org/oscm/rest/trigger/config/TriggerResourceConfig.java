/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: May 2, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.config;

import java.util.Map;


/**
 * Registers resources and providers of the trigger component to the
 * application.
 * 
 * @author miethaner
 */
//@ApplicationPath(TriggerCommonParams.PATH_TRIGGER)
public class TriggerResourceConfig {// extends ResourceConfig {

    private Map<String, Object> properties;
    //TODO glassfish upgrade
//
//    public TriggerResourceConfig() {
//        properties = new HashMap<String, Object>();
//
//        properties.put(PROPERTY_RESOURCE_FILTER_FACTORIES, new String[] {
//                CommonFilterFactory.class.getName(),
//                RolesAllowedResourceFilterFactory.class.getName() });
//    }
//
//    @Override
//    public Set<Class<?>> getRootResourceClasses() {
//        Set<Class<?>> resource = new HashSet<Class<?>>();
//
//        resource.add(RestTriggerResource.class);
//
//        return resource;
//    }
//
//    @Override
//    public Set<Class<?>> getProviderClasses() {
//        Set<Class<?>> provider = new HashSet<Class<?>>();
//
//        provider.add(GsonMessageProvider.class);
//
//        return provider;
//    }
//
//    @Override
//    public boolean getFeature(String arg0) {
//        return false;
//    }
//
//    @Override
//    public Map<String, Boolean> getFeatures() {
//        return new HashMap<String, Boolean>();
//    }
//
//    @Override
//    public Object getProperty(String key) {
//        return properties.get(key);
//    }
//
//    @Override
//    public Map<String, Object> getProperties() {
//        return properties;
//    }

}
