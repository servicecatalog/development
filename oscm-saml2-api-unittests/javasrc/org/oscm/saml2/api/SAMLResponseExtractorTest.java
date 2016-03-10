/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 13, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.FileInputStream;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import org.oscm.converter.XMLConverter;
import org.oscm.stream.Streams;
import org.oscm.internal.types.exception.UserIdNotFoundException;

/**
 * Tests the retrieving of the userid from a saml response.
 * 
 * @author farmaki
 * 
 */
public class SAMLResponseExtractorTest {

    private final String FILE_UNSIGNED_RESPONSE = "javares/unsignedResponse.xml";
    private final String FILE_UNSIGNED_RESPONSE_MISSING_USERID = "javares/unsignedResponse_missingUserid.xml";
    private final String FILE_UNSIGNED_ASSERTION = "javares/unsignedAssertion.xml";
    private final String FILE_UNSIGNED_ASSERTION_MISSING_USERID = "javares/unsignedAssertion_noConfirmationData_noUserid.xml";
    private SAMLResponseExtractor samlResponse;

    @Before
    public void setup() throws Exception {
        samlResponse = new SAMLResponseExtractor();
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
            throws Exception, TransformerException {
        Document document = loadDocument(unsignedResponse);
        String idpResponse = XMLConverter.convertToString(document, true);
        String encodedIdpResponse = encode(idpResponse);
        return encodedIdpResponse;
    }

    @Test
    public void getUserId_Response() throws Exception {
        // given the content of an encoded idp response
        String encodedIdpResponse = getEncodedIdpResponse(FILE_UNSIGNED_RESPONSE);

        // when
        String userId = samlResponse.getUserId(encodedIdpResponse);

        // then
        assertEquals("administrator", userId);
    }

    @Test(expected = UserIdNotFoundException.class)
    public void getUserIdNotFound_Response() throws Exception {
        // given the content of an encoded idp response where the userid
        // attribute is missing in the original content
        String encodedIdpResponse = getEncodedIdpResponse(FILE_UNSIGNED_RESPONSE_MISSING_USERID);

        // when
        samlResponse.getUserId(encodedIdpResponse);

        // then a UserIdNotFoundException is expected
    }

    @Test
    public void getUserId_Assertion() throws Exception {
        // given
        String encodedAssertion = getEncodedIdpResponse(FILE_UNSIGNED_ASSERTION);

        // when
        String userId = samlResponse.getUserId(encodedAssertion);

        // then
        assertEquals("administrator", userId);
    }

    @Test(expected = UserIdNotFoundException.class)
    public void getUserIdNotFound_Assertion() throws Exception {
        // given
        String encodedIdpResponse = getEncodedIdpResponse(FILE_UNSIGNED_ASSERTION_MISSING_USERID);

        // when
        samlResponse.getUserId(encodedIdpResponse);

        // then a UserIdNotFoundException is expected
    }

    @Test(expected = UserIdNotFoundException.class)
    public void getUserId_error() throws Exception {
        // given an XPathExpressionException when loading the attributes
        SAMLResponseExtractor samlResponse = spy(new SAMLResponseExtractor());
        doThrow(new XPathExpressionException("")).when(samlResponse)
                .extractUserId(any(Document.class));

        String encodedIdpResponse = getEncodedIdpResponse(FILE_UNSIGNED_RESPONSE);

        // when
        samlResponse.getUserId(encodedIdpResponse);

        // then a UserIdNotFoundException is expected
    }

}
