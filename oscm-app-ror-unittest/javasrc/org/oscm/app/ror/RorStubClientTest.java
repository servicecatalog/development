/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: 27.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import org.oscm.test.setup.PropertiesReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author kulle
 * 
 */
@Ignore
public class RorStubClientTest extends RorClientManualTest {

    public RorStubClientTest() throws Exception {
        PropertiesReader reader = new PropertiesReader();
        Properties properties = reader.load();

        IAAS_API_URI = properties.getProperty("iaas.api.uri");
    }

    @Test
    public void testRsCall() {
        Client client = Client.create();
        WebResource webResource = client.resource(IAAS_API_URI);
        String result = webResource.get(String.class);
        System.out.println(result);
    }

}
