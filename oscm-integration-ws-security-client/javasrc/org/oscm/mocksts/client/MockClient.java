/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 07.06.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.mocksts.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceRef;

import org.apache.commons.lang3.StringEscapeUtils;

import org.oscm.mocksts.ws.MockWebService;
import org.oscm.mocksts.ws.MockWebService_Service;
import org.oscm.xsd.v1.ClientSoapHandlerService;
import org.oscm.xsd.v1.ClientSoapHandlerService_Service;
import org.oscm.xsd.v1.STSSoapHandlerService;
import org.oscm.xsd.v1.STSSoapHandlerService_Service;
import org.oscm.xsd.v1.ServerSoapHandlerService;
import org.oscm.xsd.v1.SoapHandlerService;

/**
 * Servlet implementation class MockClient
 * 
 * @author gao
 */
@WebServlet("/MockClient")
public class MockClient extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @WebServiceRef
    private MockWebService_Service service;

    @WebServiceRef
    private ClientSoapHandlerService_Service clientSoapService;

    @WebServiceRef
    private ServerSoapHandlerService serverSoapHandler;

    @WebServiceRef
    private STSSoapHandlerService_Service stsSoapHandler;

    private SoapHandlerService serverSoapHandlerPort;

    private ClientSoapHandlerService clientSoapHandlerPort;

    private STSSoapHandlerService stsSoapHandlerPort;

    private MockWebService port;

    public static List<SOAPMessage> megs = new ArrayList<SOAPMessage>();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        initMessageList();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body>");
        out.print("<h1> Web Service call was successfull.</h1>");
        String webserviceResult = "";
        try {
            webserviceResult = callSecuredWebservice("");
        } catch (Exception e) {
            webserviceResult = "exception was thrown";
        }
        out.print("<h2 style=\"margin: 0px 0px 30px 0px\">1. Result: "
                + webserviceResult + "</h2>");
        out.print("<h2>2. Intercepted SOAP messages from ws-client, security token service and the ws-server:</h2>");
        printServerSoapMessages(out);
        printSTSSoapMessages(out);
        printClientSoapMessages(out);
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    /**
     * clean the message list of soap handlers
     */
    private void initMessageList() {
        serverSoapHandlerPort = serverSoapHandler
                .getServerSoapHandlerServiceImplPort();
        clientSoapHandlerPort = clientSoapService
                .getClientSoapHandlerServiceImplPort();
        stsSoapHandlerPort = stsSoapHandler.getSTSSoapHandlerServiceImplPort();
        serverSoapHandlerPort.initMessageList();
        clientSoapHandlerPort.initMessageList();
        stsSoapHandlerPort.initMessageList();
    }

    /**
     * invoke the Mock-STS-ServiceProvider web service method
     * 
     * @param clientName
     *            the client name
     * @return execute result
     */
    private String callSecuredWebservice(java.lang.String clientName) {
        port = service.getMockWebServicePort();
        return port.hello(clientName);
    }

    private List<String> getClientSoapMessages() {
        List<String> messageList = clientSoapHandlerPort.getCollectedMessages();
        return messageList;
    }

    private List<String> getServerSoapMessages() {
        List<String> messageList = serverSoapHandlerPort.getCollectedMessages();
        return messageList;
    }

    private List<String> getSTSSoapMessages() {
        List<String> messageList = stsSoapHandlerPort.getCollectedMessages();
        return messageList;
    }

    private void printClientSoapMessages(PrintWriter out) {
        out.println("<h3 style=\"margin: 30px 0px 0px 0px\">2.3 ws-client messages:</h3>");
        List<String> messages = getClientSoapMessages();
        printSoapMessages(out, messages);
    }

    private void printServerSoapMessages(PrintWriter out) {
        out.print("<h3 style=\"margin: 30px 0px 0px 0px\">2.1 ws-server messages:</h3>");
        List<String> messages = getServerSoapMessages();
        printSoapMessages(out, messages);
    }

    private void printSTSSoapMessages(PrintWriter out) {
        out.println("<h3 style=\"margin: 30px 0px 0px 0px\">2.2 security token service messages:</h3>");
        List<String> messages = getSTSSoapMessages();
        printSoapMessages(out, messages);
    }

    private void printSoapMessages(PrintWriter out, List<String> messages) {
        if (messages != null && messages.size() > 0) {
            for (int i = 0; i < messages.size(); i++) {
                String msg = messages.get(i);
                out.print("<p><b>message " + i + " is:</b></p>");
                out.print("<pre><code>");
                out.print(StringEscapeUtils.escapeXml(msg));
                out.print("</code></pre>");
            }
        }
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MockClient() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

}
