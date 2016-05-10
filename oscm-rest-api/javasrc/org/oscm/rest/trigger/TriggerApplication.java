/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 2, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.oscm.rest.common.GsonMessageProvider;
import org.oscm.rest.common.SecurityFilter;
import org.oscm.rest.common.VersionFilter;

import com.sun.jersey.api.container.filter.RolesAllowedResourceFilterFactory;

/**
 * Registers resources and providers of the trigger component to the
 * application.
 * 
 * @author miethaner
 */
@ApplicationPath("/{version}/trigger")
public class TriggerApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resource = new HashSet<Class<?>>();

        // Resources
        resource.add(RestTriggerDefinition.class);
        resource.add(RestTriggerProcess.class);
        resource.add(RestTriggerProcessIdentifier.class);
        resource.add(RestTriggerProcessParameter.class);

        // Filter & Mapper
        resource.add(VersionFilter.class);
        resource.add(SecurityFilter.class);
        resource.add(GsonMessageProvider.class);
        resource.add(RolesAllowedResourceFilterFactory.class);

        return resource;
    }
}
