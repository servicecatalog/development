package org.oscm.mockpsp.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to simply return the redirect URL for the user registration.
 */
public class PSPMockDeregistration extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // not required
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        String s = "<Response><Transaction><Processing><Result>ACK</Result><Return>12345</Return></Processing></Transaction></Response>";
        writer.print(s);
    }

}
