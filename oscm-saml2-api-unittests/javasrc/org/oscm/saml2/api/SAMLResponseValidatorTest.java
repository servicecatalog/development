/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 01, 2016
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Before;
import org.junit.Test;

import org.oscm.converter.XMLConverter;
import org.oscm.internal.types.exception.SAML2StatusCodeInvalidException;
import org.oscm.stream.Streams;

import org.w3c.dom.Document;

/**
 * Tests the validating the SAML LogoutResponse status code.
 * 
 * @author farmaki
 * 
 */
public class SAMLResponseValidatorTest {

    private final String FILE_UNSIGNED_LOGOUT_RESPONSE = "javares/openamUnsignedLogoutResponse.xml";
    private final String FILE_UNSIGNED_LOGOUT_RESPONSE_ERROR_STATUS = "javares/openamUnsignedLogoutResponse_ErrorCode.xml";
    private final String FILE_UNSIGNED_LOGOUT_RESPONSE_INVALID_STATUS = "javares/openamUnsignedLogoutResponse_InvalidStatus.xml";
    private SAMLResponseExtractor samlResponseExtractor;
    private SAMLLogoutResponseValidator samlLogoutResponseValidator;

    @Before
    public void setup() throws Exception {
        samlResponseExtractor = new SAMLResponseExtractor();
        samlLogoutResponseValidator = new SAMLLogoutResponseValidator();
    }

    private Document loadDocument(String file) throws Exception {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            Document document = XMLConverter.convertToDocument(inputStream);
            inputStream.close();
            return document;
        } finally {
            Streams.close(inputStream);
        }
    }

    private String encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    private String getEncodedIdpResponse(String unsignedResponse)
            throws Exception {
        Document document = loadDocument(unsignedResponse);
        String idpResponse = XMLConverter.convertToString(document, true);
        String encodedIdpResponse = encode(idpResponse);
        return encodedIdpResponse;
    }

    @Test
    public void responseStatusCodeSuccessful_noError() throws Exception {
        // given
        String response = getEncodedIdpResponse(FILE_UNSIGNED_LOGOUT_RESPONSE);
        // when
        final boolean successful = samlLogoutResponseValidator.responseStatusCodeSuccessful(response);
        // then
        assertTrue(successful);
    }

    @Test(expected = SAML2StatusCodeInvalidException.class)
    public void responseStatusCodeSuccessful_invalidStatus() throws Exception {

        // given
        String response = getEncodedIdpResponse(FILE_UNSIGNED_LOGOUT_RESPONSE_INVALID_STATUS);
        // when
        final boolean successful = samlLogoutResponseValidator.responseStatusCodeSuccessful(response);
        // then
        assertFalse(successful);
    }

    @Test
    public void responseStatusCodeSuccessful_hasError() throws Exception {

        // given
        String response = getEncodedIdpResponse(FILE_UNSIGNED_LOGOUT_RESPONSE_ERROR_STATUS);
        // when
        final boolean successful = samlLogoutResponseValidator.responseStatusCodeSuccessful(response);
        // then
        assertFalse(successful);
    }



}
