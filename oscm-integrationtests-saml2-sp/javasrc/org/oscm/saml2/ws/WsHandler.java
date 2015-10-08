/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 07.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.ws;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.oscm.converter.XMLConverter;

/**
 * @author kulle
 * 
 */
public class WsHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        SOAPMessage soapMessage = context.getMessage();
        try {
            Document soapBodyDocument = soapMessage.getSOAPBody()
                    .getOwnerDocument();
            Node nodeName = XMLConverter.getNodeListByXPath(soapBodyDocument,
                    "//name").item(0);
            if (nodeName != null) {
                String result = invertTextContent(nodeName);
                nodeName.setTextContent(result);
            }
        } catch (SOAPException | XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String invertTextContent(Node node) {
        String textContent = node.getTextContent();
        char[] charArray = textContent.toCharArray();
        String result = "";
        for (int i = charArray.length - 1; i > -1; i--) {
            result = result + charArray[i];
        }
        return result;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

}
