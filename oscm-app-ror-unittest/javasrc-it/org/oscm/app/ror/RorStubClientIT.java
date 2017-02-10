/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: 27.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import org.oscm.test.setup.PropertiesReader;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.data.LPlatformDescriptorConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author kulle
 * 
 */
@Ignore
public class RorStubClientIT extends RorClientIT {

    public RorStubClientIT() throws Exception {
        PropertiesReader reader = new PropertiesReader();
        Properties properties = reader.load();

        IAAS_API_URI = properties.getProperty("iaas.api.uri");
    }

    @Test
    public void testRsCall() {
        Client client = Client.create();
        WebResource webResource = client.resource(IAAS_API_URI
                + "?Action=ListLPlatform");
        String result = webResource.get(String.class);
        System.out.println(result);
    }

    @Test
    public void createLPlatform() throws Exception {
        String platformId = vdcClient.createLPlatform("instanceName",
                "descriptorId");

        assertNotNull(platformId);
        assertFalse(platformId.isEmpty());
    }

    @Test
    public void listLPlatforms() throws Exception {
        List<LPlatformConfiguration> configuration = vdcClient
                .listLPlatforms(true);
        assertNotNull(configuration);
    }

    @Test
    public void getLPlatformDescriptorConfiguration() throws Exception {
        LPlatformDescriptorConfiguration configuration = vdcClient
                .getLPlatformDescriptorConfiguration("descriptorId");
        assertNotNull(configuration);
    }

    @Test
    public void getLPlatformDescriptorConfiguration_unknown() throws Exception {
        LPlatformDescriptorConfiguration configuration = vdcClient
                .getLPlatformDescriptorConfiguration("descriptorId");
        assertNotNull(configuration);
    }

}
