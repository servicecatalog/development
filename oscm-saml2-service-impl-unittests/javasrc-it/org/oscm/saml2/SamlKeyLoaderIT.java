/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 24.06.16 11:17
 *
 ******************************************************************************/
package org.oscm.saml2;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.junit.Before;
import org.junit.Test;

import org.oscm.internal.types.exception.SaaSApplicationException;

/**
 * Created by PLGrubskiM on 2016-06-24.
 */
public class SamlKeyLoaderIT {
    private SamlKeyLoader samlKeyLoader;

    private static final String KEYSTORE_PASSWORD = "changeit";
    private static final String ALIAS = "s1as";

    @Before
    public void setUp() {
        samlKeyLoader = new SamlKeyLoader();
    }

    @Test
    public void loadPrivateKeyFromStore_OK() throws CertificateException,
            SaaSApplicationException, UnrecoverableKeyException,
            NoSuchAlgorithmException, IOException, KeyStoreException {
        // when
        samlKeyLoader.loadPrivateKeyFromStore(getKeystoreFilePath(),
                KEYSTORE_PASSWORD, ALIAS);
        // then
    }

    @Test(expected = IOException.class)
    public void loadPrivateKeyFromStore_invalidFile()
            throws CertificateException, SaaSApplicationException,
            UnrecoverableKeyException, NoSuchAlgorithmException, IOException,
            KeyStoreException {
        // given
        String path = "somePath";
        // when
        samlKeyLoader.loadPrivateKeyFromStore(path, KEYSTORE_PASSWORD, ALIAS);
    }

    @Test(expected = IOException.class)
    public void loadPrivateKeyFromStore_invalidPassword()
            throws CertificateException, SaaSApplicationException,
            UnrecoverableKeyException, NoSuchAlgorithmException, IOException,
            KeyStoreException {
        // given
        String password = "invalidPassword";
        // when
        samlKeyLoader.loadPrivateKeyFromStore(getKeystoreFilePath(), password,
                ALIAS);
        // then
    }

    @Test(expected = SaaSApplicationException.class)
    public void loadPrivateKeyFromStore_invalidAlias()
            throws CertificateException, SaaSApplicationException,
            UnrecoverableKeyException, NoSuchAlgorithmException, IOException,
            KeyStoreException {
        // when
        samlKeyLoader.loadPrivateKeyFromStore(getKeystoreFilePath(),
                KEYSTORE_PASSWORD, "invalidAlias");
    }

    public String getKeystoreFilePath() {
        return getClass().getClassLoader().getResource("").getPath()
                + "/../javares/keystore.jks";
    }

}
