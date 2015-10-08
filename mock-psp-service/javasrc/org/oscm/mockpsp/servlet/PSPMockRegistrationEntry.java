package org.oscm.mockpsp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.mockpsp.data.ParameterHandler;
import org.oscm.mockpsp.data.ParameterStorage;

/**
 * Servlet to simply return the redirect URL for the user registration.
 */
public class PSPMockRegistrationEntry extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // not required
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        Map<?, ?> parameterMap = request.getParameterMap();

        // store the parameters
        String sessionId = request.getSession().getId();
        ParameterStorage.addSessionParams(sessionId, parameterMap);

        ParameterHandler.addParametersToWriterInput(writer, parameterMap);
        StringBuffer requestURL = request.getRequestURL();
        int pos = requestURL.lastIndexOf("/");
        String redirectURL = requestURL.substring(0, pos + 1)
                + "PSPMockService?sessionId=" + sessionId;
        redirectURL = URLEncoder.encode(redirectURL, "UTF-8");
        writer.printf("%s=%s", "FRONTEND.REDIRECT_URL", redirectURL);

    }

}
