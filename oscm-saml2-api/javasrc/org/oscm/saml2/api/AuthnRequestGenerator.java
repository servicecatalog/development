/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 03.06.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import java.util.Random;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;

import org.oscm.calendar.GregorianCalendars;
import org.oscm.converter.XMLConverter;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.SamlService;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.saml2.api.model.assertion.NameIDType;
import org.oscm.saml2.api.model.protocol.AuthnRequestType;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;
import org.oscm.saml2.api.model.protocol.NameIDPolicyType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author roderus
 * 
 */
public class AuthnRequestGenerator {

    private final static Integer HTTPS_INDEX = Integer.valueOf(0);
    private final static Integer HTTP_INDEX = Integer.valueOf(1);
    private final static String SAML_VERSION = "2.0";
    private ConfigurationService configService;

    private Random prng = new Random();
    private String issuer;
    private Boolean isHttps;
    private String requestId;
    private SamlService samlBean;

    public AuthnRequestGenerator(String issuer, Boolean isHttps) {
        this.issuer = issuer;
        this.isHttps = isHttps;
        this.requestId = generate160BitID();
    }

    public AuthnRequestGenerator(String issuer, Boolean isHttps,
            ConfigurationService configurationService, SamlService samlBean) {
        this(issuer, isHttps);
        this.configService = configurationService;
        this.samlBean = samlBean;
    }

    /**
     * @return BASE64-encoded SAML Authentication Request
     * @throws SAML2AuthnRequestException
     */
    public String getEncodedAuthnRequest() throws SAML2AuthnRequestException {
        String authnRequest = null;
        try {
            authnRequest = marshal(generateAuthnRequest());
        } catch (SAML2AuthnRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new SAML2AuthnRequestException(e.getMessage(),
                    SAML2AuthnRequestException.ReasonEnum.XML_TRANSFORMATION_ERROR);
        }
        return encodeBase64(authnRequest);
    }

    /**
     * @return BASE64-encoded SAML Authentication Request
     * @throws SAML2AuthnRequestException
     * @param idpSessionIndex
     */
    public String getEncodedLogoutRequest(String idpSessionIndex) throws SAML2AuthnRequestException {
        String authnRequest = null;
        try {
            authnRequest = marshal(generateLogoutRequest(idpSessionIndex));
        } catch (SAML2AuthnRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new SAML2AuthnRequestException(
                    e.getMessage(),
                    SAML2AuthnRequestException.ReasonEnum.XML_TRANSFORMATION_ERROR);
        }
        return encodeBase64(authnRequest);
    }

    public <T> String encode(String element) throws SAML2AuthnRequestException {
        try {
            return encodeBase64(element);
        } catch (Exception e) {
            throw new SAML2AuthnRequestException(e.getMessage(),
                    SAML2AuthnRequestException.ReasonEnum.XML_TRANSFORMATION_ERROR);
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public JAXBElement<AuthnRequestType> generateAuthnRequest()
            throws DatatypeConfigurationException {

        org.oscm.saml2.api.model.protocol.ObjectFactory protocolObjFactory;
        protocolObjFactory = new org.oscm.saml2.api.model.protocol.ObjectFactory();
        org.oscm.saml2.api.model.assertion.ObjectFactory assertionObjFactory;
        assertionObjFactory = new org.oscm.saml2.api.model.assertion.ObjectFactory();

        NameIDType issuer = assertionObjFactory.createNameIDType();
        issuer.setValue(this.issuer);

        AuthnRequestType authnRequest = protocolObjFactory
                .createAuthnRequestType();
        authnRequest.setID(requestId);
        authnRequest.setVersion(SAML_VERSION);
        final NameIDPolicyType nameIDPolicyType = protocolObjFactory.createNameIDPolicyType();
        nameIDPolicyType.setAllowCreate(true);
        authnRequest.setNameIDPolicy(nameIDPolicyType);
        authnRequest.setIssueInstant(
                GregorianCalendars.newXMLGregorianCalendarSystemTime());
        Integer acsIndex = isHttps.booleanValue() ? HTTPS_INDEX : HTTP_INDEX;
        authnRequest.setAssertionConsumerServiceIndex(acsIndex);
        authnRequest.setIssuer(issuer);

        JAXBElement<AuthnRequestType> authnRequestJAXB = protocolObjFactory
                .createAuthnRequest(authnRequest);

        return authnRequestJAXB;
    }

    public JAXBElement<LogoutRequestType> generateLogoutRequest(String idpSessionIndex) throws DatatypeConfigurationException {

        org.oscm.saml2.api.model.protocol.ObjectFactory protocolObjFactory;
        protocolObjFactory = new org.oscm.saml2.api.model.protocol.ObjectFactory();
        org.oscm.saml2.api.model.assertion.ObjectFactory assertionObjFactory;
        assertionObjFactory = new org.oscm.saml2.api.model.assertion.ObjectFactory();

        NameIDType issuer = assertionObjFactory.createNameIDType();

        issuer.setValue(this.issuer);

        LogoutRequestType logoutRequest = protocolObjFactory
                .createLogoutRequestType();
        logoutRequest.setID(requestId);
        logoutRequest.setVersion("2.0");

        logoutRequest.setIssueInstant(
                GregorianCalendars.newXMLGregorianCalendarSystemTime());
        logoutRequest.setIssuer(issuer);

        JAXBElement<LogoutRequestType> logoutRequestJAXB = protocolObjFactory
                .createLogoutRequest(logoutRequest);

        Element element = null;
        final Marshalling<LogoutRequestType> marshaller = new Marshalling<>();
        try {
            final Document document = marshaller.marshallElement(logoutRequestJAXB);
            element = samlBean
                    .signLogoutRequestElement(document.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            logoutRequestJAXB = marshaller.unmarshallDocument(element,LogoutRequestType.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        logoutRequest = logoutRequestJAXB.getValue();

        issuer.setFormat("http://schemas.xmlsoap.org/claims/UPN");
        logoutRequest.setNameID(issuer);
        logoutRequest.getSessionIndex().add(idpSessionIndex);
        return protocolObjFactory.createLogoutRequest(logoutRequest);
    }

    <T> String marshal(JAXBElement<T> authnRequest) throws Exception {
        Marshalling<T> marshaller = new Marshalling<>();
        Document samlRequestDoc = marshaller.marshallElement(authnRequest);
        String authnRequestString = XMLConverter.convertToString(
                samlRequestDoc, false);
        return XMLConverter.removeEOLCharsFromXML(authnRequestString);
    }

    private String generate160BitID() {
        byte[] randomBytes = new byte[20];
        prng.nextBytes(randomBytes);

        // the XML-Schema standard requires an ID to start with [a-zA-Z_:]
        StringBuilder sb = new StringBuilder("ID_");
        // the rest can also contain numbers
        for (byte b : randomBytes) {
            sb.append(String.format("%02x", Byte.valueOf(b)));
        }

        return sb.toString();
    }

    private String encodeBase64(String authnRequest) {
        String encodedAuthnRequest = Base64
                .encodeBase64String(authnRequest.getBytes());
        return XMLConverter.removeEOLCharsFromXML(encodedAuthnRequest);
    }
}
