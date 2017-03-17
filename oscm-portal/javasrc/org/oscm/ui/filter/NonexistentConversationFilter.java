/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.ui.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.weld.context.NonexistentConversationException;
import org.oscm.ui.common.Constants;
import org.oscm.ui.dialog.mp.subscriptionDetails.SubscriptionDetailsCtrlConstants;


/**
 * This Filter is used to handling NonexistentConversationException in subscription process
 * in situation when conversation is lost and user is trying to continue subscription process.
 *
 */
public class NonexistentConversationFilter extends BaseBesFilter {

	@Override
	public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception t) {
            if (t.getCause() instanceof NonexistentConversationException) {
                //Refresh from subscription creation and upgrade
                String requestURI = request.getRequestURI();
                if (requestURI.contains("/marketplace/subscriptions/upgrade/confirmUpgrade.jsf") ||
                        requestURI.contains("/marketplace/subscriptions/creation/confirmAdd.jsf")){
                    sendRedirect(request, response,
                            "/marketplace/account/subscriptionDetails.jsf");
                } else {
                    request.setAttribute(Constants.REQ_ATTR_ERROR_KEY,
                            SubscriptionDetailsCtrlConstants.ERROR_SUBSCRIPTION_REPEATSTEPS);
                    sendRedirect(request, response, "/marketplace/index.jsf");
                }
            } else {
        		throw t;
        	}
        }
    }

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
