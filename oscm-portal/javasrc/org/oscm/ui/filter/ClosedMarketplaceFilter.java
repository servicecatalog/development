/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: Jun 01, 2016
 *
 *******************************************************************************/

package org.oscm.ui.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.internal.vo.VOUserDetails;
import org.oscm.types.constants.marketplace.Marketplace;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.MarketplaceConfigurationBean;
import org.oscm.ui.common.Constants;
import org.oscm.ui.model.MarketplaceConfiguration;

/**
 * @author Paulina Badziak
 *
 */
public class ClosedMarketplaceFilter implements Filter {

    RequestRedirector redirector;
    String excludeUrlPattern;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        redirector = new RequestRedirector(filterConfig);
        excludeUrlPattern = filterConfig
                .getInitParameter("exclude-url-pattern");
    }

    /**
     * If the request does not match exclude pattern, it is checked in the
     * context of restricted marketplace. If requested marketplace is
     * restricted, current user is checked if he has access to it. If not the
     * request is forwarded to the page informing about insufficient rights. See
     * web.xml for excluded url pattern.
     *
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!httpRequest.getServletPath().matches(excludeUrlPattern)) {
            String mId = httpRequest
                    .getParameter(Constants.REQ_PARAM_MARKETPLACE_ID);
            if (mId == null || mId.equals("")) {
                mId = (String) httpRequest.getSession().getAttribute(
                        Constants.REQ_PARAM_MARKETPLACE_ID);
            }

            MarketplaceConfigurationBean configBean = (MarketplaceConfigurationBean) httpRequest
                    .getSession().getAttribute("marketplaceConfigurationBean");

            VOUserDetails voUserDetails = (VOUserDetails) httpRequest
                    .getSession().getAttribute(Constants.SESS_ATTR_USER);

            if (mId == null || mId.equals("") || configBean == null) {
                chain.doFilter(request, response);
                return;
            }

            MarketplaceConfiguration config = configBean.getConfiguration(mId,
                    httpRequest);

            if (config.isRestricted()) {
                if (voUserDetails != null
                        && voUserDetails.getOrganizationId() != null) {
                    if (!config.getAllowedOrganizations().contains(
                            voUserDetails.getOrganizationId())) {
                        redirector
                                .forward(
                                        httpRequest,
                                        httpResponse,
                                        Marketplace.MARKETPLACE_ROOT
                                                + Constants.INSUFFICIENT_AUTHORITIES_URI);
                        return;
                    } else {
                        chain.doFilter(request, response);
                        return;
                    }
                }
                if (config.hasLandingPage()) {
                    redirector.forward(httpRequest, httpResponse,
                            BaseBean.MARKETPLACE_START_SITE);
                    return;
                }
            }

        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
