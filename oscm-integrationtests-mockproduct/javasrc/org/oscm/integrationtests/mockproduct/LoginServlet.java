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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.integrationtests.mockproduct.RequestLogEntry.RequestDirection;

/**
 * Servlet to record the login request.
 * 
 * @author pock
 * 
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = -4689541009012302089L;

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

        final RequestLog log = (RequestLog) getServletContext().getAttribute(
                InitServlet.REQUESTLOG);
        final RequestLogEntry entry = log.createEntry("HTTPRequest.login",
                RequestDirection.INBOUND);
        entry.setHost(request.getRemoteHost());

        final Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            final String name = (String) names.nextElement();
            entry.addParameter(name, request.getParameter(name));
        }

        entry.addQuickLink("resolve", "SessionService.resolveUserToken");
        entry.addQuickLink("event", "EventService.recordEventForSubscription");
        entry.addQuickLink("logout", "SessionService.deleteServiceSession");

        RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
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
