/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 29.04.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.sp;

import java.io.StringReader;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;

import org.oscm.converter.XMLConverter;
import org.oscm.saml2.api.Marshalling;
import org.oscm.saml2.api.model.protocol.AuthnRequestType;

/**
 * @author roderus
 * 
 */
public class AuthnRequestConverter {

    public String convertToString(JAXBElement<AuthnRequestType> jaxbElement)
            throws Exception {

        Marshalling<AuthnRequestType> marshaller = new Marshalling<AuthnRequestType>();
        Document document = marshaller.marshallElement(jaxbElement);
        String xmlString = XMLConverter.convertToString(document, false);
        return xmlString;
    }

    public String convertToPrettyEscapedString(
            JAXBElement<AuthnRequestType> authnRequest) throws Exception {

        Marshalling<AuthnRequestType> marshaller = new Marshalling<AuthnRequestType>();
        Document document = marshaller.marshallElement(authnRequest);
        String xmlString = XMLConverter.convertToString(document, false);
        String escapedXmlString = StringEscapeUtils.escapeXml(xmlString);
        return escapedXmlString;
    }

    public String convertToPrettyEscapedString(String xmlString)
            throws TransformerException {

        StreamSource source = new StreamSource(new StringReader(xmlString));
        String formatedString = XMLConverter.convertToString(source, true);
        String escapedXmlString = StringEscapeUtils.escapeXml(formatedString);
        return escapedXmlString;
    }
}
