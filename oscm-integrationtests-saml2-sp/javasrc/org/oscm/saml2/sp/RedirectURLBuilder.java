/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 23.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.sp;

import java.net.URL;
import java.net.URLEncoder;

import javax.xml.bind.JAXBElement;

import org.w3c.dom.Document;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.converter.XMLConverter;
import org.oscm.saml2.api.Marshalling;
import org.oscm.saml2.api.RedirectEncoder;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;

/**
 * @author roderus
 * 
 */
public class RedirectURLBuilder<T> {

    private static final Log4jLogger LOGGER = LoggerFactory
            .getLogger(RedirectURLBuilder.class);

    private URL redirectEndpoint = null;
    private JAXBElement<T> samlRequest = null;
    private String relayState = null;

    public RedirectURLBuilder<T> addRedirectEndpoint(URL redirectEndpoint) {
        this.redirectEndpoint = redirectEndpoint;
        return this;
    }

    public RedirectURLBuilder<T> addSamlRequest(JAXBElement<T> samlRequest) {
        this.samlRequest = samlRequest;
        return this;
    }

    public RedirectURLBuilder<T> addRelayState(String relayState) {
        this.relayState = relayState;
        return this;
    }

    public URL getURL() throws Exception {

        if (redirectEndpoint == null) {
            throw new SAML2AuthnRequestException(
                    "SAML redirect endpoint URL not set");
        }
        if (samlRequest == null) {
            throw new SAML2AuthnRequestException("SAML request not set");
        }

        String samlRequest = getSamlRequestAsString();

        RedirectEncoder coding = new RedirectEncoder();
        String samlRequest_encoded = coding
                .encodeForRedirectBinding(samlRequest);

        StringBuilder finalUrl = new StringBuilder(
                redirectEndpoint.toExternalForm());
        if (redirectEndpoint.getQuery() != null) {
            finalUrl.append("&");
        } else {
            finalUrl.append("?");
        }

        finalUrl.append("SAMLRequest=" + samlRequest_encoded);
        if (relayState != null) {
            finalUrl.append("&RelayState="
                    + URLEncoder.encode(relayState, "UTF-8"));
        }

        URL url = new URL(finalUrl.toString());
        return url;
    }

    private String getSamlRequestAsString() throws Exception {
        Marshalling<T> marshaller = new Marshalling<T>();
        Document samlRequestDoc = marshaller.marshallElement(samlRequest);
        return XMLConverter.convertToString(samlRequestDoc, false);
    }

    Log4jLogger getLogger() {
        return LOGGER;
    }
}
