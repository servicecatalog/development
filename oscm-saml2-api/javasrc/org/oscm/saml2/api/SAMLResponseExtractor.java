/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 13, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;

import org.oscm.converter.XMLConverter;
import org.oscm.internal.types.exception.SessionIndexNotFoundException;
import org.oscm.internal.types.exception.UserIdNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.types.enumtypes.LogMessageIdentifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.xml.ws.security.opt.impl.incoming.SAMLAssertion;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.saml.util.SAMLUtil;

/**
 * Class for retrieving the userid from a saml:response received from an IdP.
 *
 * @author farmaki
 */
public class SAMLResponseExtractor {

    private static final String USER_SAML2_ATTRIBUTE_USERID_XPATH_EXPR = "//*[local-name()='Assertion']" //
            + "//*[local-name()='AttributeStatement']" //
            + "//*[local-name()='Attribute'][@Name='userid']" //
            + "/*[local-name()='AttributeValue']";

    private static final String USER_SAML2_ATTRIBUTE_NAME_XPATH_EXPR = "//*[local-name()='Assertion']" //
            + "//*[local-name()='AttributeStatement']" //
            + "//*[local-name()='Attribute'][@Name='name']" //
            + "/*[local-name()='AttributeValue']";

    private static final String USER_SAML1_ATTRIBUTE_USERID_XPATH_EXPR = "//*[local-name()='Assertion']" //
            + "//*[local-name()='AttributeStatement']" //
            + "//*[local-name()='Attribute'][@AttributeName='userid']" //
            + "/*[local-name()='AttributeValue']";

    private static final String USER_SAML1_ATTRIBUTE_NAME_XPATH_EXPR = "//*[local-name()='Assertion']" //
            + "//*[local-name()='AttributeStatement']" //
            + "//*[local-name()='Attribute'][@AttributeName='name']" //
            + "/*[local-name()='AttributeValue']";

    private static final String SESSION_INDEX_SAML2_ATTRIBUTE_NAME_XPATH_EXPR = "//*[local-name()='Assertion']//*[local-name()='AuthnStatement']//@*[local-name()='SessionIndex']";

    private static final String STATUS_LOGOUT_RESPONSE_SAML2_XPATH_EXPR = "//*[local-name()='Status']//*[local-name()='StatusCode']//@*[local-name()='Value']";




    /**
     * Retrieves the userid from an encoded saml:Response String.
     *
     * @param encodedSamlResponse
     *            the encoded saml response
     * @return the userid as a String
     * @throws UnsupportedEncodingException
     */
    public String getUserId(String encodedSamlResponse)
            throws UserIdNotFoundException {

        String userId;

        try {
            userId = getUserId_decoded(decode(encodedSamlResponse));
        } catch (UnsupportedEncodingException exception) {
            throw new UserIdNotFoundException(
                    String.format(
                            "An exception occurred while Base64-decoding the SAML response:\n%s",
                            encodedSamlResponse),
                    UserIdNotFoundException.ReasonEnum.EXCEPTION_OCCURRED,
                    exception, new String[] { encodedSamlResponse });
        }

        return userId;
    }

    /**
     * Retrieves the sessionIndex from an encoded saml:Response String.
     *
     * @param encodedSamlResponse
     *            the encoded saml response
     * @return the sessionIndex as a String
     * @throws UnsupportedEncodingException
     */
    public String getSessionIndex(String encodedSamlResponse)
            throws SessionIndexNotFoundException {

        String sessionIndex;

        try {
            sessionIndex = getSessionIndexDecoded(decode(encodedSamlResponse));
        } catch (UnsupportedEncodingException exception) {
            throw new SessionIndexNotFoundException(
                    String.format(
                            "An exception occurred while Base64-decoding the SAML response:\n%s",
                            encodedSamlResponse),
                    SessionIndexNotFoundException.ReasonEnum.EXCEPTION_OCCURRED,
                    exception, new String[] { encodedSamlResponse });
        }

        return sessionIndex;
    }

    private String getSessionIndexDecoded(String samlResponse)
            throws SessionIndexNotFoundException {
        String sessionIndex;

        try {
            Document document = XMLConverter.convertToDocument(samlResponse,
                    true);

            sessionIndex = extractSessionIndex(document);
        } catch (XPathExpressionException | ParserConfigurationException
                | SAXException | IOException exception) {
            throw new SessionIndexNotFoundException(
                    String.format(
                            "An exception occurred while retrieving the sessionIndex from the saml response:\n%s",
                            samlResponse),
                    SessionIndexNotFoundException.ReasonEnum.EXCEPTION_OCCURRED,
                    exception, new String[] { samlResponse });
        }

        if (sessionIndex == null || sessionIndex.trim().length() == 0) {
            throw new SessionIndexNotFoundException(
                    String.format(
                            "The sessionIndex attribute was not found for the saml response:\n%s",
                            samlResponse),
                    SessionIndexNotFoundException.ReasonEnum.SESSION_ATTRIBUTE_NOT_FOUND,
                    new String[] { samlResponse });

        }
        return sessionIndex;
    }

