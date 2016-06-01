/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.xml.Transformers;
import org.oscm.internal.types.exception.SaaSSystemException;

import static org.oscm.saml.api.Response.ISSUER;
import static org.oscm.saml.api.Response.LOGOUT_REQUEST;

/**
 * @author barzu
 */
public class ResponseParser {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(ResponseParser.class);

    private XmlParser parser;

    public ResponseParser(String samlResponse) {
        parser = new XmlParser(samlResponse);
    }

    public Response unmarshall() throws ParseException {
        return Response.unmarshall(getResponseElement());
    }

    public static String toString(Node node) {
        Transformer transformer;
        StringWriter writer = new StringWriter();
        try {
            transformer = Transformers.newTransformer();
            Result result = new StreamResult(writer);
            Source source = new DOMSource(node);
            transformer.transform(source, result);
            return writer.toString();
        } catch (TransformerConfigurationException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Creating a Transformer for the SAML response failed.", e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_CREATE_TRANSFORMER_FOR_SAML_FAILED);
            throw se;
        } catch (TransformerException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Transforming the SAML response DOM-Tree to string failed.",
                    e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_TRANSFORM_SAML_DOM_TREE_TO_STRING_FAILED);
            throw se;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                SaaSSystemException se = new SaaSSystemException(
                        "Closing the SAML response writer failed.", e);
                logger.logError(Log4jLogger.SYSTEM_LOG, se,
                        LogMessageIdentifier.ERROR_CLOSE_SAML_WRITER_FAILED);
                throw se;
            }
        }
    }

    public Element getResponseElement() {
        NodeList responseNodes = parser.parseTags(Response.RESPONSE);
        if (responseNodes.getLength() < 1) {
            throw new IllegalStateException("No <Response> occurence");
        }
        if (responseNodes.getLength() > 1) {
            throw new IllegalStateException("Duplicate <Response> occurence");
        }
        Node responseNode = responseNodes.item(0);
        if (!(responseNode instanceof Element)) {
            SaaSSystemException se = new SaaSSystemException(
                    "Malformed <Response> element.");
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_MALFORMED_ELEMENT, "<Response>");
            throw se;
        }
        return (Element) responseNode;
    }

    public List<Element> getAssertionElements() {
        List<Element> assertions = new ArrayList<Element>();
        NodeList children = getResponseElement().getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (Assertion.ASSERTION.equals(child.getLocalName())) {
                if (!(child instanceof Element)) {
                    SaaSSystemException se = new SaaSSystemException(
                            "Malformed <Assertion> element.");
                    logger.logError(Log4jLogger.SYSTEM_LOG, se,
                            LogMessageIdentifier.ERROR_MALFORMED_ELEMENT,
                            "<Assertion>");
                    throw se;
                }
                assertions.add((Element) child);
            }
        }
        return assertions;
    }

    public Element getLogoutRequestElement() {
        NodeList responseNodes = parser.parseTags(LOGOUT_REQUEST);
        if (responseNodes.getLength() < 1) {
            throw new IllegalStateException("No " + LOGOUT_REQUEST + " occurence");
        }
        if (responseNodes.getLength() > 1) {
            throw new IllegalStateException("Duplicate " + LOGOUT_REQUEST + " occurence");
        }
        Node responseNode = responseNodes.item(0);
        if (!(responseNode instanceof Element)) {
            SaaSSystemException se = new SaaSSystemException(
                    "Malformed " + LOGOUT_REQUEST + " element.");
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_MALFORMED_ELEMENT, LOGOUT_REQUEST);
            throw se;
        }
        return (Element) responseNode;
    }

    public Element getIssuerElement() {
        NodeList responseNodes = parser.parseTags(ISSUER);
        if (responseNodes.getLength() < 1) {
            throw new IllegalStateException("No " + ISSUER + " occurence");
        }
        if (responseNodes.getLength() > 1) {
            throw new IllegalStateException("Duplicate " + ISSUER + " occurence");
        }
        Node responseNode = responseNodes.item(0);
        if (!(responseNode instanceof Element)) {
            SaaSSystemException se = new SaaSSystemException(
                    "Malformed " + ISSUER + " element.");
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_MALFORMED_ELEMENT, ISSUER);
            throw se;
        }
        return (Element) responseNode;
    }

    static class XmlParser {

        private Document dom;

        XmlParser(String samlResponse) {
            if (samlResponse == null) {
                throw new NullPointerException(
                        "assertion failed: Saml response must not be null");
            }
            if (samlResponse.trim().length() == 0) {
                throw new IllegalArgumentException(
                        "assertion failed: Saml response must not be null");
            }

            this.dom = buildDocument(samlResponse);
        }

        private static Document buildDocument(String xml) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new ByteArrayInputStream(xml
                        .getBytes("UTF-8")));
                return document;
            } catch (Exception e) {
                SaaSSystemException se = new SaaSSystemException(
                        "Building DOM-Tree from SAML response failed.", e);
                logger.logError(Log4jLogger.SYSTEM_LOG, se,
                        LogMessageIdentifier.ERROR_BUILD_SAML_DOM_TREE_FAILED);
                throw se;
            }
        }

        String parseAttribute(String tagname, String attributeName) {
            return parseAttribute(parseTag(tagname), attributeName);
        }

        static String parseAttribute(Node tag, String attributeName) {
            NamedNodeMap attributeMap = tag.getAttributes();
            Node attribute = attributeMap.getNamedItem(attributeName);
            String value = null;
            if (attribute != null) {
                value = attribute.getNodeValue();
            }
            return value;
        }

        static String parseLocalAttribute(Node tag, String attributeName) {
            String value = parseAttribute(tag, attributeName);
            if (value != null && value.contains(":")) {
                // without Name Space
                value = value.substring(value.indexOf(':') + 1);
            }
            return value;
        }

        Node parseTag(String tagname) {
            NodeList nodeList = dom.getElementsByTagNameNS(
                    "urn:oasis:names:tc:SAML:1.0:protocol", tagname);
            if (nodeList.getLength() == 0) {
                nodeList = dom.getElementsByTagNameNS(
                        "urn:oasis:names:tc:SAML:1.0:assertion", tagname);
            }
            if (nodeList.getLength() == 0) {
                return null;
            }
            Node tag = nodeList.item(0);
            return tag;
        }

        NodeList parseTags(String tagname) {
            NodeList nodeList = dom.getElementsByTagNameNS(
                    "urn:oasis:names:tc:SAML:1.0:protocol", tagname);
            if (nodeList.getLength() == 0) {
                nodeList = dom.getElementsByTagNameNS(
                        "urn:oasis:names:tc:SAML:1.0:assertion", tagname);
            }
            return nodeList;
        }

        String parseTextContent(String tagname) {
            Node tag = parseTag(tagname);
            return tag.getTextContent();
        }

        Document getDom() {
            return dom;
        }

    }

}
