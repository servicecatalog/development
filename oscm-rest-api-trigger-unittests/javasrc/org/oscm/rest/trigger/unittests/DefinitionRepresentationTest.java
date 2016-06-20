/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.oscm.rest.trigger.data.DefinitionRepresentation;

/**
 * Unit test for TriggerRepresentation
 * 
 * @author miethaner
 */
public class DefinitionRepresentationTest {

    @Test
    public void testValidationPositive() throws Exception {

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setDescription("abc");
        trigger.setTargetURL("http://abc.de/asdf");
        trigger.setAction("SUBSCRIBE_TO_SERVICE");

        trigger.validateContent();
    }

    @Test
    public void testValidationNegative() throws Exception {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            sb.append("1234567890");
        }

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setDescription(sb.toString());

        try {
            trigger.validateContent();
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.BAD_REQUEST.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        trigger = new DefinitionRepresentation();
        trigger.setTargetURL("<http://");

        try {
            trigger.validateContent();
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.BAD_REQUEST.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        trigger = new DefinitionRepresentation();
        trigger.setAction("SUB");

        try {
            trigger.validateContent();
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.BAD_REQUEST.getStatusCode(), e.getResponse()
                    .getStatus());
        }
    }

}
