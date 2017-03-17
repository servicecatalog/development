/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: Peter Pock                                                     
 *                                                                              
 *  Creation Date: 10.06.2010                                                      
                                      
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.integrationtests.mockproduct.RequestLogEntry.RequestDirection;
import org.oscm.integrationtests.mockproduct.operation.IOperationDescriptor;
import org.oscm.integrationtests.mockproduct.operation.OperationRegistry;

/**
 * Servlet to execute outgoing web services.
 * 
 * @author pock
 * 
 */
public class ExecuteServlet extends HttpServlet {

    private static final long serialVersionUID = -4689541009012302089L;

    private static final String PARAM_PREFIX = "param_";

    /**
     * The method is responsible for the token resolution and for the forwarding
     * of the request to the java server page. It will use the USERTOKEN and ask
     * BES for the USER_ID for the token. After the token has been resolved, the
     * request will be forwarded to list.jsp.
     * 
     * @param request
     *            the HTTP request containing the USERTOKEN
     * @param response
     *            the response object for the current request
     * @throws ServletException
     *             if the forward or the call to BES fails
     * @throws IOException
     *             if the forward fails
     */
    private void process(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        final OperationRegistry registry = (OperationRegistry) getServletContext()
                .getAttribute(InitServlet.OPERATIONREGISTRY);
        final IOperationDescriptor<Object> operation = registry
                .getOperation(request.getParameter("operation"));

        final RequestLog log = (RequestLog) getServletContext().getAttribute(
                InitServlet.REQUESTLOG);
        final RequestLogEntry entry = log.createEntry(operation.getName(),
                RequestDirection.OUTBOUND);

        final Map<String, String> params = new HashMap<String, String>();
        final Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            final String value = request.getParameter(name);
            if (name.startsWith(PARAM_PREFIX)) {
                name = name.substring(PARAM_PREFIX.length());
                entry.addParameter(name, value);
                params.put(name, value);
            }
        }

        final ConnectionInfo connection = ConnectionInfo.get(request);
        entry.setHost(connection.getBaseUrl());
        final Object service = PortFactory.getPort(connection,
                operation.getServiceType());

        try {
            operation.call(service, entry, params);
        } catch (Exception e) {
            entry.setException(e);
        }

        RequestDispatcher rd = request.getRequestDispatcher("requestlog.jsp");
        rd.forward(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

}
