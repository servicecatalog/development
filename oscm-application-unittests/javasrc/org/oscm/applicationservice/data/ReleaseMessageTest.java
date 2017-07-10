/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 10.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.applicationservice.data;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author stavreva
 *
 */
public class ReleaseMessageTest {

    private final String ID = "8fd1c107-4f72-4196-96e2-b6c4373b8108";
    private final String ETAG = "c48b24b7-66fb-4bbe-bbbe-d42718dce254";
    private final String OP = ReleaseMessage.Operation.UPD.name();
    private final String STATUS = ReleaseMessage.Status.PENDING.name();
    private final String INSTANCE = "8fd1c107-4f72-4196-96e2-b6c4373b8108";
    private final String ENDPOINT_KEY = "endpoint";
    private final String ENDPOINT_VALUE = "127.0.0.1:5000";

    @Test
    public void fromJson() {

        //given
        String json = "{\"version\": 100000, \"id\": \"" + ID
                + "\", \"etag\": \"" + ETAG + "\", \"operation\": \"" + OP
                + "\", \"status\": \"" + STATUS + "\", \"instance\": \""
                + INSTANCE + "\", \"services\": {\""+ENDPOINT_KEY+"\": \"" + ENDPOINT_VALUE
                + "\"} }";
        
        //when
        ReleaseMessage release = ReleaseMessage.fromJson(json);

        //then
        assertEquals(ID, release.getId().toString());
        assertEquals(ETAG, release.getEtag().toString());
        assertEquals(OP, release.getOperation().name());
        assertEquals(STATUS, release.getStatus().name());
        assertEquals(INSTANCE, release.getInstance().toString());
        assertEquals(ENDPOINT_VALUE, release.getServices().get(ENDPOINT_KEY));
    }
}
