/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Aug 16, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.oscm.rest.common.GsonMessageProvider;
import org.oscm.rest.trigger.RestTriggerResource;
import org.oscm.rest.trigger.config.TriggerResourceConfig;

/**
 * @author miethaner
 *
 */
public class TriggerResourceConfigTest {

    @Test
    public void testFields() {

        TriggerResourceConfig config = new TriggerResourceConfig();

        assertTrue(config.getRootResourceClasses().contains(
                RestTriggerResource.class));
        assertTrue(config.getProviderClasses().contains(
                GsonMessageProvider.class));
        assertFalse(config.getFeature(""));
        assertTrue(config.getFeatures().isEmpty());
        assertNotNull(config
                .getProperty(TriggerResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES));
        assertFalse(config.getProperties().isEmpty());
    }

}
