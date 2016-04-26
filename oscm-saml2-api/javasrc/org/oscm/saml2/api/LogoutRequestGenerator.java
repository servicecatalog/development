/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 22.04.2016
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import java.util.Random;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.binary.Base64;

import org.oscm.calendar.GregorianCalendars;
import org.oscm.converter.XMLConverter;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.saml2.api.model.assertion.NameIDType;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;

import org.w3c.dom.Document;

public class LogoutRequestGenerator {

    private Random prng = new Random();
    private String issuer;
    private String requestId;

    public LogoutRequestGenerator(String issuer) {
        this.issuer = issuer;
        this.requestId = generate160BitID();
    }

    /**
     * @return BASE64-encoded SAML Authentication Request
     * @throws SAML2AuthnRequestException
     */
    public String getEncodedLogoutRequest() throws SAML2AuthnRequestException {
        String logoutRequest = null;
        try {
            logoutRequest = marshal(generateLogoutRequest());
        } catch (SAML2AuthnRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new SAML2AuthnRequestException(
                    e.getMessage(),
                    SAML2AuthnRequestException.ReasonEnum.XML_TRANSFORMATION_ERROR);
        }
        return encodeBase64(logoutRequest);
    }

    public String getRequestId() {
        return requestId;
    }

    public JAXBElement<LogoutRequestType> generateLogoutRequest()
            throws DatatypeConfigurationException {

        org.oscm.saml2.api.model.protocol.ObjectFactory protocolObjFactory;
        protocolObjFactory = new org.oscm.saml2.api.model.protocol.ObjectFactory();
        org.oscm.saml2.api.model.assertion.ObjectFactory assertionObjFactory;
        assertionObjFactory = new org.oscm.saml2.api.model.assertion.ObjectFactory();

        NameIDType issuer = assertionObjFactory.createNameIDType();
        issuer.setValue(this.issuer);

        final NameIDType nameIdType = new NameIDType();
        nameIdType.setValue("test");
        nameIdType.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        nameIdType.setNameQualifier("http://localhost:8080/OpenAM-13.0.0");

        LogoutRequestType logoutRequest = protocolObjFactory
                .createLogoutRequestType();
        logoutRequest.setID(requestId);
        logoutRequest.setVersion("2.0");
        logoutRequest.setIssuer(issuer);
        logoutRequest.setIssueInstant(GregorianCalendars
                .newXMLGregorianCalendarSystemTime());
        logoutRequest.setNameID(nameIdType);

        JAXBElement<LogoutRequestType> logoutRequestJAXB = protocolObjFactory
                .createLogoutRequest(logoutRequest);

        return logoutRequestJAXB;
    }

    String marshal(JAXBElement<LogoutRequestType> logoutRequest) throws Exception {
        Marshalling<LogoutRequestType> marshaller = new Marshalling<LogoutRequestType>();
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
        String encodedLogoutRequest = Base64.encodeBase64String(logoutRequest
                .getBytes());
        return XMLConverter.removeEOLCharsFromXML(encodedLogoutRequest);
    }
}
