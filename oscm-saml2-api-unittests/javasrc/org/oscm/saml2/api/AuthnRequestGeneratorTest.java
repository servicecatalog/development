/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 04.06.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.*;

import java.util.Calendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import org.oscm.internal.intf.SignerService;
import org.oscm.saml2.api.model.assertion.NameIDType;
import org.oscm.saml2.api.model.protocol.AuthnRequestType;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;
import org.w3c.dom.Element;

/**
 * @author roderus
 * 
 */
public class AuthnRequestGeneratorTest {

    private AuthnRequestGenerator generator;
    private SignerService signer;

    @Before
    public void setup() throws Exception {
        signer = mock(SignerService.class);
        generator = new AuthnRequestGenerator("Issuer Name", Boolean.TRUE, signer);
        Marshalling<LogoutRequestType>
                marshaller = new Marshalling<>();
        org.oscm.saml2.api.model.protocol.ObjectFactory protocolObjFactory;
        protocolObjFactory = new org.oscm.saml2.api.model.protocol.ObjectFactory();
        LogoutRequestType logoutRequestType = protocolObjFactory.createLogoutRequestType();
        NameIDType issuerMock = mock(NameIDType.class);
        doReturn("Issuer Name").when(issuerMock).getValue();
        logoutRequestType.setIssuer(issuerMock);
        JAXBElement<LogoutRequestType> logoutRequest = protocolObjFactory.createLogoutRequest(logoutRequestType);
        Element documentElement = marshaller.marshallElement(logoutRequest).getDocumentElement();
        doReturn(documentElement).when(signer).signLogoutRequest(any(Element.class));
    }

    @Test
    public void generateAuthnRequest_issuer()
            throws DatatypeConfigurationException {
        // given
        // when
        JAXBElement<AuthnRequestType> authnRequest = generator
                .generateAuthnRequest();

        // then
        assertEquals("Issuer Name", authnRequest.getValue().getIssuer()
                .getValue());
    }

    @Test
    public void generateAuthnRequest_Error() throws Exception {
        // given
        final String errorMessage = "some error message";
        generator = spy(generator);
        doThrow(new TransformerException(errorMessage)).when(generator)
                .marshal(Matchers.<JAXBElement<AuthnRequestType>> any());

        // when
        try {
            generator.getEncodedAuthnRequest();
            fail();
        } catch (SAML2AuthnRequestException e) {
            assertThat(e.getMessage(), containsString(errorMessage));
        }
    }

    @Test
    public void generateAuthnRequest_version()
            throws DatatypeConfigurationException {
        // given
        // when
        JAXBElement<AuthnRequestType> authnRequest = generator
                .generateAuthnRequest();

        // then
        assertEquals("2.0", authnRequest.getValue().getVersion());
    }

    @Test
    public void generateAuthnRequest_issueInstant() throws Exception {
        // given
        long expectedMills = Calendar.getInstance().getTimeInMillis();

        // when
        JAXBElement<AuthnRequestType> authnRequest = generator
                .generateAuthnRequest();
        long actualMills = authnRequest.getValue().getIssueInstant()
                .toGregorianCalendar().getTimeInMillis();

        // then: time difference max. 1 minute
        long delta = actualMills - expectedMills;
        assertTrue(delta >= 0L && delta < 60000L);
    }

    @Test
    public void generateAuthnRequest_ID() throws Exception {
        // given
        // when
        JAXBElement<AuthnRequestType> authnRequest = generator
                .generateAuthnRequest();

        // then: ID string is a string containing 40 HEX digits
        String id = authnRequest.getValue().getID();
        assertEquals(43, id.length());
        assertTrue(id.matches("ID_[0-9a-f]+"));
    }

    @Test
    public void generateAuthnRequest_assertionConsumerServiceIndex_0()
            throws Exception {
        // given
        // when
        JAXBElement<AuthnRequestType> authnRequest = generator
                .generateAuthnRequest();

        // then
        Integer actual = authnRequest.getValue()
                .getAssertionConsumerServiceIndex();
        assertEquals(actual, Integer.valueOf(0));
    }

    @Test
    public void generateAuthnRequest_assertionConsumerServiceIndex_1()
            throws Exception {
        // given
        AuthnRequestGenerator generator = new AuthnRequestGenerator(
                "Issuer Name", Boolean.FALSE);

        // when
        JAXBElement<AuthnRequestType> authnRequest = generator
                .generateAuthnRequest();

        // then
        Integer actual = authnRequest.getValue()
                .getAssertionConsumerServiceIndex();
        assertEquals(actual, Integer.valueOf(1));
    }

    @Test
    public void getEncodedAuthnRequest() throws Exception {
        // given
        final String expectedAuthnRequest = "some_authn_request";
        generator = spy(generator);
        doReturn(expectedAuthnRequest).when(generator).marshal(
                Matchers.<JAXBElement<AuthnRequestType>> any());

        // when
        String encodedAuthReq = generator.getEncodedAuthnRequest();

        // then
        byte[] actualAuthReq = Base64.decodeBase64(encodedAuthReq.getBytes());
        assertEquals(expectedAuthnRequest, new String(actualAuthReq));
    }

}
