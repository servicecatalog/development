/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 03.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kulle
 * 
 */
public class UrlRewriteFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String requestUri = getFullUrl(request);

        if (!isUrlRewritten(request) && containsAction(requestUri)) {
            request.getSession().setAttribute("rewritten", "true");
            String action = readAction(requestUri);
            String newUri = requestUri
                    .replace("endpoint", "endpoint/" + action);
            response.sendRedirect(newUri);
            return;
        }
        request.getSession().setAttribute("rewritten", "false");
        chain.doFilter(req, res);
    }

    private boolean containsAction(String requestUri) {
        return requestUri.contains("?Action=")
                || requestUri.contains("&Action=");
    }

    private boolean isUrlRewritten(HttpServletRequest request) {
        String rewritten = (String) request.getSession().getAttribute(
                "rewritten");
        boolean endsWithEndpoint = false;
        if (request.getRequestURI().endsWith("/endpoint/")) {
            endsWithEndpoint = true;
        } else if (request.getRequestURI().endsWith("/endpoint")) {
            endsWithEndpoint = true;
        }
        return "true".equals(rewritten) || !endsWithEndpoint;
    }

    private String getFullUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        if (request.getQueryString() != null) {
            url.append('?');
            url.append(request.getQueryString());
        }
        return url.toString();
    }

    private String readAction(String requestUri) {
        Pattern pattern = Pattern.compile("Action\\=((?!&|$).)*");
        Matcher matcher = pattern.matcher(requestUri);
        matcher.find();
        String action = matcher.group();
        if (action.endsWith("&")) {
            action = action.replace("&", "");
        }
        return action.replace("Action=", "");
    }

    @Override
    public void destroy() {
    }
}
