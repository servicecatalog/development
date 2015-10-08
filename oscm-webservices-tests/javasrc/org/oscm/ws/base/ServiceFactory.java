/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 30.05.2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws.base;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.oscm.ct.login.LoginHandlerFactory;
import org.oscm.test.setup.PropertiesReader;
import org.oscm.ws.WSPortConnector;
import org.oscm.internal.intf.OperatorService;
import org.oscm.intf.AccountService;
import org.oscm.intf.BillingService;
import org.oscm.intf.CategorizationService;
import org.oscm.intf.DiscountService;
import org.oscm.intf.EventService;
import org.oscm.intf.IdentityService;
import org.oscm.intf.MarketplaceService;
import org.oscm.intf.OrganizationalUnitService;
import org.oscm.intf.ReportingService;
import org.oscm.intf.ReviewService;
import org.oscm.intf.SamlService;
import org.oscm.intf.SearchService;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.SessionService;
import org.oscm.intf.SubscriptionService;
import org.oscm.intf.TagService;
import org.oscm.intf.TriggerDefinitionService;
import org.oscm.intf.TriggerService;
import org.oscm.intf.VatService;
import com.sun.xml.wss.XWSSConstants;

/**
 * Factory class to retrieve service references in the web service tests.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class ServiceFactory {

    private static final String DEFAULT_USER_PASSWORD = "secret";
    private static final String BASIC_AUTH = "BASIC";
    private static final String STS_AUTH = "STS";
    private static final String CLIENT_CERT = "CLIENTCERT";
    private boolean useCertAuth = false;
    private boolean useSTSAuth = false;
    private final Properties props;

    private static ServiceFactory defaultFactory;

    public ServiceFactory() throws Exception {
        PropertiesReader reader = new PropertiesReader();
        props = reader.load();
        logProperties(props);
        // setup environment
        System.setProperty("javax.net.ssl.keyStore", getDomainHome()
                + "/config/keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStore", getDomainHome()
                + "/config/cacerts.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    public static synchronized ServiceFactory getDefault() throws Exception {
        if (defaultFactory == null) {
            defaultFactory = new ServiceFactory();
            return defaultFactory;
        }
        defaultFactory.setUseCertAuth(false);
        return defaultFactory;
    }

    public static synchronized ServiceFactory getSTSServiceFactory()
            throws Exception {
        if (defaultFactory == null) {
            defaultFactory = new ServiceFactory();
            defaultFactory.setUseSTSAuth(true);
            return defaultFactory;
        }
        defaultFactory.setUseCertAuth(false);
        defaultFactory.setUseSTSAuth(true);
        return defaultFactory;
    }

    private void setUseSTSAuth(boolean useSTSAuth) {
        this.useSTSAuth = useSTSAuth;
    }

    private String getWebServiceBaseUrl() {
        return props.getProperty("bes.https.url");
    }

    private String getDefaultUserName() {
        return props.getProperty("DEFAULT_USER");
    }

    private String getDefaultPassword() {
        return props.getProperty("DEFAULT_PASSWORD");
    }

    public IdentityService getIdentityService() throws Exception {
        return getIdentityService(getDefaultUserName(), getDefaultPassword());
    }

    public IdentityService getIdentityService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService(
                    "IdentityService/" + getAuth() + "?wsdl",
                    IdentityService.class, userName, password);
        }
        return connectToWebService("IdentityService/" + getAuth() + "?wsdl",
                "/IdentityService.wsdl", IdentityService.class, userName,
                password);
    }

    public SearchService getSearchService() throws Exception {
        return getSearchService(getDefaultUserName(), getDefaultPassword());
    }

    public SearchService getSearchService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("SearchService/" + getAuth() + "?wsdl",
                    SearchService.class, userName, password);
        }
        return connectToWebService("SearchService/" + getAuth() + "?wsdl",
                "/SearchService.wsdl", SearchService.class, userName, password);
    }

    public ServiceProvisioningService getServiceProvisioningService()
            throws Exception {
        return getServiceProvisioningService(getDefaultUserName(),
                getDefaultPassword());
    }

    public ServiceProvisioningService getServiceProvisioningService(
            String userName, String password) throws Exception {
        if (useSTSAuth) {
            return connectToWebService("ServiceProvisioningService/"
                    + getAuth() + "?wsdl", ServiceProvisioningService.class,
                    userName, password);
        }
        return connectToWebService("ServiceProvisioningService/" + getAuth()
                        + "?wsdl", "/ServiceProvisioningService.wsdl",
                ServiceProvisioningService.class, userName, password);
    }

    public ReportingService getReportingService() throws Exception {
        return getReportingService(getDefaultUserName(), getDefaultPassword());
    }

    public ReportingService getReportingService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("ReportingService/" + getAuth()
                    + "?wsdl", ReportingService.class, userName, password);
        }
        return connectToWebService("ReportingService/" + getAuth() + "?wsdl",
                "/ReportingService.wsdl", ReportingService.class, userName,
                password);
    }

    public MarketplaceService getMarketPlaceService(String userName,
            String password) throws Exception {
        if (useSTSAuth) {
            return connectToWebService("MarketplaceService/" + getAuth()
                    + "?wsdl", MarketplaceService.class, userName, password);
        }
        return connectToWebService("MarketplaceService/" + getAuth() + "?wsdl",
                "/MarketplaceService.wsdl", MarketplaceService.class, userName,
                password);
    }

    public ReviewService getReviewService() throws Exception {
        return getReviewService(getDefaultUserName(), getDefaultPassword());
    }

    public ReviewService getReviewService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("ReviewService/" + getAuth() + "?wsdl",
                    ReviewService.class, userName, password);
        }
        return connectToWebService("ReviewService/" + getAuth() + "?wsdl",
                "/ReviewService.wsdl", ReviewService.class, userName, password);
    }

    public SubscriptionService getSubscriptionService() throws Exception {
        return getSubscriptionService(getDefaultUserName(),
                getDefaultPassword());
    }

    public SubscriptionService getSubscriptionService(String userName)
            throws Exception {
        return getSubscriptionService(userName, DEFAULT_USER_PASSWORD);
    }

    public SubscriptionService getSubscriptionService(String userName,
            String password) throws Exception {
        if (useSTSAuth) {
            return connectToWebService("SubscriptionService/" + getAuth()
                    + "?wsdl", SubscriptionService.class, userName, password);
        }
        return connectToWebService(
                "SubscriptionService/" + getAuth() + "?wsdl",
                "/SubscriptionService.wsdl", SubscriptionService.class,
                userName, password);
    }

    public EventService getEventService() throws Exception {
        return getEventService(getDefaultUserName(), getDefaultPassword());
    }

    public EventService getEventService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("EventService/" + getAuth() + "?wsdl",
                    EventService.class, userName, password);
        }
        return connectToWebService("EventService/" + getAuth() + "?wsdl",
                "/EventService.wsdl", EventService.class, userName, password);
    }

    public AccountService getAccountService() throws Exception {
        return getAccountService(getDefaultUserName(), getDefaultPassword());
    }

    public AccountService getAccountService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("AccountService/" + getAuth() + "?wsdl",
                    AccountService.class, userName, password);
        }
        return connectToWebService("AccountService/" + getAuth() + "?wsdl",
                "/AccountService.wsdl", AccountService.class, userName,
                password);
    }

    public SessionService getSessionService() throws Exception {
        return getSessionService(getDefaultUserName(), getDefaultPassword());
    }

    public VatService getVatService() throws Exception {
        return getVatService(getDefaultUserName(), getDefaultPassword());
    }

    public VatService getVatService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("VatService/" + getAuth() + "?wsdl",
                    VatService.class, userName, password);
        }
        return connectToWebService("VatService/" + getAuth() + "?wsdl",
                "/VatService.wsdl", VatService.class, userName, password);
    }

    public DiscountService getDiscountService() throws Exception {
        return getDiscountService(getDefaultUserName(), getDefaultPassword());
    }

    public DiscountService getDiscountService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService(
                    "DiscountService/" + getAuth() + "?wsdl",
                    DiscountService.class, userName, password);
        }
        return connectToWebService("DiscountService/" + getAuth() + "?wsdl",
                "/DiscountService.wsdl", DiscountService.class, userName,
                password);
    }

    public SessionService getSessionService(String userName, String password)
            throws Exception {

        if (useSTSAuth) {
            return connectToWebService("SessionService/" + getAuth() + "?wsdl",
                    SessionService.class, userName, password);
        }
        return connectToWebService("SessionService/" + getAuth() + "?wsdl",
                "/SessionService.wsdl", SessionService.class, userName,
                password);
    }

    public TagService getTagService() throws Exception {
        return getTagService(getDefaultUserName(), getDefaultPassword());
    }

    public TagService getTagService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("TagService/" + getAuth() + "?wsdl",
                    TagService.class, userName, password);
        }
        return connectToWebService("TagService/" + getAuth() + "?wsdl",
                "/TagService.wsdl", TagService.class, userName, password);
    }

    public CategorizationService getCategorizationService() throws Exception {
        return getCategorizationService(getDefaultUserName(),
                getDefaultPassword());
    }

    public CategorizationService getCategorizationService(String userName,
            String password) throws Exception {
        if (useSTSAuth) {
            return connectToWebService("CategorizationService/" + getAuth()
                    + "?wsdl", CategorizationService.class, userName, password);
        }
        return connectToWebService("CategorizationService/" + getAuth()
                        + "?wsdl", "/CategorizationService.wsdl",
                CategorizationService.class, userName, password);
    }

    private <T> T connectToWebService(String urlSuffix, String wsdlFile,
            Class<T> remoteInterface, String userName, String password)
            throws Exception {
        String wsdlUrl = getWebServiceBaseUrl() + "/" + urlSuffix;
        WSPortConnector wsPortConnector = new WSPortConnector(wsdlUrl,
                userName, password);
        URL resourceUrl = ServiceFactory.class.getResource(wsdlFile);
        return wsPortConnector.getPort(resourceUrl, remoteInterface);
    }

    private <T> T connectToWebService(String urlSuffix,
            Class<T> remoteInterface, String userName, String password)
            throws Exception {
        String wsdlUrl = getWebServiceBaseUrl() + "/" + urlSuffix;
        URL url = new URL(wsdlUrl);
        QName qName = new QName("http://oscm.org/xsd",
                remoteInterface.getSimpleName());
        Service service = Service.create(url, qName);
        T port = service.getPort(remoteInterface);
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> clientRequestContext = bindingProvider
                .getRequestContext();
        clientRequestContext.put(XWSSConstants.USERNAME_PROPERTY, userName);
        clientRequestContext.put(XWSSConstants.PASSWORD_PROPERTY, password);
        return port;
    }

    public OperatorService getOperatorService() throws Exception {
        return getOperatorService(getDefaultUserName(), getDefaultPassword());
    }

    public OperatorService getOperatorService(String userName, String password)
            throws Exception {
        return connectToEJB(OperatorService.class, userName, password);
    }

    public TriggerDefinitionService getTriggerDefinitionService()
            throws Exception {
        return getTriggerDefinitionService(getDefaultUserName(),
                getDefaultPassword());
    }

    public TriggerDefinitionService getTriggerDefinitionService(
            String userName, String password) throws Exception {
        if (useSTSAuth) {
            return connectToWebService("TriggerDefinitionService/" + getAuth()
                    + "?wsdl", TriggerDefinitionService.class, userName,
                    password);
        }
        return connectToWebService("TriggerDefinitionService/" + getAuth()
                        + "?wsdl", "/TriggerDefinitionService.wsdl",
                TriggerDefinitionService.class, userName, password);
    }

    public TriggerService getTriggerService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("TriggerService/" + getAuth() + "?wsdl",
                    TriggerService.class, userName, password);
        }
        return connectToWebService("TriggerService/" + getAuth() + "?wsdl",
                "/TriggerService.wsdl", TriggerService.class, userName,
                password);
    }

    public OrganizationalUnitService getOrganizationalUnitService(
            String userName, String password) throws Exception {
        if (useSTSAuth) {
            return connectToWebService("OrganizationalUnitService/" + getAuth()
                    + "?wsdl", OrganizationalUnitService.class, userName, password);
        }
        return connectToWebService("OrganizationalUnitService/" + getAuth()
                + "?wsdl", "/OrganizationalUnitService.wsdl",
                OrganizationalUnitService.class, userName, password);
    }
    
    public OrganizationalUnitService getOrganizationalUnitService() throws Exception {
        return getOrganizationalUnitService(getDefaultUserName(), getDefaultPassword());
    }

    public BillingService getBillingService() throws Exception {
        return getBillingService(getDefaultUserName(), getDefaultPassword());
    }

    public BillingService getBillingService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("BillingService/" + getAuth() + "?wsdl",
                    BillingService.class, userName, password);
        }
        return connectToWebService("BillingService/" + getAuth() + "?wsdl",
                "/BillingService.wsdl", BillingService.class, userName,
                password);
    }

    public SamlService getSamlService() throws Exception {
        return getSamlService(getDefaultUserName(), getDefaultPassword());
    }

    public SamlService getSamlService(String userName, String password)
            throws Exception {
        if (useSTSAuth) {
            return connectToWebService("SamlService/" + getAuth() + "?wsdl",
                    SamlService.class, userName, password);
        }
        return connectToWebService("SamlService/" + getAuth() + "?wsdl",
                "/SamlService.wsdl", SamlService.class, userName, password);
    }

    private <T> T connectToEJB(Class<T> remoteInterface, String userName,
            String password) throws Exception {
        InitialContext initialContext = new InitialContext(props);
        String configurl = ServiceFactory.class.getResource(
                "/glassfish-login.conf").toString();
        System.setProperty("java.security.auth.login.config", configurl);
        LoginHandlerFactory.getInstance().login(userName, password);
        @SuppressWarnings("unchecked")
        T service = (T) initialContext.lookup(remoteInterface.getName());
        return service;
    }

    private String getAuth() {
        if (useCertAuth) {
            return CLIENT_CERT;
        }
        if (useSTSAuth) {
            return STS_AUTH;
        }
        return BASIC_AUTH;
    }

    public boolean isUseCertAuth() {
        return useCertAuth;
    }

    public void setUseCertAuth(boolean useCertAuth) {
        this.useCertAuth = useCertAuth;
    }

    private String getDomainHome() {
        return props.getProperty("glassfish.bes.domain");
    }

    public static void logProperties(Properties properties) {
        System.out
                .println("Starting WebService test with the following properties:");
        for (Object key : properties.keySet()) {
            System.out.println("\t" + key + "="
                    + properties.getProperty((String) key));
        }
        System.out.println();
        System.out
                .println("If the environment specific properties are wrong, especially bes.https.url and glassfish.bes.domain");
        System.out
                .println("\tplease override them in oscm-devruntime/javares/local/<hostname>/test.properties !");
        System.out.println();
    }

}