    private String getUserId_decoded(String samlResponse)
            throws UserIdNotFoundException {
        String userid;

        try {
            Document document = XMLConverter.convertToDocument(samlResponse,
                    true);

            userid = extractUserId(document);
        } catch (XPathExpressionException | ParserConfigurationException
                | SAXException | IOException exception) {
            throw new UserIdNotFoundException(
                    String.format(
                            "An exception occurred while retrieving the userid from the saml response:\n%s",
                            samlResponse),
                    UserIdNotFoundException.ReasonEnum.EXCEPTION_OCCURRED,
                    exception, new String[] { samlResponse });
        }

        if (userid == null || userid.trim().length() == 0) {
            throw new UserIdNotFoundException(
                    String.format(
                            "The userid attribute was not found for the saml response:\n%s",
                            samlResponse),
                    UserIdNotFoundException.ReasonEnum.USERID_ATTRIBUTE_NOT_FOUND,
                    new String[] { samlResponse });

        }
        return userid;
    }

    String extractUserId(Document samlResponse)
            throws XPathExpressionException {
        String userId = XMLConverter.getNodeTextContentByXPath(samlResponse,
                USER_SAML2_ATTRIBUTE_USERID_XPATH_EXPR);
        if (userId != null) {
            return userId;
        }

        userId = XMLConverter.getNodeTextContentByXPath(samlResponse,
                USER_SAML1_ATTRIBUTE_USERID_XPATH_EXPR);
        if (userId != null) {
            return userId;
        }

        userId = XMLConverter.getNodeTextContentByXPath(samlResponse,
                USER_SAML2_ATTRIBUTE_NAME_XPATH_EXPR);
        if (userId != null) {
            return userId;
        }

        userId = XMLConverter.getNodeTextContentByXPath(samlResponse,
                USER_SAML1_ATTRIBUTE_NAME_XPATH_EXPR);
        return userId;
    }

    String extractSessionIndex(Document samlResponse)
            throws XPathExpressionException {
        String samlSessionId = XMLConverter.getNodeTextContentByXPath(
                samlResponse, SESSION_INDEX_SAML2_ATTRIBUTE_NAME_XPATH_EXPR);
        return samlSessionId;
    }

    public String getUserId(SAMLAssertion samlResponse)
            throws UserIdNotFoundException {
        String samlAssertionString;
        try {
            Element samlAssertion = SAMLUtil
                    .createSAMLAssertion(samlResponse.getSamlReader());
            samlAssertionString = XMLConverter.convertToString(samlAssertion,
                    false);
        } catch (XWSSecurityException | XMLStreamException
                | TransformerException exception) {
            throw new UserIdNotFoundException(
                    "An XML exception occurred while processing the SAML response",
                    UserIdNotFoundException.ReasonEnum.EXCEPTION_OCCURRED,
                    exception);
        }
        return getUserId_decoded(samlAssertionString);
    }

    public String decode(String encodedString)
            throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decodeBase64(encodedString);
        String decodedString = new String(decodedBytes, "UTF-8");
        return decodedString;
    }

    public boolean isFromLogout(String encodedSamlResponse) {
        try {
            return decode(encodedSamlResponse).contains("LogoutResponse");
        } catch (IOException e) {
            // TODO: Add specific exception
            throw new RuntimeException(e);
        }
    }

    public String getSAMLLogoutResponseStatusCode(String encodedSamlResponse) {
        Document document = null;
        try {
            document = XMLConverter.convertToDocument(decode(encodedSamlResponse),
                    true);
            String resultWithNameSpace = XMLConverter.getNodeTextContentByXPath(document,
                    STATUS_LOGOUT_RESPONSE_SAML2_XPATH_EXPR);
            return removeNameSpaceFromStatus(resultWithNameSpace);
        } catch (ParserConfigurationException | SAXException | IOException |XPathExpressionException e) {
            // TODO: Add specific exception
            throw new RuntimeException(e);
        }
    }

    private String removeNameSpaceFromStatus(String statusWithNameSpace) {
        String[] splitted = statusWithNameSpace.split(":");
        String result = splitted[splitted.length - 1];
        return result;
    }




}
