/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.ui.common;

/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Author: pock                                                      
 *                                                                              
 *  Creation Date: 10.12.2010                                                      
 *                                                                              
 *******************************************************************************/

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.naming.CommunicationException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.constants.Configuration;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ws.WSPortConnector;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.internal.vo.VOUser;
import org.oscm.intf.AccountService;
import org.oscm.intf.BillingService;
import org.oscm.intf.EventService;
import org.oscm.intf.IdentityService;
import org.oscm.intf.ReportingService;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.SessionService;
import org.oscm.intf.SubscriptionService;
import org.oscm.intf.TriggerService;
import org.oscm.intf.VatService;

/**
 * Implementation to access the services via JAX-WS
 */
public class CustomServiceAccess extends ServiceAccess {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(CustomServiceAccess.class);

    private static String OPERATOR_USR = "1000";
    private static String OPERATOR_PWD = "admin123";

    private final static List<Class<?>> webServices = Arrays
            .asList(new Class<?>[] { AccountService.class,
                    BillingService.class, EventService.class,
                    IdentityService.class, ReportingService.class,
                    ServiceProvisioningService.class, SessionService.class,
                    SubscriptionService.class, TriggerService.class,
                    VatService.class });

    private EJBServiceAccess ejbServiceAccess = new EJBServiceAccess();

    private String user;
    private String pwd;
    private String wsBaseUrl = "https://localhost:8181/";

    protected CustomServiceAccess() {
        ConfigurationService service = ejbServiceAccess
                .getService(ConfigurationService.class);
        String baseUrl = service.getVOConfigurationSetting(
                ConfigurationKey.BASE_URL, Configuration.GLOBAL_CONTEXT)
                .getValue();
        if (baseUrl == null || baseUrl.length() == 0) {
            baseUrl = service.getVOConfigurationSetting(
                    ConfigurationKey.BASE_URL_HTTPS,
                    Configuration.GLOBAL_CONTEXT).getValue();
        }
        try {
            URL url = new URL(baseUrl);
            wsBaseUrl = url.getProtocol() + "://" + url.getHost() + ":"
                    + url.getPort() + "/";
        } catch (MalformedURLException e) {
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    e,
                    LogMessageIdentifier.ERROR_ACCESS_CUSTOM_SERVICE_FAILED_WITH_MALFORMED_URL);
        }
    }

    private <T> T getPort(Class<T> clazz, String user, String pwd) {
        if (user == null && pwd == null) {
            user = OPERATOR_USR;
            pwd = OPERATOR_PWD;
        }

        try {
            WSPortConnector connector;
            String wsdlLocation = wsBaseUrl + clazz.getSimpleName()
                    + "/v1.3/BASIC?wsdl";
            connector = new WSPortConnector(wsdlLocation, user, pwd);
            return connector.getPort(new URL(wsdlLocation), clazz);
        } catch (Exception e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Web service lookup failed!", e);
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_LOOKUP_WEB_SERVICE_FAILED);
            throw se;
        }
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        if (webServices.contains(clazz)) {
            return getPort(clazz, user, pwd);
        }
        return ejbServiceAccess.getService(clazz);
    }

    @Override
    protected void doLogin(VOUser userObject, String password,
            HttpServletRequest request, HttpServletResponse response)
            throws CommunicationException, LoginException {

        user = String.valueOf(userObject.getKey());
        pwd = password;

        // we must also perform the programatic login because some services are
        // not deployed as web services
        ejbServiceAccess.login(userObject, password, request, response);
    }

    @Override
    protected boolean createSession() {
        // because ejbServiceAccess.login is called. Create session is created
        // implicitly.
        return false;
    }
}
