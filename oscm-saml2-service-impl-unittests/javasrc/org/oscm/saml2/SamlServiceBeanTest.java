/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 23.06.16 13:59
 *
 ******************************************************************************/

package org.oscm.saml2;

import org.junit.Before;
import org.junit.Test;
import org.oscm.internal.types.exception.UnsupportedOperationException;

/**
 * Authored by dawidch
 */
public class SamlServiceBeanTest {

    private SamlServiceBean classUnderStress;

    @Before
    public void setUp() throws Exception {
        classUnderStress = new SamlServiceBean();

    }

    @Test
    public void generateLogoutRequestTest() {
        classUnderStress.generateLogoutRequest("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createSamlResponse() throws Exception {
        classUnderStress.createSamlResponse("");
    }
}
