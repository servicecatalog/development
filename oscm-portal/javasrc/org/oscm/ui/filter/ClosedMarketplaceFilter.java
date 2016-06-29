/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: Jun 01, 2016
 *
 *******************************************************************************/

package org.oscm.ui.filter;

import java.io.IOException;

import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.types.constants.marketplace.Marketplace;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.*;

/**
 * @author Paulina Badziak
 *
 */
public class ClosedMarketplaceFilter implements Filter {

    RequestRedirector redirector;
    String excludeUrlPattern;
    MarketplaceService marketplaceService;
    IdentityService identityService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        redirector = new RequestRedirector(filterConfig);
        excludeUrlPattern = filterConfig
                .getInitParameter("exclude-url-pattern");

        ServiceAccess serviceAccess = new EJBServiceAccess();
        marketplaceService = serviceAccess.getService(MarketplaceService.class);
        identityService = serviceAccess.getService(IdentityService.class);
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
            String mId = (String) httpRequest.getSession()
                    .getAttribute(Constants.REQ_PARAM_MARKETPLACE_ID);

            if (mId == null || mId.equals("")) {
                chain.doFilter(request, response);
                return;
            }

            try {
                VOMarketplace voMarketplace = marketplaceService
                        .getMarketplaceById(mId);
                if (voMarketplace.isRestricted()) {
                    VOUserDetails voUserDetails = identityService
                            .getCurrentUserDetailsIfPresent();
                    if (voUserDetails != null
                            && voUserDetails.getUserId() != null) {
                        if (!marketplaceService
                                .doesOrganizationHaveAccessMarketplace(mId,
                                        voUserDetails.getOrganizationId())) {
                            redirector.forward(httpRequest, httpResponse,
                                    Marketplace.MARKETPLACE_ROOT
                                            + Constants.INSUFFICIENT_AUTHORITIES_URI);
                            return;
                        } else {
                            chain.doFilter(request, response);
                            return;
                        }
                    }
                    redirector.forward(httpRequest, httpResponse,
                            BaseBean.MARKETPLACE_START_SITE);
                    return;
                }

            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
