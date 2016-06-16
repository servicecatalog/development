/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml2.tools;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
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

import org.oscm.internal.types.exception.DigitalSignatureValidationException;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.saml2.api.DigitalSignatureValidator;
import org.oscm.security.Keystores;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides digital signing of SAML elements.<br>
 * As SAML 2.0 supports only signing of <code>< Request ></code>,
 * <code>< Response ></code> or <code>< Assertion ></code> or <code>< LogoutRequest ></code> elements, so does
 * this class.
 * 
 * @author barzu
 */
public class Saml20Signer {

    public static final String RESPONSE = "Response";
    public static final String RESPONSE_ID = "ResponseID";
    public static final String ASSERTION = "Assertion";
    public static final String ASSERTION_ID = "AssertionID";
    public static final String STATUS = "Status";

    private static final String EXCEPTION_PREFIX = "Unable to sign SAML XML element: ";

    private static final String JSR_105_PROVIDER = "org.jcp.xml.dsig.internal.dom.XMLDSigRI";
    private static final String SAML_PROTOCOL_NS_URI_V20 = "urn:oasis:names:tc:SAML:2.0:protocol";
//TODO add logger

//    private static final Log4jLogger logger = LoggerFactory
//            .getLogger(Saml20Signer.class);

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X509Certificate publicCertificate;

    private XMLSignatureFactory signatureFactory;

    public Saml20Signer(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new NullPointerException(
                    "assertion failed: The private key must not be null");
        }
        this.privateKey = privateKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPublicCertificate(X509Certificate publicCertificate) {
        this.publicCertificate = publicCertificate;
    }

    /**
     * @return the factory to sign the enveloped signature
     */
    private XMLSignatureFactory getSignatureFactory() {
        synchronized (this) {
            if (signatureFactory == null) {
                try {
                    signatureFactory = createSignatureFactory();
                } catch (InstantiationException e) {
                    throw createSaaSSystemException(e);
                } catch (IllegalAccessException e) {
                    throw createSaaSSystemException(e);
                } catch (ClassNotFoundException e) {
                    throw createSaaSSystemException(e);
                }
            }
        }
        return signatureFactory;
    }

    public static XMLSignatureFactory createSignatureFactory()
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        String providerName = System.getProperty("jsr105Provider",
                JSR_105_PROVIDER);
        return XMLSignatureFactory.getInstance("DOM",
                (Provider) Class.forName(providerName).newInstance());
    }

    /**
     * Digitally signs the specified <code>Element</code> with the private key.
     *
     * @return the signed DOM Element
     * @see SAML 2.0 Core 5.2, 3.2, 3.4
     */
    public Element signSamlElement(Element element, Element parent) {

        try {
            // create a signing context for the element
            DOMSignContext context = new DOMSignContext(privateKey, element);
            if (parent != null) {
                context.setParent(parent);
            } else {
                Node nextSibling = getNextSibling(element);
                if (nextSibling != null) {
                    context.setNextSibling(nextSibling);
                }
            }
            context.setDefaultNamespacePrefix("ds");

            // create the <Signature>
            XMLSignature signature = getSignatureFactory().newXMLSignature(
                    getSignedInfo(element), getKeyInfoAsX509Data());

            // sign the element using the previously create context
            signature.sign(context);
//            verifySig(element);
            return element;

        } catch (AccessControlException e) {
            throw createSaaSSystemException(e);
        } catch (XMLSignatureException e) {
            throw createSaaSSystemException(e);
        } catch (MarshalException e) {
            throw createSaaSSystemException(e);
//        } catch (IOException e) {
//            throw createSaaSSystemException(e);
//        } catch (CertificateException e) {
//            throw createSaaSSystemException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw createSaaSSystemException(e);
//        } catch (DigitalSignatureValidationException e) {
//            throw createSaaSSystemException(e);
//        } catch (KeyStoreException e) {
//            throw createSaaSSystemException(e);
        }
    }
//todo remove it - temporary for signature validation
//    private boolean verifySig(Element element) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, DigitalSignatureValidationException {
//        String filePath = "C:/bin/glassfish3/glassfish/domains/integration-bes/config/keystore.jks";
//        KeyStore keystore = Keystores.initializeKeyStore(filePath,
//                "changeit");
//        DigitalSignatureValidator validator = new DigitalSignatureValidator(keystore);
//        final boolean validate = validator.validate(findChild(element.getChildNodes(), "Signature"));
//        return validate;
//    }

