/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 23.06.16 13:57
 *
 ******************************************************************************/

package org.oscm.saml2;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import org.oscm.internal.types.exception.SaaSApplicationException;

/**
 * Created by PLGrubskiM on 2016-06-23.
 */
public class SamlKeyLoader {

    public static PrivateKey loadPrivateKey(String path)
            throws GeneralSecurityException, IOException {
        byte[] clear = loadPrivateKeyFromFile(path);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    private static byte[] loadPrivateKeyFromFile(String privateKeyPath)
            throws IOException {
        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(privateKeyPath));
        return load(in);
    }

    private static byte[] load(InputStream inputStream) throws IOException {
        try {
            byte[] bytes = new byte[inputStream.available()];
            int len = inputStream.read(bytes);
            if (len > -1) {
                return bytes;
            }
            return new byte[0];
        } finally {
            inputStream.close();
        }
    }

    public static Key loadKeyStore(String filePath, String password,
            String alias) throws SaaSApplicationException, IOException,
                    KeyStoreException, CertificateException,
                    NoSuchAlgorithmException, UnrecoverableKeyException {
        FileInputStream is = new FileInputStream(filePath);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] passwd = password.toCharArray();
        keystore.load(is, passwd);
        Key key = keystore.getKey(alias, passwd);
        if (key instanceof PrivateKey) {
            return key;

        }
        throw new SaaSApplicationException(
                "Private key with alias " + alias + " cannot be found");
    }

}
