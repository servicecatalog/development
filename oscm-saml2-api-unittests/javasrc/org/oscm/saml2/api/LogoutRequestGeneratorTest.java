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

/**
 * Added by @chojnackid
 */
public class LogoutRequestGeneratorTest {

    LogoutRequestGenerator classUnderStress;

    @Before
    public void setup() {
        classUnderStress = new LogoutRequestGenerator();
    }

    @Test
    public void generateLogoutRequestTest() {
        String URL = classUnderStress.generateLogoutRequest("");
        Assert.assertNotNull(URL);
    }
}
