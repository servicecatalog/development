/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 23.06.16 13:57
 *
 ******************************************************************************/

package org.oscm.saml2;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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
