/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: Mar 5, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.oscm.app.ror.exceptions.RORException;

/**
 * @author zhaohang
 * 
 */
public class RORExceptionTest {

    private RORException exception;

    @Test
    public void RORException_MessageContainsCode67210() throws Exception {
        // when
        String message = "message_[code:67210]";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(exception.isBusyMessage()));
    }

    @Test
    public void RORException_MessageContainsVSYS10124() throws Exception {
        // when
        String message = "VSYS10124 message";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(exception.isBusyMessage()));
    }

    @Test
    public void RORException_MessageContainsVSYS10120() throws Exception {
        // when
        String message = "VSYS10120message";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.TRUE,
                Boolean.valueOf(exception.isStateAlreadyPresent()));
    }

    @Test
    public void RORException_MessageContainsVSYS10121() throws Exception {
        // when
        String message = "VSYS10121message";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.TRUE,
                Boolean.valueOf(exception.isStateAlreadyPresent()));
    }

    @Test
    public void RORException_MessageContainsVSYS10122() throws Exception {
        // when
        String message = "VSYS10122message";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.TRUE,
                Boolean.valueOf(exception.isStateAlreadyPresent()));
    }

    @Test
    public void RORException_MessageContainsVSYS10123() throws Exception {
        // when
        String message = "VSYS10123message";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.TRUE,
                Boolean.valueOf(exception.isStateAlreadyPresent()));
    }

    @Test
    public void RORException_MessageIsNull() throws Exception {
        // when
        exception = new RORException(null);

        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(exception.isBusyMessage()));
        assertEquals(Boolean.FALSE,
                Boolean.valueOf(exception.isStateAlreadyPresent()));
    }

    @Test
    public void RORException_MessageContainsNoSignals() throws Exception {
        // when
        String message = "message";
        exception = new RORException(message);

        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(exception.isBusyMessage()));
        assertEquals(Boolean.FALSE,
                Boolean.valueOf(exception.isStateAlreadyPresent()));
    }
}
