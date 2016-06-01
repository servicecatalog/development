/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml.api;

import java.security.PrivateKey;

import org.w3c.dom.Element;
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
