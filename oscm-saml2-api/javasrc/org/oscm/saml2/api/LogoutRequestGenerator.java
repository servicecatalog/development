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

import org.apache.commons.codec.binary.Base64;

import org.oscm.calendar.GregorianCalendars;
import org.oscm.converter.XMLConverter;
import org.oscm.internal.intf.SignerService;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.saml2.api.model.assertion.NameIDType;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mgrubski
 */
public class LogoutRequestGenerator {

    private final static String SAML_VERSION = "2.0";

    private Random prng = new Random();
    private String issuer;
    private Boolean isHttps;
    private String requestId;
    private SignerService samlBean;

    public LogoutRequestGenerator(String issuer, Boolean isHttps) {
        this.issuer = issuer;
        this.isHttps = isHttps;
        this.requestId = generate160BitID();
    }

    public LogoutRequestGenerator(String issuer, Boolean isHttps,
                                  SignerService samlBean) {
        this(issuer, isHttps);
        this.samlBean = samlBean;
    }

    /**
     * @param idpSessionIndex
     * @return BASE64-encoded SAML Authentication Request
     * @throws SAML2AuthnRequestException
     */
    public String getEncodedLogoutRequest(String idpSessionIndex) throws SAML2AuthnRequestException {
        String logoutRequest;
        try {
            logoutRequest = marshal(generateLogoutRequest(idpSessionIndex));
        } catch (SAML2AuthnRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new SAML2AuthnRequestException(
                    e.getMessage(),
                    SAML2AuthnRequestException.ReasonEnum.XML_TRANSFORMATION_ERROR);
        }
        return encodeBase64(logoutRequest);
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

    public JAXBElement<LogoutRequestType> generateLogoutRequest(String idpSessionIndex) throws Exception {

        org.oscm.saml2.api.model.protocol.ObjectFactory protocolObjFactory;
        protocolObjFactory = new org.oscm.saml2.api.model.protocol.ObjectFactory();
        org.oscm.saml2.api.model.assertion.ObjectFactory assertionObjFactory;
        assertionObjFactory = new org.oscm.saml2.api.model.assertion.ObjectFactory();

        NameIDType issuer = assertionObjFactory.createNameIDType();
        NameIDType nameId = assertionObjFactory.createNameIDType();
        nameId.setValue(this.issuer);
        issuer.setValue(this.issuer);

        LogoutRequestType logoutRequest = protocolObjFactory
                .createLogoutRequestType();
        logoutRequest.setID(requestId);
        logoutRequest.setVersion(SAML_VERSION);

        logoutRequest.setIssueInstant(
                GregorianCalendars.newXMLGregorianCalendarSystemTime());
        logoutRequest.setIssuer(issuer);

        JAXBElement<LogoutRequestType> logoutRequestJAXB = protocolObjFactory
                .createLogoutRequest(logoutRequest);

        nameId.setFormat("http://schemas.xmlsoap.org/claims/UPN");
        logoutRequest.setNameID(nameId);
        logoutRequest.getSessionIndex().add(idpSessionIndex);
        encodeBase64(logoutRequestJAXB.toString());
        logoutRequestJAXB = signLogoutRequest(logoutRequestJAXB);
        logoutRequest = logoutRequestJAXB.getValue();

        return protocolObjFactory.createLogoutRequest(logoutRequest);
    }

    protected JAXBElement<LogoutRequestType> signLogoutRequest(JAXBElement<LogoutRequestType> logoutRequestJAXB) throws Exception {
        Element marshaled = marshallJAXBElement(logoutRequestJAXB);
        Element signed = samlBean.signLogoutRequest(marshaled);
        logoutRequestJAXB = unmarshallJAXBElement(signed);
        return logoutRequestJAXB;
    }

    private Element marshallJAXBElement(JAXBElement<LogoutRequestType> logoutRequestJAXB) throws Exception {
        Marshalling<LogoutRequestType>
                marshaller = new Marshalling<>();
        return marshaller.marshallElement(logoutRequestJAXB).getDocumentElement();
    }

    private JAXBElement<LogoutRequestType> unmarshallJAXBElement(Element signed) throws Exception {
        Marshalling<LogoutRequestType>
                marshaller = new Marshalling<>();
        return marshaller.unmarshallDocument(signed, LogoutRequestType.class);
    }

    <T> String marshal(JAXBElement<T> logoutRequest) throws Exception {
        Marshalling<T> marshaller = new Marshalling<>();
        Document samlRequestDoc = marshaller.marshallElement(logoutRequest);
        String logoutRequestString = XMLConverter.convertToString(
                samlRequestDoc, false);
        return XMLConverter.removeEOLCharsFromXML(logoutRequestString);
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

    private String encodeBase64(String logoutRequest) {
        String encodedLogoutRequest = Base64
                .encodeBase64String(logoutRequest.getBytes());
        return XMLConverter.removeEOLCharsFromXML(encodedLogoutRequest);
    }
}
