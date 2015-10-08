/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Feb 9, 2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.apiversioning.soapmgmt.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.oscm.apiversioning.adapter.base.SOAPMessageContextStub;

/**
 * @author zhaoh.fnst
 * 
 */
public class SoapRequestParserTest {
    private SOAPMessageContextStub context;
    private SOAPMessage message;

    private static final String MESSAGEBODYTAG = "S:Body";
    private static final int OPERATIONNAMEINDEX = 4;

    @Before
    public void setUp() throws Exception {
        context = new SOAPMessageContextStub();
        message = mock(SOAPMessage.class);
        context.setMessage(message);
    }

    @Test
    public void parseApiVersion_empty() throws Exception {
        // given
        SOAPPart part = mock(SOAPPart.class);
        SOAPEnvelope envelope = mock(SOAPEnvelope.class);
        doReturn(envelope).when(part).getEnvelope();
        doReturn(part).when(message).getSOAPPart();
        // when
        String result = SoapRequestParser.parseApiVersion(context);

        // then
        assertEquals("", result);
    }

    @Test
    public void parseApiVersion_1_8() throws Exception {
        // given
        SOAPPart part = mock(SOAPPart.class);
        SOAPEnvelope envelope = mock(SOAPEnvelope.class);
        SOAPHeader soapHeader = mock(SOAPHeader.class);
        List<Node> version = new ArrayList<Node>();
        Node node = mock(Node.class);
        doReturn("testVersion").when(node).getValue();
        version.add(node);
        Iterator<?> it = version.iterator();
        doReturn(it).when(soapHeader).extractHeaderElements(
                eq("ctmg.service.version"));
        doReturn(soapHeader).when(envelope).getHeader();
        doReturn(envelope).when(part).getEnvelope();
        doReturn(part).when(message).getSOAPPart();
        // when
        String result = SoapRequestParser.parseApiVersion(context);

        // then
        assertEquals("testVersion", result);
    }

    @Test
    public void parseOperationName() throws Exception {
        // given
        String operationName = "operation1";
        SOAPBody body = mock(SOAPBody.class);
        Document doc = mock(Document.class);
        NodeList list = mock(NodeList.class);
        Node node = mock(Node.class);
        NodeList l1 = mock(NodeList.class);
        Node n1 = mock(Node.class);
        doReturn(operationName).when(n1).getNodeName();
        doReturn(n1).when(l1).item(0);
        doReturn(l1).when(node).getChildNodes();
        doReturn(node).when(list).item(0);
        doReturn(list).when(doc).getElementsByTagName(eq(MESSAGEBODYTAG));
        doReturn(doc).when(body).getOwnerDocument();
        doReturn(body).when(message).getSOAPBody();

        // when
        String result = SoapRequestParser.parseOperationName(context);

        // then
        assertEquals(operationName.substring(OPERATIONNAMEINDEX), result);
    }

    @Test
    public void parseServiceName() throws Exception {
        // given
        String serviceName = "userService";
        context.put(MessageContext.WSDL_SERVICE, serviceName);
        // when
        String result = SoapRequestParser.parseServiceName(context);

        // then
        assertEquals(serviceName, result);
    }
}
