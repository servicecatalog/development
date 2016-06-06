/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.sp;

import org.oscm.saml2.tools.KeyLoader;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class MockKeyProvider {

    public static PrivateKey getPrivateKey() {
        return KeyLoader.getPrivateKey(MockKeyProvider.class, "cakey.der",
                KeyLoader.Algorithm.RSA);
    }

    public static PublicKey getPublicKey() {
        return KeyLoader.getPublicKey(MockKeyProvider.class, "pub-key.der",
                KeyLoader.Algorithm.RSA);
    }

    public static X509Certificate getPublicCertificate() {
        return KeyLoader.getPublicCertificate(MockKeyProvider.class,
                "cacert.der");
    }

}
