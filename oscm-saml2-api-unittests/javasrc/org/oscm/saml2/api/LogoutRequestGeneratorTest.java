/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 03.06.2013
 *
 *******************************************************************************/

package org.oscm.saml2.api;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.oscm.internal.intf.SamlService;
import org.oscm.internal.types.exception.SaaSApplicationException;

import static org.mockito.Mockito.*;

/**
 * Added by @chojnackid
 */
@RunWith(MockitoJUnitRunner.class)
public class LogoutRequestGeneratorTest {

    LogoutRequestGenerator classUnderStress;

    @Mock
    private SamlService mock;

    @Before
    public void setup() throws SaaSApplicationException {
        doReturn("").when(mock).generateLogoutRequest("", "");
        classUnderStress = new LogoutRequestGenerator();
        classUnderStress.setSamlService(mock);
    }

    @Test
    public void generateLogoutRequestTest() throws SaaSApplicationException {
        String URL = classUnderStress.generateLogoutRequest("", "");
        Assert.assertNotNull(URL);
        verify(mock, times(1)).generateLogoutRequest("", "");
    }
}
