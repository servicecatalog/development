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

import org.junit.Test;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.trigger.data.ProcessRepresentation;

/**
 * Unit test for ProcessRepresentation
 * 
 * @author miethaner
 */
public class ProcessRepresentationTest {

    @Test
    public void testValidationPositive() throws Exception {

        ProcessRepresentation process = new ProcessRepresentation();
        process.setComment("abc");

        process.validateContent();
    }

    @Test
    public void testValidationNegative() throws Exception {

        ProcessRepresentation process = new ProcessRepresentation();
        process.setComment(">abc");

        try {
            process.validateContent();
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_BAD_REQUEST, e.getResponse()
                    .getStatus());
        }
    }
}
