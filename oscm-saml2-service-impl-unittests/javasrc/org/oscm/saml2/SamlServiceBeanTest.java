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
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.types.exception.UnsupportedOperationException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Authored by dawidch
 */
public class SamlServiceBeanTest {

    private SamlServiceBean classUnderStress;

    @Before
    public void setUp() throws Exception {
        classUnderStress = spy(new SamlServiceBean());

    }

    @Test
    public void generateLogoutRequestTest() throws SaaSApplicationException {
        doReturn("1").when(classUnderStress).getIssuer();
        doReturn("2").when(classUnderStress).getKeyAlias();
        doReturn("3").when(classUnderStress).getKeystorePass();
        doReturn("4").when(classUnderStress).getLogoutURL();
        doReturn("").when(classUnderStress).getKeystorePath();


        String result = classUnderStress.generateLogoutRequest("", "");

        assertTrue(result.length() != 0);
    }

    @Test(expected = SaaSApplicationException.class)
    public void generateLogoutRequestWithoutKeystoreTest() throws SaaSApplicationException {
        doReturn("").when(classUnderStress).getIssuer();
        doReturn("").when(classUnderStress).getKeyAlias();
        doReturn("").when(classUnderStress).getKeystorePass();
        doReturn("").when(classUnderStress).getLogoutURL();
        doReturn("aaa").when(classUnderStress).getKeystorePath();
        classUnderStress.generateLogoutRequest("", "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createSamlResponse() throws Exception {
        classUnderStress.createSamlResponse("");
    }
}
