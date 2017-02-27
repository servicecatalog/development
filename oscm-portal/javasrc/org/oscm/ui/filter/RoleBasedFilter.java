/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.ui.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.constants.marketplace.Marketplace;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.common.Constants;
import org.oscm.ui.common.JSFUtils;
import com.google.common.collect.Sets;
import com.sun.enterprise.security.web.integration.WebPrincipal;

/**
 * Created by Marcin Maciaszczyk on 2015-09-11.
 */
public class RoleBasedFilter extends BaseBesFilter {

    private static final String CONFIG_FILE_LOCATION = "/WEB-INF/role-based-filter-config.xml";
    private static final Log4jLogger logger = LoggerFactory
            .getLogger(RoleBasedFilter.class);
    private RoleBasedFilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        loadConfig(filterConfig.getServletContext());
    }

    private void loadConfig(ServletContext servletContext) {
        try {
            final JAXBContext context = JAXBContext
                    .newInstance(RoleBasedFilterConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource source = new StreamSource(
                    servletContext.getResourceAsStream(CONFIG_FILE_LOCATION));
            config = unmarshaller.unmarshal(source, RoleBasedFilterConfig.class)
                    .getValue();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
                    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        Set<String> principalRoles = getPrincipalRoles(httpRequest);
        for (RoleBasedFilterConfigEntry entry : config.getEntries()) {
            if (httpRequest.getRequestURI().endsWith(entry.getPage())
                    && !isPrincipalRoleAllowed(principalRoles,
                            entry.getRolesAllowed())) {
                logger.logInfo(Log4jLogger.ACCESS_LOG,
                        LogMessageIdentifier.INFO_INSUFFICIENT_ROLE,
                        principalRoles.toString(), entry.getPage(),
                        entry.getRolesAllowed().toString());
                JSFUtils.sendRedirect((HttpServletResponse) servletResponse,
                        httpRequest.getContextPath()
                                + Marketplace.MARKETPLACE_ROOT
                                + Constants.INSUFFICIENT_AUTHORITIES_URI);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isPrincipalRoleAllowed(Set<String> principalRoles,
            Set<String> rolesAllowed) {
        return !Sets.intersection(principalRoles, rolesAllowed).isEmpty();
    }

    private Set<String> getPrincipalRoles(HttpServletRequest httpRequest) {
        WebPrincipal webPrincipal = (WebPrincipal) httpRequest
                .getUserPrincipal();
        Set<String> principalRoles = new HashSet<>();
        if (webPrincipal != null) {
            Subject subject = webPrincipal.getSecurityContext().getSubject();
            for (Principal principal : subject.getPrincipals()) {
                principalRoles.add(principal.getName());
            }
        }
        return principalRoles;
    }

}
