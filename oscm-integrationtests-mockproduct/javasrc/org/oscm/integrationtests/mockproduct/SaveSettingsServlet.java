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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.integrationtests.mockproduct.PropertyLoader;

/**
 * Servlet save the connection settings
 * 
 * @author hoffmann
 * 
 */
public class SaveSettingsServlet extends HttpServlet {

    private static final long serialVersionUID = -4689541009012302089L;

    private static final String COMMON_PROPERTIES_PATH = "common.properties";
    private static final String AUTH_MODE = "authMode";

    private void process(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ConnectionInfo info = ConnectionInfo.get(request);
        String baseUrl = request.getParameter("baseUrl");
        info.setBaseUrl(baseUrl + (baseUrl.endsWith("/") == true ? "" : "/"));
        info.setUsername(request.getParameter("username"));
        info.setPassword(request.getParameter("password"));
        info.setAuthMode(PropertyLoader.getInstance()
                .load(COMMON_PROPERTIES_PATH).getProperty(AUTH_MODE));

        final String auth = request.getParameter("authentication");
        info.setClientCert("CLIENTCERT".equals(auth));

        RequestDispatcher rd = request.getRequestDispatcher("settings.jsp");
        rd.forward(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

}
