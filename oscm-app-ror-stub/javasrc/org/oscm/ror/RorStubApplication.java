/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 29.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.oscm.ror.api.EndpointResource;

/**
 * @author kulle
 * 
 */
@ApplicationPath("/cfmgapi")
public class RorStubApplication extends Application {

    /**
     * Registers JAX-RS root resources
     */
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(EndpointResource.class);
        return classes;
    }

}
