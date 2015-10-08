/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: Peter Pock                                                     
 *                                                                              
 *  Creation Date: 10.06.2010                                                      
                                      
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet save the connection settings
 * 
 * @author hoffmann
 * 
 */
public class SaveSettingsServlet extends HttpServlet {

    private static final long serialVersionUID = -4689541009012302089L;

    private void process(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ConnectionInfo info = ConnectionInfo.get(request);
        info.setBaseUrl(request.getParameter("baseUrl") + "/");
        info.setUsername(request.getParameter("username"));
        info.setPassword(request.getParameter("password"));
        final String auth = request.getParameter("authentication");
        info.setClientCert("CLIENTCERT".equals(auth));

        RequestDispatcher rd = request.getRequestDispatcher("settings.jsp");
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
