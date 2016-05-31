/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 23.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.api;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.oscm.saml2.api.model.protocol.LogoutRequestType;

/**
 * @author roderus
 * 
 */
public class Marshalling<T> {
    public Document marshallElement(JAXBElement<T> element) throws Exception {
        Document finalDocument = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Marshaller marshaller = JAXBContext.newInstance(
                element.getDeclaredType()).createMarshaller();

        marshaller.marshal(element, finalDocument);

        return finalDocument;
    }

    public JAXBElement<T> unmarshallDocument(Element document, Class<T> clazz) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
        return (JAXBElement<T>) unmarshaller.unmarshal(document);
    }
}
