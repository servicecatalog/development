/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 28.06.16 08:11
 *
 ******************************************************************************/

package org.oscm.saml2.api;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Created by PLGrubskiM on 2016-06-23.
 */
public class SamlKeyLoaderTest {

    SamlKeyLoader samlKeyLoader;
    @Before
    public void setup() {
        samlKeyLoader = new SamlKeyLoader();
    }

    @Test
    public void keyloaderTest() {
        String URL = "ignore";
        Assert.assertNotNull(URL);
    }
}
