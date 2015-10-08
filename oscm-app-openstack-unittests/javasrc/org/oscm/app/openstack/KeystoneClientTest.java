/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                        
 *                                                                              
 *  Creation Date: Dec 2, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.v1_0.exceptions.APPlatformException;

public class KeystoneClientTest {
    private final MockURLStreamHandler streamHandler = new MockURLStreamHandler();

    @Before
    public void setUp() throws Exception {
        OpenStackConnection.setURLStreamHandler(streamHandler);
        HeatProcessor.setURLStreamHandler(streamHandler);
    }

    @Test(expected = HeatException.class)
    public void authenticate_malFormedURL() throws HeatException,
            APPlatformException {
        // given

        // when
        new KeystoneClient(new OpenStackConnection("xyz")).authenticate("user",
                "password", "tenantName");
    }

    @Test
    public void authenticate() throws HeatException, APPlatformException {
        // given

        // when
        new KeystoneClient(new OpenStackConnection("http://xyz.de"))
                .authenticate("user", "password", "tenantName");
    }

    @Test
    public void authenticate_http_666() throws HeatException,
            APPlatformException {
        // given
        int status = 666;
        streamHandler.put("/tokens", new MockHttpURLConnection(status, ""));

        try {
            // when
            new KeystoneClient(new OpenStackConnection("http://xyz.de"))
                    .authenticate("user", "password", "tenantName");
            assertTrue("Test must fail at this point", false);
        } catch (RuntimeException ex) {
            // then
            assertTrue("Wrong exception message!",
                    ex.getMessage().indexOf("" + status) > -1);
        }
    }

    @Test(expected = RuntimeException.class)
    public void authenticate_noJSONResponse() throws HeatException,
            APPlatformException {
        // given
        streamHandler.put("/tokens", new MockHttpURLConnection(200, ""));

        // when
        new KeystoneClient(new OpenStackConnection("http://xyz.de"))
                .authenticate("user", "password", "tenantName");
    }

    @Test(expected = APPlatformException.class)
    public void authenticate_noHeatEndpoint() throws HeatException,
            APPlatformException {
        // given
        streamHandler.put("/tokens", new MockHttpURLConnection(200,
                MockURLStreamHandler.respTokens(false, true)));

        // when
        new KeystoneClient(new OpenStackConnection("http://xyz.de"))
                .authenticate("user", "password", "tenantName");
    }

    @Test(expected = APPlatformException.class)
    public void authenticate_noNovaEndpoint() throws HeatException,
            APPlatformException {
        // given
        streamHandler.put("/tokens", new MockHttpURLConnection(200,
                MockURLStreamHandler.respTokens(true, false)));

        // when
        new KeystoneClient(new OpenStackConnection("http://xyz.de"))
                .authenticate("user", "password", "tenantName");
    }
}
