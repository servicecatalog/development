/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml.api;

import java.security.AccessControlException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * Provides digital signing of SAML elements.<br>
 * As SAML 2.0 supports only signing of <code>< Request ></code>,
 * <code>< Response ></code> or <code>< Assertion ></code> or <code>< LogoutRequest ></code> elements, so does
 * this class.
 * 
 * @author barzu
 */
public class SamlSigner extends Saml20Signer {

    private static final String SAML_PROTOCOL_NS_URI_V11 = "urn:oasis:names:tc:SAML:1.0:protocol";

    public SamlSigner(PrivateKey privateKey) {
        super(privateKey);
    }

    /**
     * Determines the node to insert the XML < Signature > element before. The
     * location is enforced by the oasis-sstc-saml-schema-protocol-1.1.xsd
     * 
     * @see SAML 1.1 Core 3.4.1, 2.3.2
     */
    protected Node getNextSibling(Element element) {
        Node insertLocation = null;
        NodeList nodeList = element.getElementsByTagNameNS(
                SAML_PROTOCOL_NS_URI_V11, "Extensions");
        if (nodeList.getLength() <= 0) {
            nodeList = element.getElementsByTagNameNS(SAML_PROTOCOL_NS_URI_V11,
                    Status.STATUS);
        }
        if (nodeList.getLength() <= 0) {
            return null;
        }
        insertLocation = nodeList.item(nodeList.getLength() - 1);
        return insertLocation;
    }

}
