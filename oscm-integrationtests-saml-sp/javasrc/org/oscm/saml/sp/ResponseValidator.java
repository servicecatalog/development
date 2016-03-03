/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.sp;

import java.security.PublicKey;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.oscm.saml.api.ResponseParser;
import org.oscm.saml.api.SamlSigner;

/**
 * Provides methods for validating SAML responses.
 * 
 * @author barzu
 */
public class ResponseValidator {

    public static boolean hasValidSignature(String samlResponse)
            throws MarshalException, XMLSignatureException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException {

        return hasValidSignature(samlResponse, MockKeyProvider.getPublicKey());
    }

    public static boolean hasValidSignature(String samlResponse,
            PublicKey publicKey) throws MarshalException,
            XMLSignatureException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {

        ResponseParser parser = new ResponseParser(samlResponse);
        Element responseElement = parser.getResponseElement();
        setAssertionIdAttribute(responseElement);
        NodeList signatures = responseElement.getElementsByTagNameNS(
                XMLSignature.XMLNS, "Signature");

        if (signatures.getLength() < 1) {
            throw new IllegalStateException(
                    "SAML response contains no signature");
        }
        if (signatures.getLength() > 1) {
            throw new IllegalStateException("SAML response contains multiple ("
                    + signatures.getLength() + ") signatures");
        }

        DOMValidateContext valContext = new DOMValidateContext(publicKey,
                signatures.item(0));

        XMLSignatureFactory newxFac = SamlSigner.createSignatureFactory();

        XMLSignature signature = newxFac.unmarshalXMLSignature(valContext);

        return signature.validate(valContext);
    }

    private static void setAssertionIdAttribute(Element responseElement) {
        NodeList assertions = responseElement.getElementsByTagNameNS(
                "urn:oasis:names:tc:SAML:1.0:assertion", "Assertion");
        Element assertion = (Element) assertions.item(0);
        assertion.setIdAttribute("AssertionID", true);
    }
}
