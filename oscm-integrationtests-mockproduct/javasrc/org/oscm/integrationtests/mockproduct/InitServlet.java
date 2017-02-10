/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.oscm.integrationtests.mockproduct.operation.OperationRegistry;

/**
 * The only purpose of this servlet is to populate the context with the global
 * objects.
 * 
 * @author hoffmann
 */
public class InitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String REQUESTLOG = "requestlog";

    public static final String OPERATIONREGISTRY = "operationregistry";

    @Override
    public void init(ServletConfig config) throws ServletException {
        final ServletContext ctx = config.getServletContext();
        ctx.setAttribute(REQUESTLOG, new RequestLog());
        ctx.setAttribute(OPERATIONREGISTRY, new OperationRegistry());
    }

}
