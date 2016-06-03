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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Calendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import org.oscm.internal.intf.SignerService;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.saml2.api.model.assertion.NameIDType;
import org.oscm.saml2.api.model.protocol.AuthnRequestType;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;

import org.w3c.dom.Element;

/**
 * @author mgrubski
 * 
 */
public class LogoutRequestGeneratorTest {

    private LogoutRequestGenerator generator;
    private SignerService signer;

    @Before
    public void setup() throws Exception {
        signer = mock(SignerService.class);
        generator = new LogoutRequestGenerator("Issuer Name", Boolean.TRUE, signer);
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
    public void generateLogoutRequest() throws Exception {
        // given
        // when
        JAXBElement<LogoutRequestType> logoutRequest = generator
                .generateLogoutRequest("sessionId");

        // then
        verify(signer, times(1)).signLogoutRequest(any(Element.class));
    }

    @Test
    public void generateLogoutRequest_Error() throws Exception {
        // given
        final String errorMessage = "some error message";
        generator = spy(generator);
        doThrow(new TransformerException(errorMessage)).when(generator)
                .marshal(Matchers.<JAXBElement<AuthnRequestType>> any());

        // when
        try {
            generator.getEncodedLogoutRequest("sessionId");
            fail();
        } catch (SAML2AuthnRequestException e) {
            assertThat(e.getMessage(), containsString(errorMessage));
        }
    }

    @Test
    public void getEncodedLogoutRequest() throws Exception {
        // given
        final String expectedLogoutRequest = "some_logout_request";
        generator = spy(generator);
        doReturn(expectedLogoutRequest).when(generator).marshal(
                Matchers.<JAXBElement<AuthnRequestType>> any());

        // when
        String encodedLogoutReq = generator.getEncodedLogoutRequest("sessionId");

        // then
        byte[] actualLogoutReq = Base64.decodeBase64(encodedLogoutReq.getBytes());
        assertEquals(expectedLogoutRequest, new String(actualLogoutReq));
    }
}
