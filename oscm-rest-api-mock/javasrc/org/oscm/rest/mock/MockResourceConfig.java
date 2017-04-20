/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: Jun 14, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.mock;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Rest Resource Config for Mock endpoints
 * 
 * @author miethaner
 */
@ApplicationPath("/mock")
public class MockResourceConfig extends ResourceConfig {

    public MockResourceConfig() {
        register(RestMockResource.class);
    }
}
