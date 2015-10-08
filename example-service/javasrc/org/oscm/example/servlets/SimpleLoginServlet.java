package org.oscm.example.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class SimpleLoginServlet extends HttpServlet {

    private static final long serialVersionUID = -7687631972422515007L;

    /**
     * Process a GET or POST request.
     * 
     * @param response
     *            - the HttpServletResponse object
     * 
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the request
     */
    protected void process(HttpServletResponse response) throws IOException {

        response.sendRedirect("./lcm.png");

    }

    /**
     * Called by the server (via the service method) to allow a servlet to
     * handle a GET request.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param response
     *            - the HttpServletResponse object
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the GET request
     * @throws ServletException
     *             - if the request for the GET could not be handled
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(response);
    }

    /**
     * Called by the server (via the service method) to allow a servlet to
     * handle a POST request.
     * 
     * @param request
     *            - the HttpServletRequest object
     * @param response
     *            - the HttpServletResponse object
     * @throws IOException
     *             - if an input or output error is detected when the servlet
     *             handles the POST request
     * @throws ServletException
     *             - if the request for the POST could not be handled
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(response);
    }

}
