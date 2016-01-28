/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jan 13, 2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.apiversioning.soapmgmt.parser;

import java.util.Iterator;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * @author zhaoh.fnst
 * 
 */
public class SoapRequestParser {

    private static final String VERSION = "ctmg.service.version";
    private static final String MESSAGEBODYTAG = "S:Body";
    private static final int OPERATIONNAMEINDEX = 4;

    public static String parseApiVersion(SOAPMessageContext context)
            throws SOAPException {

        SOAPMessage soapMessage = context.getMessage();

        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        if (soapHeader == null) {
            return "";
        }

        Iterator<?> it = soapHeader.extractHeaderElements(VERSION);
        if (it == null || !it.hasNext()) {
            return "";
        }

        Node node = (Node) it.next();
        String value = node == null ? null : node.getValue();

        return value;
    }

    public static String parseOperationName(SOAPMessageContext context)
            throws SOAPException {
        String operationName = context.getMessage().getSOAPBody()
                .getOwnerDocument().getElementsByTagName(MESSAGEBODYTAG).item(0)
                .getChildNodes().item(0).getNodeName()
                .substring(OPERATIONNAMEINDEX);

        return operationName;
    }

    public static String parseServiceName(SOAPMessageContext context) {
        String serviceNameLine = context.get(MessageContext.WSDL_SERVICE)
                .toString();
        String serviceName = serviceNameLine
                .substring(serviceNameLine.lastIndexOf("}") + 1);
        return serviceName;
    }

    public static SOAPBodyElement getServiceParam(SOAPMessageContext context,
            String serviceName, String paramName) throws SOAPException {

        SOAPMessage soapMessage = context.getMessage();
        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPBody soapBody = soapEnvelope.getBody();

        @SuppressWarnings("unchecked")
        Iterator<SOAPBodyElement> elements = soapBody.getChildElements();

        while (elements.hasNext()) {
            SOAPBodyElement element = elements.next();

            if (element.getNodeName().contains(serviceName)) {

                @SuppressWarnings("unchecked")
                Iterator<SOAPBodyElement> params = element.getChildElements();

                while (params.hasNext()) {
                    SOAPBodyElement param = params.next();

                    if (paramName.equals(param.getNodeName())) {
                        return param;
                    }
                }
            }
        }

        throw new SOAPException(
                "Soap message param " + paramName + " not found.");
    }
    
    public static SOAPBodyElement getChildNode(SOAPBodyElement element, String name) throws SOAPException {

        @SuppressWarnings("unchecked")
        Iterator<SOAPBodyElement> elements = element.getChildElements();

        while (elements.hasNext()) {
            SOAPBodyElement childNode = elements.next();

            if (childNode.getNodeName().contains(name)) {
                return childNode;
            }
        }

        throw new SOAPException(
                "Child element: " + name + " not found.");
    }
}
