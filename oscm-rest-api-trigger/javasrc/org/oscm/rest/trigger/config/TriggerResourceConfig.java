/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: May 2, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.oscm.rest.common.GsonMessageProvider;
import org.oscm.rest.common.SecurityFilter;
import org.oscm.rest.common.VersionFilter;
import org.oscm.rest.trigger.RestTriggerResource;

/**
 * Registers resources and providers of the trigger component to the
 * application.
 * 
 * @author miethaner
 */
@ApplicationPath(TriggerCommonParams.PATH_TRIGGER)
public class TriggerResourceConfig extends ResourceConfig {

    public TriggerResourceConfig() {

        register(RestTriggerResource.class);
        register(GsonMessageProvider.class);
        register(VersionFilter.class);
        register(SecurityFilter.class);
        register(RolesAllowedDynamicFeature.class);
    }
}
