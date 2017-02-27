/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 30.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.log;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * @author kulle
 * 
 */
@WebServlet("/viewlogfile/clearlog")
public class ClearLogServlet extends HttpServlet {

    private static final long serialVersionUID = -34077564546438566L;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        response.setContentType(MediaType.TEXT_HTML);
        try (PrintWriter writer = response.getWriter();) {
            LogFileHandler logfileHandler = new LogFileHandler();
            logfileHandler.clearLogFile(writer);
        }
    }

}
