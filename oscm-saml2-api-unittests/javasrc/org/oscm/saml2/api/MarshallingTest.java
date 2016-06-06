/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 31.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import org.oscm.calendar.GregorianCalendars;
import org.oscm.converter.XMLConverter;
import org.oscm.saml2.api.model.assertion.NameIDType;
import org.oscm.saml2.api.model.protocol.AuthnRequestType;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;
import org.oscm.saml2.api.model.xmldsig.SignatureType;

import org.w3c.dom.Document;

/**
 * @author roderus
 * 
 */
public class MarshallingTest {

    private Marshalling<AuthnRequestType> marshaller;
    private Marshalling<LogoutRequestType> logoutMarshaller;
    private org.oscm.saml2.api.model.protocol.ObjectFactory protocolObjFactory;
    private org.oscm.saml2.api.model.assertion.ObjectFactory assertionObjFactory;

    @Before
    public void setup() {
        marshaller = new Marshalling<AuthnRequestType>();
        logoutMarshaller = new Marshalling<>();
        protocolObjFactory = new org.oscm.saml2.api.model.protocol.ObjectFactory();
        assertionObjFactory = new org.oscm.saml2.api.model.assertion.ObjectFactory();
    }

    @Test
    public void marshallElement_authnRequest() throws Exception {
        // given
        XMLGregorianCalendar gregCalendar = GregorianCalendars
                .newXMLGregorianCalendarSystemTime();
        JAXBElement<AuthnRequestType> authnRequestJAXB = createAuthnRequest(gregCalendar);

        // when
        Document authnRequestDoc = marshaller.marshallElement(authnRequestJAXB);

        // then
        String issuerValueResult = XMLConverter.getNodeTextContentByXPath(
                authnRequestDoc, "/samlp:AuthnRequest/saml:Issuer");
        assertEquals("Issuer Test Name", issuerValueResult);

        String versionResult = XMLConverter.getNodeTextContentByXPath(
                authnRequestDoc, "/samlp:AuthnRequest/@Version");
        assertEquals("2.0", versionResult);

        String idResult = XMLConverter.getNodeTextContentByXPath(
                authnRequestDoc, "/samlp:AuthnRequest/@ID");
        assertEquals("4040406c-1530-11e0-e869-0110283f4jj6", idResult);

        String issueInstantResult = XMLConverter.getNodeTextContentByXPath(
                authnRequestDoc, "/samlp:AuthnRequest/@IssueInstant");
        assertEquals(gregCalendar.toString(), issueInstantResult);

        String ACSResult = XMLConverter.getNodeTextContentByXPath(
                authnRequestDoc,
                "/samlp:AuthnRequest/@AssertionConsumerServiceIndex");
        assertEquals("0", ACSResult);
    }

    @Test
    public void unmarshallElement() throws Exception {
        //given
        XMLGregorianCalendar gregCalendar = GregorianCalendars
                .newXMLGregorianCalendarSystemTime();
        JAXBElement<AuthnRequestType> authnRequestJAXB = createAuthnRequest(gregCalendar);
        authnRequestJAXB.getValue().getIssuer().setValue("someIssuer");
        final SignatureType signature = new SignatureType();
        signature.setId("someSigID");
        authnRequestJAXB.getValue().setSignature(signature);
        authnRequestJAXB.getValue().setVersion("someVersion");
        Document authnRequestDoc = marshaller.marshallElement(authnRequestJAXB);
        // when
        JAXBElement<AuthnRequestType> unmarshalled = marshaller.unmarshallDocument(authnRequestDoc.getDocumentElement(), AuthnRequestType.class);
        //then
        assertTrue(unmarshalled.getValue() instanceof AuthnRequestType);
        assertTrue(unmarshalled.getValue().getIssuer().getValue().equals("someIssuer"));
        assertTrue(unmarshalled.getValue().getSignature().getId().equals("someSigID"));
        assertTrue(unmarshalled.getValue().getVersion().equals("someVersion"));
    }
    @Test
    public void unmarshallElement_logout() throws Exception {
        //given
        XMLGregorianCalendar gregCalendar = GregorianCalendars
                .newXMLGregorianCalendarSystemTime();
        JAXBElement<LogoutRequestType> logoutRequestJAXB = createLogoutRequest(gregCalendar);
        Document authnRequestDoc = logoutMarshaller.marshallElement(logoutRequestJAXB);
        // when
        JAXBElement<LogoutRequestType> unmarshalled = logoutMarshaller.unmarshallDocument(authnRequestDoc.getDocumentElement(), LogoutRequestType.class);
        //then
        assertTrue(unmarshalled.getValue() instanceof LogoutRequestType);
        assertTrue(unmarshalled.getValue().getIssuer().getValue().equals("someIssuer"));
        assertTrue(unmarshalled.getValue().getSignature().getId().equals("someSigID"));
        assertTrue(unmarshalled.getValue().getVersion().equals("someVersion"));
    }

    private JAXBElement<AuthnRequestType> createAuthnRequest(
            XMLGregorianCalendar gregCalendar) {
        NameIDType issuer = assertionObjFactory.createNameIDType();
        AuthnRequestType authnRequest = protocolObjFactory
                .createAuthnRequestType();

        issuer.setValue("Issuer Test Name");
        authnRequest.setIssuer(issuer);
        authnRequest.setID("4040406c-1530-11e0-e869-0110283f4jj6");
        authnRequest.setVersion("2.0");
        authnRequest.setIssueInstant(gregCalendar);
        authnRequest.setAssertionConsumerServiceIndex(Integer.valueOf(0));

        return protocolObjFactory.createAuthnRequest(authnRequest);
    }

    private JAXBElement<LogoutRequestType> createLogoutRequest(
            XMLGregorianCalendar gregCalendar) {
        NameIDType issuer = assertionObjFactory.createNameIDType();
        final SignatureType signature = new SignatureType();
        LogoutRequestType logoutRequest = protocolObjFactory
                .createLogoutRequestType();

        issuer.setValue("someIssuer");
        logoutRequest.setIssuer(issuer);
        logoutRequest.setID("someID");
        logoutRequest.setVersion("someVersion");
        logoutRequest.setIssueInstant(gregCalendar);
        signature.setId("someSigID");
        logoutRequest.setSignature(signature);

        return protocolObjFactory.createLogoutRequest(logoutRequest);
    }
}