//    private Node findChild(NodeList childrenNodes, String expectedChildName) {
//        for (int j = 0; j < childrenNodes.getLength(); j++) {
//            final Node childNode = childrenNodes.item(j);
//            if (expectedChildName.equals(childNode
//                    .getLocalName())) {
//                return childNode;
//            }
//        }
//        return null;
//    }


    /**
     * Determines the node to insert the XML < Signature > element before. The
     * location is enforced by the oasis-sstc-saml-schema-protocol-2.0.xsd
     * 
     * @see SAML 2.0 Core 3.4.1, 2.3.2
     */
    protected Node getNextSibling(Element element) {
        Node insertLocation;
        NodeList nodeList = element.getElementsByTagNameNS(
                SAML_PROTOCOL_NS_URI_V20, "Extensions");
        if (nodeList.getLength() <= 0) {
            // signing a SAML <Response>
            nodeList = element.getElementsByTagNameNS(SAML_PROTOCOL_NS_URI_V20,
                    STATUS);
        }
        if (nodeList.getLength() <= 0) {
            return null;
        }
        insertLocation = nodeList.item(nodeList.getLength() - 1);
        return insertLocation;
    }

    /**
     * Creates a <code>< SignedInfo ></code> element containing:
     * <ul>
     * <li>a <code>< CanonicalizationMethod ></code> element specifying the
     * <code>Exclusive Canonicalization [Excl-C14N]</code> algorithm (see SAML
     * 2.0 Core 5.4.3)
     * <li>a <code>< SignatureMethod ></code> element specifying the
     * <code>RSA-SHA1</code> algorithm (see SAML 2.0 Core 5.4.1)</li>
     * <li>a <code>< Reference ></code> element as described by
     * {@link #getReference(Element)}</li>
     * </ul>
     */
    private SignedInfo getSignedInfo(Element element) {
        try {
            // create the <Reference>
            Reference reference = getReference(element);

            // create the <SignatureMethod> depending on the key type
            // see SAML 2.0 Core 5.4.1
            SignatureMethod signatureMethod;

            if (privateKey instanceof RSAPrivateKey) {
                signatureMethod = getSignatureFactory().newSignatureMethod(
                        SignatureMethod.RSA_SHA1, null);
            } else if (privateKey instanceof DSAPrivateKey) {
                signatureMethod = getSignatureFactory().newSignatureMethod(
                        SignatureMethod.DSA_SHA1, null);
            } else {
                throw new SaaSSystemException(EXCEPTION_PREFIX
                        + "Unsupported key type");
            }

            // create the <CanonicalizationMethod>
            CanonicalizationMethod canonicalizationMethod = getSignatureFactory()
                    .newCanonicalizationMethod(
                            CanonicalizationMethod.EXCLUSIVE,
                            (C14NMethodParameterSpec) null);

            // create the <SignedInfo>
            return getSignatureFactory().newSignedInfo(canonicalizationMethod,
                    signatureMethod, Collections.singletonList(reference));

        } catch (NoSuchAlgorithmException e) {
            throw createSaaSSystemException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw createSaaSSystemException(e);
        }
    }

    /**
     * Creates a <code>< Reference ></code> element containing:
     * <ul>
     * <li>the attribute <code>URI</code> =
     * <code>"#[ID of the element to be signed]"</code></li>
     * <li>a <code>< Transform ></code> element specifying the
     * <code>CanonicalizationMethod.EXCLUSIVE</code> algorithm (According to
     * SAML 2.0 Core 5.4.3, 5.4.4 also Transform.ENVELOPED could be used, but
     * Cordys examples seem to prefer CanonicalizationMethod.EXCLUSIVE).
     * <li>a <code>< DigestMethod ></code> element specifying the
     * <code>SHA1</code> algorithm (see SAML 2.0 Core 5.4.1)</li>
     * </ul>
     */
    private Reference getReference(Element element) {
        try {
            // specify the ENVELOPED signature format
            // (it is a must according to SAML 2.0 Core 5.4.1)
            List<Transform> envelopedTransform = new ArrayList<>();
            envelopedTransform.add(getSignatureFactory().newTransform(
                    CanonicalizationMethod.ENVELOPED,
                    (TransformParameterSpec) null));
            envelopedTransform.add(getSignatureFactory().newTransform(
                    CanonicalizationMethod.EXCLUSIVE,
                    (TransformParameterSpec) null));
            // create the <Reference>
            return getSignatureFactory().newReference(
                    "#" + getReferenceId(element),
                    getSignatureFactory().newDigestMethod(DigestMethod.SHA1,
                            null), envelopedTransform, null, null);
        } catch (NoSuchAlgorithmException e) {
            throw createSaaSSystemException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw createSaaSSystemException(e);
        }
    }

    /**
     * Gets the value of the ID attribute of the specified element. SAML 2.0
     * supports only signing of <code>< Request ></code>,
     * <code>< Response ></code> or <code>< Assertion ></code> elements.
     *
     * @param element
     *            the element to be signed
     * @return the value of the ID attribute of the <code>element</code>
     * @see SAML 2.0 Core 5.4.2
     */
    private String getReferenceId(Element element) {
        String name = element.getLocalName();
        NamedNodeMap attributeMap = element.getAttributes();
        Node attribute;
        if (RESPONSE.equals(name)) {
            attribute = attributeMap.getNamedItem(RESPONSE_ID);
        } else if (ASSERTION.equals(name)) {
            attribute = attributeMap.getNamedItem(ASSERTION_ID);
        } else if ("Request".equals(name)) {
            attribute = attributeMap.getNamedItem("RequestID");
        } else if ("LogoutRequest".equals(name)){
            element.setIdAttribute("ID", true);
            attribute = attributeMap.getNamedItem("ID");
        } else {
            throw new SaaSSystemException(EXCEPTION_PREFIX
                    + "Unsupported element to be signed: <" + name + "> ");
        }
        if (attribute == null) {
            throw new SaaSSystemException(EXCEPTION_PREFIX
                    + "ID Attribute of element <" + name
                    + "> (to be signed) not found");
        }
        return attribute.getNodeValue();
    }

    /**
     * Creates a <code>< KeyInfo ></code> element which embeds the public key.
     * This element is not required by the SAML specification, but is allowed to
     * be provided. The service provider should get the public key from other
     * channels (for example Cordys requires the upload of a certificate on
     * enabling the SSO).
     *
     * @see SAML 2.0 Core 5.4.5
     */
    @SuppressWarnings("unused")
    private KeyInfo getKeyInfo() {
        if (publicKey == null) {
            return null;
        }
        try {
            KeyInfoFactory keyInfoFactory = getSignatureFactory()
                    .getKeyInfoFactory();
            KeyValue keyValuePair = keyInfoFactory.newKeyValue(publicKey);
            return keyInfoFactory.newKeyInfo(Collections
                    .singletonList(keyValuePair));
        } catch (KeyException e) {
            throw createSaaSSystemException(e);
        }
    }

    /**
     * Creates a <code>< KeyInfo ></code> element which embeds the public x509
     * certificate. This element is not required by the SAML specification, but
     * is allowed to be provided. The service provider should get the
     * certificate from other channels (for example Cordys requires the upload
     * of a certificate on enabling the SSO).
     *
     * @see SAML 2.0 Core 5.4.5
     */
    private KeyInfo getKeyInfoAsX509Data() {
        if (publicCertificate == null) {
            return null;
        }
        // create the KeyInfo using the public certificate
        KeyInfoFactory keyInfoFactory = getSignatureFactory()
                .getKeyInfoFactory();
        X509Data keyValuePair = keyInfoFactory.newX509Data(Collections
                .singletonList(publicCertificate));
        return keyInfoFactory.newKeyInfo(Collections
                .singletonList(keyValuePair));
    }

    private static SaaSSystemException createSaaSSystemException(Throwable t) {
        SaaSSystemException e = new SaaSSystemException(EXCEPTION_PREFIX
                + t.getMessage(), t);
//        logger.logError(Log4jLogger.SYSTEM_LOG, e,
//                LogMessageIdentifier.ERROR_PROVIDE_DIGITAL_SIGNING_OF_SAML);
        return e;
    }
}
