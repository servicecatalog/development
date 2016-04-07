/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: pock                                                      
 *                                                                              
 *  Creation Date: 18.02.2009                                                      
 *                                                                              
 *  Completion Time: 30.05.2011
 *                                                                              
 *******************************************************************************/
package org.oscm.ui.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.oscm.converter.XMLConverter;
import org.oscm.internal.intf.AccountService;
import org.oscm.internal.intf.BrandService;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.intf.ReportingService;
import org.oscm.internal.intf.SearchServiceInternal;
import org.oscm.internal.intf.ServiceProvisioningService;
import org.oscm.internal.intf.ServiceProvisioningServiceInternal;
import org.oscm.internal.intf.SessionService;
import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.intf.TriggerDefinitionService;
import org.oscm.internal.intf.TriggerService;
import org.oscm.internal.intf.VatService;
import org.oscm.internal.review.POServiceReview;
import org.oscm.internal.review.ReviewInternalService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.EventType;
import org.oscm.internal.types.enumtypes.ImageType;
import org.oscm.internal.types.enumtypes.OperationStatus;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ParameterType;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.types.enumtypes.PaymentCollectionType;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.types.enumtypes.PriceModelType;
import org.oscm.internal.types.enumtypes.ReportType;
import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.TriggerProcessParameterType;
import org.oscm.internal.types.enumtypes.TriggerProcessStatus;
import org.oscm.internal.types.enumtypes.TriggerTargetType;
import org.oscm.internal.types.enumtypes.TriggerType;
import org.oscm.internal.types.enumtypes.UserAccountStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.BulkUserImportException;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.CurrencyException;
import org.oscm.internal.types.exception.DeletionConstraintException;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.MandatoryUdaMissingException;
import org.oscm.internal.types.exception.MarketingPermissionNotFoundException;
import org.oscm.internal.types.exception.MarketplaceAccessTypeUneligibleForOperationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OperationStateException;
import org.oscm.internal.types.exception.OrganizationAlreadyBannedException;
import org.oscm.internal.types.exception.OrganizationAlreadyExistsException;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.types.exception.OrganizationAuthorityException;
import org.oscm.internal.types.exception.PaymentDataException;
import org.oscm.internal.types.exception.PaymentDeregistrationException;
import org.oscm.internal.types.exception.PaymentInformationException;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.internal.types.exception.ServiceOperationException;
import org.oscm.internal.types.exception.ServiceParameterException;
import org.oscm.internal.types.exception.ServiceStateException;
import org.oscm.internal.types.exception.SubscriptionStateException;
import org.oscm.internal.types.exception.TechnicalServiceActiveException;
import org.oscm.internal.types.exception.TechnicalServiceNotAliveException;
import org.oscm.internal.types.exception.TechnicalServiceOperationException;
import org.oscm.internal.types.exception.TriggerDefinitionDataException;
import org.oscm.internal.types.exception.TriggerProcessStatusException;
import org.oscm.internal.types.exception.UpdateConstraintException;
import org.oscm.internal.types.exception.UserActiveException;
import org.oscm.internal.types.exception.UserModificationConstraintException;
import org.oscm.internal.types.exception.UserRoleAssignmentException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.vo.LdapProperties;
import org.oscm.internal.vo.ListCriteria;
import org.oscm.internal.vo.VOBillingContact;
import org.oscm.internal.vo.VOCatalogEntry;
import org.oscm.internal.vo.VOCompatibleService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOCountryVatRate;
import org.oscm.internal.vo.VOCustomerService;
import org.oscm.internal.vo.VOEventDefinition;
import org.oscm.internal.vo.VOImageResource;
import org.oscm.internal.vo.VOInstanceInfo;
import org.oscm.internal.vo.VOLocalizedText;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOOrganizationPaymentConfiguration;
import org.oscm.internal.vo.VOOrganizationVatRate;
import org.oscm.internal.vo.VOParameter;
import org.oscm.internal.vo.VOParameterDefinition;
import org.oscm.internal.vo.VOParameterOption;
import org.oscm.internal.vo.VOPaymentInfo;
import org.oscm.internal.vo.VOPaymentType;
import org.oscm.internal.vo.VOPriceModel;
import org.oscm.internal.vo.VOPriceModelLocalization;
import org.oscm.internal.vo.VOPricedEvent;
import org.oscm.internal.vo.VOPricedOption;
import org.oscm.internal.vo.VOPricedParameter;
import org.oscm.internal.vo.VOPricedRole;
import org.oscm.internal.vo.VOReport;
import org.oscm.internal.vo.VORoleDefinition;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceActivation;
import org.oscm.internal.vo.VOServiceDetails;
import org.oscm.internal.vo.VOServiceEntry;
import org.oscm.internal.vo.VOServiceListResult;
import org.oscm.internal.vo.VOServiceLocalization;
import org.oscm.internal.vo.VOServiceOperationParameterValues;
import org.oscm.internal.vo.VOServicePaymentConfiguration;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.internal.vo.VOSubscriptionDetails;
import org.oscm.internal.vo.VOSubscriptionIdAndOrganizations;
import org.oscm.internal.vo.VOTechnicalService;
import org.oscm.internal.vo.VOTechnicalServiceOperation;
import org.oscm.internal.vo.VOTriggerDefinition;
import org.oscm.internal.vo.VOTriggerProcess;
import org.oscm.internal.vo.VOTriggerProcessParameter;
import org.oscm.internal.vo.VOUda;
import org.oscm.internal.vo.VOUdaDefinition;
import org.oscm.internal.vo.VOUsageLicense;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.internal.vo.VOUserSubscription;
import org.oscm.internal.vo.VOVatRate;
import org.oscm.types.constants.Configuration;
import org.oscm.ui.common.Constants;
import org.oscm.ui.model.MockVOParameter;
import org.oscm.ui.model.MockVOParameterDefinition;
import org.oscm.ui.model.MockVOParameterOption;

public class MockService implements IdentityService, SubscriptionService,
        ServiceProvisioningService, AccountService, ConfigurationService,
        SessionService, ReportingService, BrandService, TriggerService,
        TriggerDefinitionService, VatService, SearchServiceInternal,
        MarketplaceService, ServiceProvisioningServiceInternal,
        ReviewInternalService {

    class MockVOUserSubscription extends VOUserSubscription {

        private static final long serialVersionUID = -3727909333182572198L;

        private long key;

        @Override
        public long getKey() {
            return key;
        }

        public void setMockKey(long key) {
            this.key = key;
        }

    }

    class MockVOProductDetails extends VOServiceDetails {

        private static final long serialVersionUID = -3547157561052453540L;

        private long key;

        @Override
        public long getKey() {
            return key;
        }

        public void setMockKey(long key) {
            this.key = key;
        }

    }

    class MockVOTechnicalProduct extends VOTechnicalService {

        private static final long serialVersionUID = -3738909333182572198L;

        private long key;

        @Override
        public long getKey() {
            return key;
        }

        public void setMockKey(long key) {
            this.key = key;
        }

    }

    abstract private class VOFinder<E> {

        public E findById(List<E> list, String id) {
            if (list == null) {
                return null;
            }
            for (E e : list) {
                if (getId(e).equals(id)) {
                    return e;
                }
            }
            return null;
        }

        abstract public String getId(E element);
    }

    private static final String LICENSE = "License Agreement<br/><br/>"
            + "The license terms of (hereinafter called \"licensor\") are applied"
            + " for the concession of the rights of use for the entire or partly"
            + " use of the object code of the software SmartSVN (hereinafter called \"SOFTWARE\")"
            + " to contractors, juristic persons under public law or official fund assets in terms"
            + " of §310 in conjunction with §14 BGB [Civil Code] (hereinafter called \"licensee\")."
            + " Herewith the inclusion of the licensee's own terms and conditions is contradicted,"
            + " unless their validity has explicitly been agreed to.<br/><br/>"
            + "2 Scope of the Rights of Use<br/><br/>"
            + "2.1 The following terms are valid for the assignment and use of the SOFTWARE"
            + " for an unlimited period of time including any documentation and the license"
            + " file (a file that is custom-made for each individual granting of a license,"
            + " the file being necessary for the operation of the SOFTWARE).<br/><br/>"
            + "2.2 They are not valid for additional services such as installation,"
            + " integration, parameterization and customization of the SOFTWARE"
            + " to the licensee's requirements. ";

    private static final String PAYMENT_FREE = "Free of charge.";
    private static final String PAYMENT_MONTHLY = "Monthly fee of 100 EUR per user.";

    private static MockService instance;
    static {
        instance = new MockService();
        instance.init();
    }

    Set<VOPaymentType> availablePaymentTypes = new HashSet<>();

    List<VOService> productList = new ArrayList<>();
    List<VOTechnicalService> techProductList = new ArrayList<>();

    VOOrganization supplier = new VOOrganization();
    List<VOOrganization> organizationList = new ArrayList<>();

    // BE08054: In V1.2 the field supplierId has been removed from class
    // Organization, so we need to store the customers in an extra list
    List<VOOrganization> customers = new ArrayList<>();

    Map<VOOrganization, List<VOUserDetails>> organizationUsersMap = new HashMap<>();
    Map<VOOrganization, List<VOSubscription>> organizationSubscriptionsMap = new HashMap<>();
    Map<VOOrganization, VOPaymentType> organizationPaymentTypeMap = new HashMap<>();

    Map<VOSubscription, List<VOUserDetails>> subscriptionUsersMap = new HashMap<>();
    Map<VOSubscription, VOService> subscriptionProductMap = new HashMap<>();

    VOUserDetails voUserDetails;

    Map<String, Properties> messagePropertiesMap = new HashMap<>();
    List<VOImageResource> imageResources = new ArrayList<>();

    VOVatRate defaultVat;

    VOFinder<VOSubscription> subscriptionFinder = new VOFinder<VOSubscription>() {
        @Override
        public String getId(VOSubscription element) {
            return element.getSubscriptionId();
        }
    };

    VOFinder<VOUserDetails> userFinder = new VOFinder<VOUserDetails>() {
        @Override
        public String getId(VOUserDetails element) {
            return element.getUserId();
        }
    };

    public static MockService getInstance() {
        return instance;
    }

    private MockService() {
    }

    public void copyProperties(Object dest, Object orig) {
        try {
            PropertyUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            throw new SaaSSystemException(e);
        }
    }

    private VOTechnicalService createTechProduct(List<VOTechnicalService> list,
            String id, String description, String license) {

        MockVOTechnicalProduct techProd = new MockVOTechnicalProduct();
        techProd.setMockKey(list.size() + 1);
        techProd.setTechnicalServiceId(id);
        techProd.setTechnicalServiceDescription(description);
        techProd.setLicense(license);

        final Long ZERO = Long.valueOf(0);

        List<VOParameterDefinition> parameterDefs = new ArrayList<>();
        VOParameterDefinition paramDef;
        paramDef = new MockVOParameterDefinition(
                ParameterType.SERVICE_PARAMETER,
                "LONG_PARAM",
                "This is a longer parameter description which shold cause a line wrap."
                        + " Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam"
                        + " nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,"
                        + " sed diam voluptua. At vero eos et accusam et justo duo dolores.",
                ParameterValueType.INTEGER, "", ZERO, ZERO, false, true, null);
        parameterDefs.add(paramDef);
        paramDef = new MockVOParameterDefinition(
                ParameterType.SERVICE_PARAMETER, "NUM_OF_USER",
                "Number of users", ParameterValueType.INTEGER, "", ZERO, ZERO,
                false, true, null);
        parameterDefs.add(paramDef);
        paramDef = new MockVOParameterDefinition(
                ParameterType.SERVICE_PARAMETER, "SIZE", "System size",
                ParameterValueType.ENUMERATION, "", ZERO, ZERO, false, true,
                new ArrayList<VOParameterOption>());
        paramDef.getParameterOptions().add(
                new MockVOParameterOption("SMALL", "small", paramDef
                        .getParameterId()));
        paramDef.getParameterOptions().add(
                new MockVOParameterOption("MEDIUM", "medium", paramDef
                        .getParameterId()));
        paramDef.getParameterOptions().add(
                new MockVOParameterOption("LARGE", "large", paramDef
                        .getParameterId()));
        parameterDefs.add(paramDef);

        techProd.setParameterDefinitions(parameterDefs);

        List<VOEventDefinition> eventDefinitions = new ArrayList<>();
        VOEventDefinition event;
        event = new VOEventDefinition();
        event.setKey(5 * list.size() + 1);
        event.setEventId("ADD");
        event.setEventDescription(event.getEventId() + " description");
        event.setEventType(EventType.SERVICE_EVENT);
        eventDefinitions.add(event);
        event = new VOEventDefinition();
        event.setKey(5 * list.size() + 2);
        event.setEventId("UPDATE");
        event.setEventDescription(event.getEventId() + " description");
        event.setEventType(EventType.SERVICE_EVENT);
        eventDefinitions.add(event);
        event = new VOEventDefinition();
        event.setKey(5 * list.size() + 3);
        event.setEventId("DELETE");
        event.setEventDescription(event.getEventId() + " description");
        event.setEventType(EventType.SERVICE_EVENT);
        eventDefinitions.add(event);
        event = new VOEventDefinition();
        event.setKey(5 * list.size() + 4);
        event.setEventId("LIST");
        event.setEventDescription(event.getEventId() + " description");
        event.setEventType(EventType.SERVICE_EVENT);
        eventDefinitions.add(event);
        event = new VOEventDefinition();

        techProd.setEventDefinitions(eventDefinitions);

        List<VORoleDefinition> roles = new ArrayList<>();
        VORoleDefinition roleDefinition = new VORoleDefinition();
        roleDefinition.setKey(1);
        roleDefinition.setRoleId("role1");
        roleDefinition.setName("roleName1");
        roleDefinition.setDescription("roleDescription1");
        roles.add(roleDefinition);

        roleDefinition = new VORoleDefinition();
        roleDefinition.setKey(2);
        roleDefinition.setRoleId("role2");
        roleDefinition.setName("roleName2");
        roleDefinition.setDescription("roleDescription2");
        roles.add(roleDefinition);

        roleDefinition = new VORoleDefinition();
        roleDefinition.setKey(3);
        roleDefinition.setRoleId("role3");
        roleDefinition.setName("roleName3");
        roleDefinition.setDescription("roleDescription3");
        roles.add(roleDefinition);

        techProd.setRoleDefinitions(roles);

        list.add(techProd);
        return techProd;
    }

    private VOService createProduct(List<VOService> list, String name,
            String description, String paymentInfo, VOTechnicalService techProd) {
        MockVOProductDetails voProduct = new MockVOProductDetails();
        voProduct.setTechnicalService(techProd);
        voProduct.setMockKey(list.size() + 1);
        voProduct.setServiceId(techProd.getTechnicalServiceId()
                + voProduct.getKey());
        voProduct.setName(name);
        voProduct.setTechnicalId(techProd.getTechnicalServiceId());
        voProduct.setFeatureURL("http://www.google.com");
        if (voProduct.getPriceModel() != null) {
            voProduct.getPriceModel().setLicense(techProd.getLicense());
        } else {
            VOPriceModel priceModel = new VOPriceModel();
            priceModel.setLicense(techProd.getLicense());
            voProduct.setPriceModel(priceModel);
        }
        voProduct.setDescription(description);
        List<VOParameter> parameters = new ArrayList<>();
        for (VOParameterDefinition paramDef : techProd
                .getParameterDefinitions()) {
            VOParameter param = new MockVOParameter(paramDef);
            param.setConfigurable(true);
            parameters.add(param);
        }
        voProduct.setParameters(parameters);

        VOPriceModel priceModel;
        priceModel = new VOPriceModel();
        List<VOPricedEvent> consideredEvents = new ArrayList<>();
        for (VOEventDefinition event : techProd.getEventDefinitions()) {
            VOPricedEvent pricedEvent = new VOPricedEvent(event);
            consideredEvents.add(pricedEvent);
        }
        priceModel.setConsideredEvents(consideredEvents);
        List<VOPricedParameter> selectedParameters = new ArrayList<>();
        for (VOParameter param : parameters) {
            VOPricedParameter pricedParam = new VOPricedParameter(
                    param.getParameterDefinition());
            if (param.getParameterDefinition().getValueType() == ParameterValueType.ENUMERATION) {
                List<VOPricedOption> pricedOptions = new ArrayList<>();
                for (VOParameterOption option : param.getParameterDefinition()
                        .getParameterOptions()) {
                    VOPricedOption pricedOption = new VOPricedOption();
                    pricedOption.setParameterOptionKey(option.getKey());
                    pricedOptions.add(pricedOption);
                }
                pricedParam.setPricedOptions(pricedOptions);
            }
            selectedParameters.add(pricedParam);
        }
        priceModel.setSelectedParameters(selectedParameters);
        if (PAYMENT_FREE.equals(paymentInfo)) {
            priceModel.setType(PriceModelType.FREE_OF_CHARGE);
        } else {
            priceModel.setType(PriceModelType.PRO_RATA);
        }
        priceModel.setDescription(paymentInfo);
        List<VOPricedRole> pricedRoles = new ArrayList<>();
        VOPricedRole pricedRole;
        pricedRole = new VOPricedRole();
        pricedRole.setRole(new VORoleDefinition());
        pricedRole.getRole().setName("Admin");
        pricedRole.setPricePerUser(BigDecimal.valueOf(42));
        pricedRoles.add(pricedRole);
        pricedRole = new VOPricedRole();
        pricedRole.setRole(new VORoleDefinition());
        pricedRole.getRole().setName("Guest");
        pricedRole.setPricePerUser(BigDecimal.valueOf(10));
        pricedRoles.add(pricedRole);
        priceModel.setRoleSpecificUserPrices(pricedRoles);
        voProduct.setPriceModel(priceModel);

        list.add(voProduct);
        return voProduct;
    }

    private VOUser createUser(List<VOUserDetails> list, String organizationId,
            String userId, String firstName, String lastName, String eMail,
            boolean isOrganizationAdmin) {
        VOUserDetails voUserDetails = new VOUserDetails();
        voUserDetails.setOrganizationId(organizationId);
        voUserDetails.setUserId(userId);
        voUserDetails.setFirstName(firstName);
        voUserDetails.setLastName(lastName);
        voUserDetails.setEMail(eMail);
        voUserDetails.setLocale("en");
        if (isOrganizationAdmin) {
            voUserDetails.addUserRole(UserRoleType.ORGANIZATION_ADMIN);
        } else {
            voUserDetails.removeUserRole(UserRoleType.ORGANIZATION_ADMIN);
        }
        voUserDetails.setStatus(UserAccountStatus.ACTIVE);
        voUserDetails.getOrganizationRoles().add(OrganizationRoleType.CUSTOMER);
        voUserDetails.getOrganizationRoles().add(OrganizationRoleType.SUPPLIER);
        voUserDetails.getOrganizationRoles().add(
                OrganizationRoleType.TECHNOLOGY_PROVIDER);
        list.add(voUserDetails);
        return voUserDetails;
    }

    private VOSubscription createSubscription(List<VOSubscription> list,
            String subscriptionId, VOService voProduct) {
        MockVOUserSubscription voSubscription = new MockVOUserSubscription();
        voSubscription.setMockKey(list.size());
        voSubscription.setSubscriptionId(subscriptionId);
        voSubscription.setServiceId(voProduct.getServiceId());
        voSubscription.setServiceKey(voProduct.getKey());
        voSubscription
                .setServiceBaseURL("http://localhost:8080/example-service/simple");
        voSubscription.setServiceInstanceId(subscriptionId);
        voSubscription.setStatus(SubscriptionStatus.ACTIVE);
        voSubscription.setServiceAccessType(ServiceAccessType.LOGIN);
        voSubscription
                .setCreationDate(Long.valueOf(System.currentTimeMillis()));
        subscriptionProductMap.put(voSubscription, voProduct);
        list.add(voSubscription);
        return voSubscription;
    }

    private void init() {

        VOPaymentType paymentType;
        paymentType = new VOPaymentType();
        paymentType
                .setCollectionType(PaymentCollectionType.PAYMENT_SERVICE_PROVIDER);
        paymentType.setPaymentTypeId("CREDIT_CARD");
        availablePaymentTypes.add(paymentType);
        paymentType = new VOPaymentType();
        paymentType.setCollectionType(PaymentCollectionType.ORGANIZATION);
        paymentType.setPaymentTypeId("INVOICE");
        availablePaymentTypes.add(paymentType);

        VOTechnicalService techProd;
        techProd = createTechProduct(
                techProductList,
                "LCM",
                "Interstage Application and Service Management (Interstage ASM)"
                        + " is a suite of products, components and tools that"
                        + " support you in managing the services, software products"
                        + " and applications of your company", LICENSE);
        createProduct(
                productList,
                "Trial",
                "Durch "
                        + "die Koordination einzelner Teilprozesse und die Unterstützung"
                        + " bei der Einhaltung des Entwicklungsprozesses gewinnen die"
                        + " Mitglieder des Entwicklungs- und Testteams mehr Zeit, um"
                        + " sich auf ihre eigentlichen Aufgaben zu konzentrieren. Zudem"
                        + " wird die Feedbackschleife von Änderungen oder Neuerungen"
                        + " stark verkürzt. So können auch Entwicklungs- und Wartungs-"
                        + "kosten signifikant gesenkt werden.", PAYMENT_FREE,
                techProd);
        createProduct(
                productList,
                "Professional",
                "Durch "
                        + "die Koordination einzelner Teilprozesse und die Unterstützung"
                        + " bei der Einhaltung des Entwicklungsprozesses gewinnen die"
                        + " Mitglieder des Entwicklungs- und Testteams mehr Zeit, um"
                        + " sich auf ihre eigentlichen Aufgaben zu konzentrieren. Zudem"
                        + " wird die Feedbackschleife von Änderungen oder Neuerungen"
                        + " stark verkürzt. So können auch Entwicklungs- und Wartungs-"
                        + "kosten signifikant gesenkt werden.",
                PAYMENT_MONTHLY, techProd);
        createProduct(
                productList,
                "Enterprise",
                "Durch "
                        + "die Koordination einzelner Teilprozesse und die Unterstützung"
                        + " bei der Einhaltung des Entwicklungsprozesses gewinnen die"
                        + " Mitglieder des Entwicklungs- und Testteams mehr Zeit, um"
                        + " sich auf ihre eigentlichen Aufgaben zu konzentrieren. Zudem"
                        + " wird die Feedbackschleife von Änderungen oder Neuerungen"
                        + " stark verkürzt. So können auch Entwicklungs- und Wartungs-"
                        + "kosten signifikant gesenkt werden.",
                PAYMENT_MONTHLY, techProd);

        techProd = createTechProduct(techProductList, "CRM",
                "The free encyclopedia that anyone can edit.", LICENSE);
        createProduct(
                productList,
                "",
                "SugarCRM, the largest open-source CRM platform with more than 800 organizations and 80,000+ downloads per month",
                PAYMENT_MONTHLY, techProd);

        techProd = createTechProduct(techProductList, "Wikipedia",
                "The free encyclopedia that anyone can edit.", LICENSE);
        createProduct(productList, "",
                "The free encyclopedia that anyone can edit.", PAYMENT_FREE,
                techProd);

        supplier = new VOOrganization();
        supplier.setOrganizationId("EST");
        supplier.setName("FUJITSU Enabling Software Technology GmbH");
        supplier.setAddress("Schwanthalerstr. 75A\r\n80336 München");
        supplier.setEmail("info@est.fujitsu.com");
        supplier.setLocale("en");
        supplier.setPhone("+49 89 32378-456");
        organizationList.add(supplier);
        customers.add(supplier);

        VOOrganization organization;
        organization = new VOOrganization();
        organization.setOrganizationId("Allianz");
        organization.setName("Allianz Deutschland");
        organization.setAddress("Königinstraße 28\r\nD-80802 München");
        organization.setEmail("info@allianz.de");
        organization.setLocale("de");
        organization.setPhone("0049.89.3800-0");
        organizationList.add(organization);
        customers.add(organization);

        organization = new VOOrganization();
        organization.setOrganizationId("BMW");
        organization.setName("BMW Deutschland");
        organization.setAddress("Heidemannstrasse 164\r\n80788 München");
        organization.setEmail("kundenbetreuung@bmw.de");
        organization.setLocale("en");
        organization.setPhone("0180 2 324252");
        organizationList.add(organization);
        customers.add(organization);

        List<VOUserDetails> userList = new ArrayList<>();
        createUser(userList, organization.getOrganizationId(), "user",
                "Martin", "Meier", "martin.meier@gmail.com", false);
        createUser(userList, organization.getOrganizationId(), "holger",
                "Holger", "Müller", "holger.mueller.meier@hotmail.com", false);
        createUser(userList, organization.getOrganizationId(), "sepp", "Sepp",
                "Huber", "sepp.huber@gmail.com", false);
        createUser(userList, organization.getOrganizationId(), "martin",
                "Martin", "Becker", "martin.becker@gmail.com", false);
        createUser(userList, organization.getOrganizationId(), "thomas",
                "Thomas", "Bach", "thomas.bach@web.de", false);
        for (int i = 0; i < 200; i++) {
            createUser(userList, organization.getOrganizationId(), "user" + i,
                    "Test", "User" + i, "test.user" + i + "@gmail.com", false);
        }
        createUser(userList, organization.getOrganizationId(), "admin",
                "Fritz", "Huber", "fritz.huber@gmail.com", true);
        voUserDetails = userList.get(userList.size() - 1);
        createUser(userList, organization.getOrganizationId(), "petra",
                "Petra", "Admin", "petra.admin@gmail.com", true);
        organizationUsersMap.put(organization, userList);

        List<VOSubscription> subscriptionList = new ArrayList<>();
        VOSubscription subscription;
        ArrayList<VOUserDetails> users;

        subscription = createSubscription(subscriptionList, "LCM (Sales)",
                productList.get(0));
        users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(userList.get(i));
        }
        subscriptionUsersMap.put(subscription, users);

        subscription = createSubscription(subscriptionList,
                "LCM (Documentation)", productList.get(0));
        users = new ArrayList<>();
        for (int i = 3; i < 10; i++) {
            users.add(userList.get(i));
        }
        subscriptionUsersMap.put(subscription, users);

        subscription = createSubscription(subscriptionList,
                "LCM (Development)", productList.get(0));
        VOTechnicalServiceOperation operation = new VOTechnicalServiceOperation();
        operation.setOperationId("BACKUP");
        operation.setOperationName("Backup");
        operation
                .setOperationDescription("Backup the application data to a FTP server.");
        List<VOTechnicalServiceOperation> operationList = new ArrayList<>();
        operationList.add(operation);
        subscription.setTechnicalServiceOperations(operationList);
        users = new ArrayList<>();
        for (int i = 10; i < userList.size(); i++) {
            users.add(userList.get(i));
        }
        subscriptionUsersMap.put(subscription, users);

        subscription = createSubscription(subscriptionList, "Wikipedia",
                productList.get(3));
        subscription.setServiceAccessType(ServiceAccessType.DIRECT);
        subscription.setServiceAccessInfo("Please got to www.wikipedia.org");
        users = new ArrayList<>();
        users.add(voUserDetails);
        subscriptionUsersMap.put(subscription, users);

        organizationSubscriptionsMap.put(organization, subscriptionList);
    }

    private VOOrganization getOrganizationById(String organizationId) {
        VOFinder<VOOrganization> organizationFinder = new VOFinder<VOOrganization>() {
            @Override
            public String getId(VOOrganization element) {
                return element.getOrganizationId();
            }
        };
        return organizationFinder.findById(organizationList, organizationId);
    }

    private VOUserDetails getUserById(VOOrganization organization, String userId) {
        if (organization == null) {
            return null;
        }
        return userFinder.findById(organizationUsersMap.get(organization),
                userId);
    }

    /*
     * IdManagement
     */

    @Override
    public VOUserDetails createUser(VOUserDetails userDetails,
            List<UserRoleType> roles, String marketplaceId)
            throws NonUniqueBusinessKeyException, MailOperationException,
            ValidationException, UserRoleAssignmentException {
        VOOrganization organization = getOrganizationDataInt();
        if (getUserById(organization, userDetails.getUserId()) != null) {
            throw new NonUniqueBusinessKeyException(ClassEnum.USER,
                    userDetails.getUserId());
        }
        userDetails.setOrganizationId(organization.getOrganizationId());
        userDetails.setStatus(UserAccountStatus.PASSWORD_MUST_BE_CHANGED);
        userDetails.setUserRoles(new HashSet<>(roles));
        getUsersForOrganization().add(userDetails);
        return userDetails;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        getCurrentUserDetails().setStatus(UserAccountStatus.ACTIVE);
    }

    @Override
    public void confirmAccount(VOUser user, String marketplaceId)
            throws ObjectNotFoundException {
        VOUser voUserDetails = getUserDetails(user);
        voUserDetails.setStatus(UserAccountStatus.ACTIVE);
    }

    @Override
    public void deleteUser(VOUser user, String marketplaceId) {
        VOOrganization organization = getOrganizationDataInt();

        // remove the user from the subscriptions
        for (VOSubscription subscription : organizationSubscriptionsMap
                .get(organization)) {
            List<VOUserDetails> userList = subscriptionUsersMap
                    .get(subscription);
            VOUser voUserDetails = userFinder.findById(userList,
                    user.getUserId());
            if (voUserDetails != null) {
                userList.remove(voUserDetails);
            }
        }

        // remove the user from the user list
        Iterator<VOUserDetails> userIt = organizationUsersMap.get(organization)
                .iterator();
        while (userIt.hasNext()) {
            if (user.getUserId().equals(userIt.next().getUserId())) {
                userIt.remove();
                break;
            }
        }

    }

    @Override
    public void grantUserRoles(VOUser user, List<UserRoleType> roles)
            throws ObjectNotFoundException, OperationNotPermittedException,
            UserRoleAssignmentException {
        VOUserDetails voUserDetails = getUserById(getOrganizationDataInt(),
                user.getUserId());
        voUserDetails.getUserRoles().addAll(new HashSet<>(roles));
    }

    @Override
    public void revokeUserRoles(VOUser user, List<UserRoleType> roles)
            throws ObjectNotFoundException,
            UserModificationConstraintException, UserActiveException,
            OperationNotPermittedException, UserRoleAssignmentException {
        VOUserDetails voUserDetails = getUserById(getOrganizationDataInt(),
                user.getUserId());
        voUserDetails.getUserRoles().removeAll(new HashSet<>(roles));
    }

    @Override
    public void setUserRoles(VOUser user, List<UserRoleType> roles)
            throws ObjectNotFoundException, OperationNotPermittedException,
            UserModificationConstraintException, UserRoleAssignmentException,
            UserActiveException {
        grantUserRoles(user, roles);
    }

    @Override
    public List<VOUserDetails> getUsersForOrganization() {
        return organizationUsersMap.get(getOrganizationDataInt());
    }

    @Override
    public VOUserDetails getUserDetails(VOUser user)
            throws ObjectNotFoundException {
        String organizationId = user.getOrganizationId();
        VOOrganization organization = getOrganizationById(organizationId);
        if (organization == null) {
            throw new ObjectNotFoundException(ClassEnum.ORGANIZATION,
                    user.getOrganizationId());
        }
        voUserDetails = getUserById(organization, user.getUserId());
        if (voUserDetails == null) {
            throw new ObjectNotFoundException(ClassEnum.USER, user.getUserId());
        }
        return voUserDetails;
    }

    @Override
    public VOUser getUser(VOUser user) throws ObjectNotFoundException {
        return getUserDetails(user);
    }

    @Override
    public void lockUserAccount(VOUser userToBeLocked,
            UserAccountStatus lockType, String marketplaceId) {

    }

    @Override
    public VOUserDetails updateUser(VOUserDetails userDetails)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        return userDetails;
    }

    @Override
    public void unlockUserAccount(VOUser user, String marketplaceId) {

    }

    @Override
    public void notifyOnLoginAttempt(VOUser user, boolean attemptSuccessful)
            throws ValidationException {

    }

    @Override
    public VOUserDetails getCurrentUserDetails() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            HttpServletRequest request = (HttpServletRequest) context
                    .getExternalContext().getRequest();
            VOUserDetails voUserDetails = (VOUserDetails) request.getSession()
                    .getAttribute(Constants.SESS_ATTR_USER);
            if (voUserDetails != null) {
                return voUserDetails;
            }
        }
        // the AuthorizationFilter calls this method to fill the session
        // attribute, we must break the cycle
        return voUserDetails;
    }

    @Override
    public VOUserDetails getCurrentUserDetailsIfPresent() {
        return getCurrentUserDetails();
    }

    @Override
    public void requestResetOfUserPassword(VOUser user, String marketplaceId) {

    }

    @Override
    public void sendAccounts(String email, String marketplaceId)
            throws ValidationException, MailOperationException {

    }

    @Override
    public List<VOUserDetails> searchLdapUsers(final String userIdPattern)
            throws ValidationException {
        return new ArrayList<>();
    }

    @Override
    public void importLdapUsers(List<VOUserDetails> users, String marketplaceId)
            throws NonUniqueBusinessKeyException, ValidationException,
            MailOperationException {

    }

    /*
     * ISubscriptionManagementRemote
     */

    public VOSubscription getSubscriptionById(String subscriptionId) {
        return subscriptionFinder.findById(getSubscriptionsForOrganization(),
                subscriptionId);
    }

    @Override
    public boolean addRevokeUser(String voSubscriptionId,
            List<VOUsageLicense> usersToBeAdded, List<VOUser> usersToBeRevoked)
            throws ObjectNotFoundException, ServiceParameterException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

        VOSubscription subscription = getSubscriptionById(voSubscriptionId);
        if (subscription == null) {
            throw new ObjectNotFoundException(ClassEnum.SUBSCRIPTION,
                    voSubscriptionId);
        }

        List<VOUserDetails> users = subscriptionUsersMap.get(subscription);
        if (users == null) {
            users = new ArrayList<>();
            subscriptionUsersMap.put(subscription, users);
        }

        // revoke
        for (VOUser revokedUser : usersToBeRevoked) {
            VOUser user = userFinder.findById(users, revokedUser.getUserId());
            if (user != null) {
                users.remove(user);
            }
        }

        // add
        VOOrganization organization = getOrganizationDataInt();
        for (VOUsageLicense lic : usersToBeAdded) {
            VOUserDetails user = getUserById(organization, lic.getUser()
                    .getUserId());
            if (user != null) {
                if (!users.contains(user)) {
                    // If we only changed the role we must not add the user
                    // again
                    users.add(user);
                }
            }
        }
        return true;
    }

    @Override
    public List<VOUserSubscription> getSubscriptionsForUser(VOUser user)
            throws ObjectNotFoundException {

        List<VOUserSubscription> list = new ArrayList<>();
        for (VOSubscription subscription : getSubscriptionsForOrganization()) {
            List<VOUserDetails> users = subscriptionUsersMap.get(subscription);
            if (userFinder.findById(users, user.getUserId()) != null) {
                MockVOUserSubscription userSub = new MockVOUserSubscription();
                copyProperties(userSub, subscription);
                userSub.setMockKey(subscription.getKey());
                VOUsageLicense voUsageLicense = new VOUsageLicense();
                voUsageLicense.setUser(user);
                userSub.setLicense(voUsageLicense);
                list.add(userSub);
            }
        }
        return list;
    }

    @Override
    public List<VOUserSubscription> getSubscriptionsForCurrentUser() {
        try {
            return getSubscriptionsForUser(getCurrentUserDetails());
        } catch (ObjectNotFoundException e) {
            throw new SaaSSystemException("User not found, invalid session!");
        }
    }

    @Override
    public List<VOSubscription> getSubscriptionsForOrganization() {
        List<VOSubscription> list = organizationSubscriptionsMap
                .get(getOrganizationDataInt());
        if (list == null) {
            list = new ArrayList<>();
            organizationSubscriptionsMap.put(getOrganizationDataInt(), list);
        }
        return list;
    }

    public boolean hasActiveSubscriptions() {
        return !getSubscriptionsForOrganization().isEmpty();
    }

    public void revokeAssignment(String subscriptionId, VOUser user)
            throws ObjectNotFoundException, ServiceParameterException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        List<VOUsageLicense> usersToBeAdded = new ArrayList<>();
        List<VOUser> usersToBeRevoked = new ArrayList<>();
        usersToBeRevoked.add(user);
        addRevokeUser(subscriptionId, usersToBeAdded, usersToBeRevoked);
    }

    @Override
    public VOSubscription subscribeToService(VOSubscription subscription,
            VOService service, List<VOUsageLicense> users,
            VOPaymentInfo paymentInfo, VOBillingContact billingContact,
            List<VOUda> udas) throws NonUniqueBusinessKeyException,
            PaymentInformationException, ServiceParameterException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException, MandatoryUdaMissingException {
        String subscriptionId = subscription.getSubscriptionId();
        if (getSubscriptionById(subscriptionId) != null) {
            throw new NonUniqueBusinessKeyException(ClassEnum.SUBSCRIPTION,
                    subscriptionId);
        }

        String pon = subscription.getPurchaseOrderNumber();
        VOOrganization organization = getOrganizationDataInt();
        subscription = createSubscription(getSubscriptionsForOrganization(),
                subscriptionId, service);
        subscription.setPurchaseOrderNumber(pon);
        List<VOUserDetails> userList = new ArrayList<>();
        for (VOUsageLicense lic : users) {
            VOUser user = lic.getUser();
            userList.add(getUserById(organization, user.getUserId()));
        }
        subscriptionUsersMap.put(subscription, userList);
        return subscription;
    }

    @Override
    public VOSubscription upgradeSubscription(VOSubscription current,
            VOService newProduct, VOPaymentInfo paymentInfo,
            VOBillingContact billingContact, List<VOUda> udas)
            throws PaymentInformationException, MandatoryUdaMissingException {
        VOSubscription subscription = getSubscriptionById(current
                .getSubscriptionId());
        subscriptionProductMap.put(subscription, newProduct);
        return null;
    }

    public VOService getProduct(VOSubscription subscription) {
        return subscriptionProductMap.get(subscription);
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetails(String subId)
            throws ObjectNotFoundException {
        VOSubscription subscription = getSubscriptionById(subId);
        if (subscription == null) {
            throw new ObjectNotFoundException(ClassEnum.SUBSCRIPTION, subId);
        }

        VOSubscriptionDetails voSubscriptionDetails = new VOSubscriptionDetails();
        copyProperties(voSubscriptionDetails, subscription);

        List<VOUsageLicense> voUsageLicenseList = new ArrayList<>();
        List<VOUserDetails> users = subscriptionUsersMap.get(subscription);
        if (users != null) {
            for (VOUser voUser : users) {
                VOUsageLicense voUsageLicense = new VOUsageLicense();
                voUsageLicense.setUser(voUser);
                voUsageLicenseList.add(voUsageLicense);
            }
        }
        voSubscriptionDetails.setUsageLicenses(voUsageLicenseList);

        VOService voProduct = getProduct(subscription);
        if (voProduct != null) {
            voSubscriptionDetails.setPriceModel(voProduct.getPriceModel());
        }

        return voSubscriptionDetails;
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetails(long subscriptionKey)
            throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public List<VOService> getUpgradeOptions(String subscriptionId) {
        List<VOService> list = new ArrayList<>();
        VOSubscription subscription = getSubscriptionById(subscriptionId);

        VOService voProduct;
        voProduct = getProduct(subscription);
        if (voProduct == null) {
            return list;
        }
        for (VOService e : productList) {
            if (e.getTechnicalId().equals(voProduct.getTechnicalId())
                    && e.getPriceModel() != voProduct.getPriceModel()) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    public List<VOService> getUpgradeOptions(long subscriptionKey)
            throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public boolean unsubscribeFromService(String subId)
            throws ObjectNotFoundException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        VOSubscription subscription = getSubscriptionById(subId);
        if (subscription == null) {
            throw new ObjectNotFoundException(ClassEnum.SUBSCRIPTION, subId);
        }
        subscription.setStatus(SubscriptionStatus.DEACTIVATED);
        subscriptionUsersMap.remove(subscription);
        return true;
    }

    /*
     * IServiceProvisioning
     */

    @Override
    public List<VOService> getSuppliedServices() {
        return productList;
    }

    @Override
    public List<VOService> getServicesForMarketplace(String marketplaceId) {
        return productList;
    }

    @Override
    public String importTechnicalServices(byte[] xml)
            throws OperationNotPermittedException,
            TechnicalServiceActiveException, UpdateConstraintException {

        return null;
    }

    @Override
    public VOService activateService(VOService service) {

        return null;
    }

    @Override
    public VOService deactivateService(VOService service) {

        return null;
    }

    @Override
    public void validateTechnicalServiceCommunication(
            VOTechnicalService techProduct) {
    }

    /*
     * IAccountManagement
     */

    @Override
    public void deregisterOrganization() {
        organizationList.remove(getOrganizationDataInt());
        organizationUsersMap.remove(getOrganizationDataInt());
        organizationSubscriptionsMap.remove(getOrganizationDataInt());
        organizationPaymentTypeMap.remove(getOrganizationDataInt());
    }

    @Override
    public VOOrganization getOrganizationData() {
        VOOrganization organization = new VOOrganization();
        copyProperties(organization, getOrganizationDataInt());
        return organization;
    }

    @Override
    public VOOrganization getOrganizationDataFallback() {
        VOOrganization organization = new VOOrganization();
        copyProperties(organization, getOrganizationDataInt());
        return organization;
    }

    public VOOrganization getOrganizationDataInt() {
        VOUserDetails voUserDetails = getCurrentUserDetails();

        for (VOOrganization organization : organizationList) {
            if (organization.getOrganizationId().equals(
                    voUserDetails.getOrganizationId())) {
                return organization;
            }
        }
        throw new SaaSSystemException(
                "Organization not found, invalid session!");
    }

    @Override
    public VOOrganization registerCustomer(VOOrganization organization,
            VOUserDetails admin, String password, Long serviceKey,
            String marketplaceId, String sellerId)
            throws NonUniqueBusinessKeyException, MailOperationException {

        // we use a random number as custumerId, if the number is already
        // used we must try another number
        Random rand = new Random();
        int i = 0;
        String organizationId = null;
        while (organizationId == null) {
            organizationId = Integer.toHexString(Short.MAX_VALUE
                    + rand.nextInt(Short.MAX_VALUE));
            if (getOrganizationById(organizationId) != null) {
                i++;
                if (i > 100) {
                    throw new SaaSSystemException(
                            "No free organizationId found!");
                }
                organizationId = null;
            }
        }

        if (organization == null) {
            organization = new VOOrganization();
        }
        organization.setOrganizationId(organizationId);
        organization.setLocale(admin.getLocale());
        admin.setOrganizationId(organizationId);
        admin.addUserRole(UserRoleType.ORGANIZATION_ADMIN);
        admin.setStatus(UserAccountStatus.LOCKED_NOT_CONFIRMED);
        organizationList.add(organization);
        organizationUsersMap.put(organization, new ArrayList<VOUserDetails>());
        organizationUsersMap.get(organization).add(admin);
        organizationSubscriptionsMap.put(organization,
                new ArrayList<VOSubscription>());
        return organization;
    }

    @Override
    public void updateAccountInformation(VOOrganization organization,
            VOUserDetails voUser, String marketplaceId,
            VOImageResource imageResource) {
        copyProperties(getOrganizationDataInt(), organization);
    }

    @Override
    public String getOrganizationId(long subscriptionKey) {
        for (VOOrganization organization : organizationSubscriptionsMap
                .keySet()) {
            for (VOSubscription sub : organizationSubscriptionsMap
                    .get(organization)) {
                if (sub.getKey() == subscriptionKey) {
                    return organization.getOrganizationId();
                }
            }
        }
        return "";
    }

    /*
     * IConfigurationServiceRemote
     */

    @Override
    public VOConfigurationSetting getVOConfigurationSetting(
            ConfigurationKey informationId, String contextId) {
        VOConfigurationSetting vo = null;
        if (informationId == ConfigurationKey.BASE_URL) {
            vo = new VOConfigurationSetting(ConfigurationKey.BASE_URL,
                    Configuration.GLOBAL_CONTEXT,
                    "http://localhost:8080/oscm-portal");
        }
        return vo;
    }

    @Override
    public void setConfigurationSetting(String informationId, String value) {
    }

    @Override
    public VOSubscriptionDetails modifySubscription(
            VOSubscription subscription, List<VOParameter> modifiedParameters,
            List<VOUda> udas) throws NonUniqueBusinessKeyException,
            ObjectNotFoundException, OperationNotPermittedException,
            MandatoryUdaMissingException {
        List<VOSubscription> list = getSubscriptionsForOrganization();
        if (list == null) {
            return null;
        }
        for (VOSubscription vo : list) {
            if (vo.getKey() == subscription.getKey()) {
                vo.setSubscriptionId(subscription.getSubscriptionId());
                return null;
            }
        }
        throw new ObjectNotFoundException(ClassEnum.SUBSCRIPTION,
                String.valueOf(subscription.getKey()));
    }

    @Override
    public void createServiceSession(long subscriptionKey, String sessionId,
            String userToken) throws ServiceParameterException {

    }

    @Override
    public String deleteServiceSession(long subscriptionTKey, String sessionId) {

        return null;
    }

    @Override
    public void deleteSessionsForSessionId(String sessionId) {

    }

    @Override
    public List<Long> getSubscriptionKeysForSessionId(String sessionId) {

        return null;
    }

    @Override
    public String resolveUserToken(long subscriptionKey, String sessionId,
            String userToken) {

        return null;
    }

    @Override
    public void createPlatformSession(String sessionId) {

    }

    @Override
    public int deletePlatformSession(String sessionId) {

        return 0;
    }

    /*
     * IReporting
     */

    @Override
    public void completeAsyncSubscription(String subscriptionId,
            String customerId, VOInstanceInfo instance)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {

    }

    @Override
    public void abortAsyncSubscription(String subscriptionId,
            String customerId, List<VOLocalizedText> reason)
            throws OrganizationAuthoritiesException,
            OperationNotPermittedException {

    }

    @Override
    public List<VOTechnicalService> getTechnicalServices(
            OrganizationRoleType organizationRoleType) {
        return techProductList;
    }

    @Override
    public VOServiceDetails createService(VOTechnicalService technicalProduct,
            VOService productToCreate, VOImageResource voImageResource)
            throws ObjectNotFoundException {
        productList.add(productToCreate);
        VOServiceDetails result = new VOServiceDetails();
        result.setServiceId(productToCreate.getServiceId());
        result.setName(productToCreate.getName());
        result.setDescription(productToCreate.getDescription());
        result.setTechnicalService(technicalProduct);
        return result;
    }

    @Override
    public List<VOOrganization> getMyCustomers() {
        return new ArrayList<>(customers);
    }

    @Override
    public VOOrganization getMyCustomer(VOOrganization org, String locale) {
        return new VOOrganization();
    }

    @Override
    public List<VOOrganization> getMyCustomersOptimization() {
        return new ArrayList<>(customers);
    }

    @Override
    public void deleteTechnicalService(VOTechnicalService technicalProduct)
            throws OrganizationAuthoritiesException, ObjectNotFoundException,
            DeletionConstraintException {

    }

    @Override
    public List<String> getSubscriptionIdentifiers() {
        List<String> result = new ArrayList<>();
        for (VOOrganization org : customers) {
            if (organizationSubscriptionsMap.get(org) != null) {
                for (VOSubscription sub : organizationSubscriptionsMap.get(org)) {
                    result.add(sub.getSubscriptionId());
                }
            }
        }
        return result;
    }

    @Override
    public List<VOOrganization> getCustomersForSubscriptionId(
            String subscriptionIdentifier) {
        List<VOOrganization> result = new ArrayList<>();
        for (VOOrganization org : customers) {
            if (organizationSubscriptionsMap.get(org) != null) {
                for (VOSubscription sub : organizationSubscriptionsMap.get(org)) {
                    if (subscriptionIdentifier.equals(sub.getSubscriptionId())) {
                        result.add(org);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public VOServiceDetails getServiceForCustomer(VOOrganization customer,
            VOService service) {
        return getServiceDetails(service);
    }

    @Override
    public VOServiceDetails getServiceForSubscription(VOOrganization customer,
            String subscriptionId) throws ObjectNotFoundException {
        return getServiceDetails(getSubscriptionDetails(subscriptionId)
                .getSubscribedService());
    }

    @Override
    public VOServiceDetails getServiceDetails(VOService service) {
        if (service instanceof VOServiceDetails) {
            return (VOServiceDetails) service;
        }
        return null;
    }

    @Override
    public VOServiceDetails savePriceModel(VOServiceDetails productDetails,
            VOPriceModel pricemodel) throws ObjectNotFoundException,
            OperationNotPermittedException {
        return productDetails;
    }

    @Override
    public VOServiceDetails savePriceModelForCustomer(
            VOServiceDetails productDetails, VOPriceModel priceModel,
            VOOrganization customer) throws OrganizationAuthoritiesException,
            ObjectNotFoundException, OperationNotPermittedException,
            CurrencyException {

        return null;
    }

    @Override
    public VOServiceDetails savePriceModelForSubscription(
            VOServiceDetails productDetails, VOPriceModel priceModel)
            throws OrganizationAuthoritiesException, ObjectNotFoundException,
            OperationNotPermittedException, CurrencyException {

        return null;
    }

    @Override
    public List<VOService> getCompatibleServices(VOService referenceProduct) {

        return null;
    }

    @Override
    public void setCompatibleServices(VOService sourceProduct,
            List<VOService> compatibleProducts) {

    }

    @Override
    public void deleteService(VOService service) {

    }

    @Override
    public VOServiceDetails updateService(VOServiceDetails productDetails,
            VOImageResource voImageResource) {

        return null;
    }

    @Override
    public List<VOService> getServicesForCustomer(VOOrganization customer)
            throws ObjectNotFoundException {
        return Collections.emptyList();
    }

    @Override
    public List<String> getSupportedCurrencies() {
        return Collections.singletonList("EUR");
    }

    @Override
    public VOImageResource loadImage(Long productKey) {
        return null;
    }

    /*
     * BrandManagement
     */

    @Override
    public void deleteImages(List<ImageType> imageTypes) {
        if (imageTypes != null) {
            for (Iterator<VOImageResource> it = imageResources.iterator(); it
                    .hasNext();) {
                VOImageResource vo = it.next();
                if (imageTypes.contains(vo.getImageType())) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void deleteAllMessageProperties(String marketplaceId)
            throws ObjectNotFoundException, OperationNotPermittedException {
        messagePropertiesMap = null;
    }

    @Override
    public VOImageResource loadImage(String organizationId, ImageType imageType) {
        for (VOImageResource vo : imageResources) {
            if (vo.getImageType() == imageType) {
                return vo;
            }
        }
        return null;
    }

    @Override
    public Properties loadMessageProperties(String organizationId,
            String localeString) {
        if (messagePropertiesMap != null) {
            return messagePropertiesMap.get(localeString);
        }
        return null;
    }

    @Override
    public void saveImages(List<VOImageResource> voImageResources,
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException {
        for (VOImageResource newVo : voImageResources) {
            VOImageResource vo = loadImage(getCurrentUserDetails()
                    .getOrganizationId(), newVo.getImageType());
            if (vo == null) {
                imageResources.remove(null);
            }
            imageResources.add(newVo);
        }
    }

    @Override
    public void saveMessageProperties(Map<String, Properties> propertiesMap,
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException {
        messagePropertiesMap = propertiesMap;
    }

    @Override
    public VOOrganization registerKnownCustomer(VOOrganization organization,
            VOUserDetails user, LdapProperties ldapProperties,
            String marketplaceId) throws OrganizationAuthoritiesException,
            ValidationException, NonUniqueBusinessKeyException,
            MailOperationException {

        return null;
    }

    @Override
    public Set<VOPaymentType> getAvailablePaymentTypesForOrganization() {
        return availablePaymentTypes;
    }

    @Override
    public Set<VOPaymentType> getAvailablePaymentTypesFromOrganization(
            Long serviceKey) throws OrganizationAuthoritiesException {
        return new HashSet<>();
    }

    @Override
    public byte[] exportTechnicalServices(List<VOTechnicalService> service)
            throws OrganizationAuthoritiesException, ObjectNotFoundException,
            OperationNotPermittedException {
        return XMLConverter
                .toUTF8("<?xml version='1.0' encoding='UTF-8'?><export></export>\n");
    }

    @Override
    public List<VOSubscriptionIdAndOrganizations> getCustomerSubscriptions()
            throws OrganizationAuthoritiesException {
        List<VOSubscriptionIdAndOrganizations> result = new ArrayList<>();
        List<String> subscriptionIdentifiers = getSubscriptionIdentifiers();
        for (String subscriptionIdentifier : subscriptionIdentifiers) {
            List<VOOrganization> customers = getCustomersForSubscriptionId(subscriptionIdentifier);
            VOSubscriptionIdAndOrganizations subAndOrgs = new VOSubscriptionIdAndOrganizations();
            subAndOrgs.setSubscriptionId(subscriptionIdentifier);
            subAndOrgs.setOrganizations(customers);
            result.add(subAndOrgs);
        }
        return result;
    }

    @Override
    public VOTechnicalService createTechnicalService(
            VOTechnicalService technicalProduct)
            throws OrganizationAuthoritiesException, ValidationException,
            NonUniqueBusinessKeyException {
        return technicalProduct;
    }

    @Override
    public void saveTechnicalServiceLocalization(
            VOTechnicalService technicalProduct)
            throws OrganizationAuthoritiesException, ObjectNotFoundException,
            OperationNotPermittedException, UpdateConstraintException {

    }

    @Override
    public List<VOCustomerService> getAllCustomerSpecificServices()
            throws OrganizationAuthoritiesException {
        return Collections.emptyList();
    }

    @Override
    public List<VOBillingContact> getBillingContacts() {
        return Collections.singletonList(new VOBillingContact());
    }

    @Override
    public List<VOOrganizationPaymentConfiguration> getCustomerPaymentConfiguration() {

        return null;
    }

    @Override
    public VOBillingContact saveBillingContact(VOBillingContact billingContact)
            throws NonUniqueBusinessKeyException,
            OperationNotPermittedException {
        return null;
    }

    @Override
    public void deleteBillingContact(VOBillingContact billingContact)
            throws ObjectNotFoundException, ConcurrentModificationException,
            OperationNotPermittedException {
    }

    @Override
    public boolean savePaymentConfiguration(
            Set<VOPaymentType> defaultConfiguration,
            List<VOOrganizationPaymentConfiguration> customerConfigurations,
            Set<VOPaymentType> defaultServiceConfiguration,
            List<VOServicePaymentConfiguration> serviceConfigurations)
            throws ObjectNotFoundException, OperationNotPermittedException {

        return true;
    }

    @Override
    public Set<VOPaymentType> getDefaultPaymentConfiguration() {

        return null;
    }

    @Override
    public VOPaymentInfo savePaymentInfo(VOPaymentInfo paymentInfo)
            throws ObjectNotFoundException, PaymentDeregistrationException,
            NonUniqueBusinessKeyException, OperationNotPermittedException,
            PaymentDataException {
        organizationPaymentTypeMap.put(getOrganizationDataInt(),
                paymentInfo.getPaymentType());
        return paymentInfo;
    }

    @Override
    public VOSubscriptionDetails getSubscriptionForCustomer(
            String organizationId, String subscriptionId)
            throws ObjectNotFoundException, OperationNotPermittedException {

        List<VOSubscription> list = organizationSubscriptionsMap
                .get(getOrganizationById(organizationId));
        if (list != null) {
            for (VOSubscription vo : list) {
                if (vo.getSubscriptionId().equals(subscriptionId)) {
                    VOSubscriptionDetails details = new VOSubscriptionDetails();
                    copyProperties(details, vo);
                    details.setPriceModel(getProduct(vo).getPriceModel());
                    return details;
                }
            }
        }
        return null;
    }

    @Override
    public VOOrganization updateCustomerDiscount(VOOrganization voOrganization)
            throws ObjectNotFoundException, ValidationException,
            OperationNotPermittedException {

        return null;
    }

    /*
     * TriggerService
     */

    @Override
    public void approveAction(long key) {

    }

    @Override
    public void cancelActions(List<Long> keys, List<VOLocalizedText> reason)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TriggerProcessStatusException {

    }

    @Override
    public void deleteActions(List<Long> keys) throws ObjectNotFoundException,
            OperationNotPermittedException, TriggerProcessStatusException {

    }

    @Override
    public List<VOTriggerProcess> getAllActions() {
        return getAllActionsForOrganization();
    }

    @Override
    public List<VOTriggerProcess> getAllActionsForOrganization() {
        List<VOTriggerDefinition> list = getAllDefinitions();

        VOTriggerProcess vo = new VOTriggerProcess();
        vo.setKey(4711l);
        vo.setActivationDate(System.currentTimeMillis());
        vo.setStatus(TriggerProcessStatus.WAITING_FOR_APPROVAL);
        vo.setTriggerDefinition(list.get(0));
        vo.setUser(voUserDetails);
        return Collections.singletonList(vo);
    }

    @Override
    public List<VOTriggerDefinition> getAllDefinitions() {
        // we return a dummy trigger defintion to enable the menue item
        VOTriggerDefinition vo = new VOTriggerDefinition();
        vo.setSuspendProcess(true);
        vo.setTarget("http://");
        vo.setTargetType(TriggerTargetType.WEB_SERVICE);
        vo.setType(TriggerType.ACTIVATE_SERVICE);
        vo.setName(vo.getType().name());
        return Collections.singletonList(vo);
    }

    @Override
    public void rejectAction(long key, List<VOLocalizedText> reason)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TriggerProcessStatusException {

    }

    @Override
    public List<VORoleDefinition> getServiceRolesForSubscription(
            String subscription) throws ObjectNotFoundException,
            OperationNotPermittedException {

        return null;
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForSubscription(
            long subscriptionKey) throws ObjectNotFoundException,
            OperationNotPermittedException {
        return null;
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForService(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException {

        return null;
    }

    @Override
    public VOPriceModelLocalization getPriceModelLocalization(
            VOPriceModel pricemodel) throws ObjectNotFoundException,
            OperationNotPermittedException {
        final String locale = voUserDetails.getLocale();
        final VOPriceModelLocalization pl = new VOPriceModelLocalization();
        pl.setDescriptions(Collections.singletonList(new VOLocalizedText(
                locale, pricemodel.getDescription())));
        return pl;
    }

    @Override
    public VOServiceLocalization getServiceLocalization(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException {
        final String locale = voUserDetails.getLocale();
        final VOServiceLocalization pl = new VOServiceLocalization();
        pl.setNames(Collections.singletonList(new VOLocalizedText(locale,
                service.getName())));
        pl.setDescriptions(Collections.singletonList(new VOLocalizedText(
                locale, service.getDescription())));
        return pl;
    }

    @Override
    public void savePriceModelLocalization(VOPriceModel pricemodel,
            VOPriceModelLocalization localization)
            throws ObjectNotFoundException, OperationNotPermittedException {

    }

    @Override
    public void saveServiceLocalization(VOService service,
            VOServiceLocalization localization) throws ObjectNotFoundException,
            OperationNotPermittedException {

    }

    @Override
    public void updateAsyncSubscriptionProgress(String subscriptionId,
            String organizationId, List<VOLocalizedText> progress)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {

    }

    @Override
    public void executeServiceOperation(VOSubscription subscription,
            VOTechnicalServiceOperation operation)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, ValidationException {

    }

    @Override
    public Set<String> getUdaTargetTypes() {
        return null;
    }

    @Override
    public List<VOUdaDefinition> getUdaDefinitions() {
        return null;
    }

    @Override
    public List<VOUda> getUdas(String targetType, long targetObjectKey)
            throws ValidationException, OrganizationAuthoritiesException,
            ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public void saveUdaDefinitions(List<VOUdaDefinition> udaDefinitionsToSave,
            List<VOUdaDefinition> udaDefinitionsToDelete)
            throws ValidationException, OrganizationAuthoritiesException,
            NonUniqueBusinessKeyException, ObjectNotFoundException,
            ConcurrentModificationException, OperationNotPermittedException {

    }

    @Override
    public void saveUdas(List<VOUda> udas) throws ValidationException,
            ObjectNotFoundException, OperationNotPermittedException,
            ConcurrentModificationException, NonUniqueBusinessKeyException {

    }

    @Override
    public List<String> getSupportedCountryCodes() {
        return Arrays.asList(Locale.getISOCountries());
    }

    @Override
    public VOServiceDetails copyService(VOService service, String serviceId)
            throws ObjectNotFoundException, OrganizationAuthoritiesException,
            OperationNotPermittedException, ServiceStateException,
            ConcurrentModificationException, NonUniqueBusinessKeyException,
            ValidationException {
        return getServiceDetails(service);
    }

    @Override
    public void terminateSubscription(VOSubscription subscription, String reason)
            throws ObjectNotFoundException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, ConcurrentModificationException {

    }

    /*
     * VatService
     */

    @Override
    public List<VOCountryVatRate> getCountryVats() {
        return new ArrayList<>();
    }

    @Override
    public VOVatRate getDefaultVat() {
        return defaultVat;
    }

    @Override
    public List<VOOrganizationVatRate> getOrganizationVats() {
        return new ArrayList<>();
    }

    @Override
    public boolean getVatSupport() {
        return defaultVat != null;
    }

    @Override
    public void saveAllVats(VOVatRate defaultVat,
            List<VOCountryVatRate> countryVats,
            List<VOOrganizationVatRate> organizationVats)
            throws OrganizationAuthoritiesException,
            ConcurrentModificationException, OperationNotPermittedException,
            ValidationException {
        this.defaultVat = defaultVat;
    }

    @Override
    public void saveCountryVats(List<VOCountryVatRate> countryVats)
            throws OrganizationAuthoritiesException,
            ConcurrentModificationException, ValidationException {

    }

    @Override
    public void saveDefaultVat(VOVatRate defaultVat)
            throws OrganizationAuthoritiesException,
            ConcurrentModificationException, ValidationException {
        this.defaultVat = defaultVat;
    }

    @Override
    public void saveOrganizationVats(
            List<VOOrganizationVatRate> organizationVats)
            throws OrganizationAuthoritiesException,
            ConcurrentModificationException, OperationNotPermittedException,
            ValidationException {

    }

    @Override
    public VOImageResource loadImageForSupplier(String serviceId,
            String supplierId) throws ObjectNotFoundException {

        return null;
    }

    @Override
    public List<VOLocalizedText> getPriceModelLicenseTemplateLocalization(
            VOServiceDetails service) throws ObjectNotFoundException,
            OperationNotPermittedException {

        return null;
    }

    @Override
    public boolean hasCurrentUserSubscriptions() {

        return false;
    }

    @Override
    public String getMarketplaceStage(String marketplaceId, String localeString) {

        return null;
    }

    @Override
    public List<UserRoleType> getAvailableUserRoles(VOUser user)
            throws ObjectNotFoundException, OperationNotPermittedException {
        return Collections.emptyList();
    }

    @Override
    public VOImageResource loadImageOfOrganization(long organizationKey) {
        return null;
    }

    @Override
    public VOOrganization getSeller(String sellerId, String locale)
            throws ObjectNotFoundException {
        return null;
    }

    @Override
    public List<VOService> getRelatedServicesForMarketplace(VOService service,
            String marketplaceId, String locale) throws ObjectNotFoundException {
        return null;
    }

    @Override
    public List<VOService> setActivationStates(
            List<VOServiceActivation> activations)
            throws ObjectNotFoundException, ServiceStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException,
            ServiceOperationException, TechnicalServiceNotAliveException {
        return null;
    }

    @Override
    public VOServiceEntry getServiceForMarketplace(Long serviceKey,
            String marketplaceId, String locale)
            throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public void setMarketplaceStage(String stageContent, String marketplaceId,
            String localeString) throws ObjectNotFoundException,
            OperationNotPermittedException {
    }

    @Override
    public VOUserDetails createOnBehalfUser(String organizationId, String string)
            throws ObjectNotFoundException, OperationNotPermittedException,
            NonUniqueBusinessKeyException {
        return null;
    }

    @Override
    public void cleanUpCurrentUser() {
    }

    @Override
    public List<VOLocalizedText> getMarketplaceStageLocalization(
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException {
        return null;
    }

    @Override
    public VOOrganization getServiceSeller(long serviceKey, String locale) {
        return new VOOrganization();
    }

    @Override
    public void deletePaymentInfo(VOPaymentInfo paymentInfo)
            throws ObjectNotFoundException, OperationNotPermittedException,
            ConcurrentModificationException, PaymentDeregistrationException {
    }

    @Override
    public List<VOPaymentInfo> getPaymentInfos() {
        return new ArrayList<>();
    }

    @Override
    public VOSubscriptionDetails modifySubscriptionPaymentData(
            VOSubscription subscription, VOBillingContact billingContact,
            VOPaymentInfo paymentInfo) throws ObjectNotFoundException,
            ConcurrentModificationException, OperationNotPermittedException,
            PaymentInformationException, SubscriptionStateException,
            PaymentDataException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        return null;
    }

    @Override
    public Set<VOPaymentType> getAvailablePaymentTypes() {
        return availablePaymentTypes;
    }

    @Override
    public List<String> getInstanceIdsForSellers(List<String> organizationIds) {
        return new ArrayList<>();
    }

    @Override
    public VOService suspendService(VOService service, String reason)
            throws ObjectNotFoundException, OperationNotPermittedException,
            ServiceStateException {
        return null;
    }

    @Override
    public VOService resumeService(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException,
            ServiceStateException {
        return null;
    }

    @Override
    public Set<VOPaymentType> getDefaultServicePaymentConfiguration() {
        return new HashSet<>();
    }

    @Override
    public List<VOServicePaymentConfiguration> getServicePaymentConfiguration() {
        return new ArrayList<>();
    }

    @Override
    public VOServiceListResult getServicesByCriteria(String marketplaceId,
            String locale, ListCriteria listCriteria,
            PerformanceHint performanceHint) throws ObjectNotFoundException {
        return new VOServiceListResult();
    }

    @Override
    public VOServiceListResult searchServices(String marketplaceId,
            String locale, String searchPhrase, PerformanceHint performanceHint)
            throws ObjectNotFoundException, InvalidPhraseException {
        return new VOServiceListResult();
    }

    @Override
    public List<VOSubscription> getSubscriptionsForOrganizationWithFilter(
            Set<SubscriptionStatus> requiredStatus) {
        return Collections.emptyList();
    }

    @Override
    public void addSuppliersForTechnicalService(
            VOTechnicalService technicalService, List<String> organizationIds)
            throws ObjectNotFoundException {
    }

    @Override
    public void removeSuppliersFromTechnicalService(
            VOTechnicalService technicalService, List<String> organizationIds)
            throws OrganizationAuthoritiesException,
            MarketingPermissionNotFoundException {
    }

    @Override
    public List<VOOrganization> getSuppliersForTechnicalService(
            VOTechnicalService technicalService) {
        return Collections.emptyList();
    }

    @Override
    public boolean statusAllowsDeletion(VOService service)
            throws OperationNotPermittedException, ObjectNotFoundException,
            ConcurrentModificationException {
        return true;
    }

    @Override
    public List<VOCompatibleService> getPotentialCompatibleServices(
            VOService service) throws ObjectNotFoundException,
            OperationNotPermittedException {
        return Collections.emptyList();
    }

    @Override
    public boolean isPartOfUpgradePath(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException {
        return true;
    }

    @Override
    public void createTriggerDefinition(VOTriggerDefinition vo)
            throws TriggerDefinitionDataException, ValidationException {
        // nothing
    }

    @Override
    public void deleteTriggerDefinition(long key)
            throws ObjectNotFoundException, DeletionConstraintException {
        // nothing
    }

    @Override
    public void updateTriggerDefinition(VOTriggerDefinition vo)
            throws ObjectNotFoundException, ValidationException,
            ConcurrentModificationException, TriggerDefinitionDataException {
        // nothing
    }

    @Override
    public List<VOTriggerDefinition> getTriggerDefinitions() {
        return new ArrayList<>();
    }

    @Override
    public List<TriggerType> getTriggerTypes() {
        return new ArrayList<>();
    }

    @Override
    public void deleteServiceSessionsForSubscription(long subscriptionKey) {

    }

    @Override
    public int getNumberOfServiceSessions(long subscriptionKey) {
        return 0;
    }

    @Override
    public List<VOReport> getAvailableReports(ReportType reportFilterType) {
        return null;
    }

    @Override
    public List<VOMarketplace> getMarketplacesForOrganization() {
        return null;
    }

    @Override
    public List<VOCatalogEntry> getMarketplacesForService(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public VOServiceDetails publishService(VOService service,
            List<VOCatalogEntry> entries) throws ObjectNotFoundException,
            ValidationException, NonUniqueBusinessKeyException,
            OperationNotPermittedException {
        return null;
    }

    @Override
    public VOMarketplace getMarketplaceForSubscription(long subscriptionKey,
            String locale) throws ObjectNotFoundException {
        return null;
    }

    @Override
    public List<VOMarketplace> getMarketplacesOwned() {
        return null;
    }

    @Override
    public List<VOMarketplace> getMarketplacesForOperator() {
        return null;
    }

    @Override
    public VOMarketplace updateMarketplace(VOMarketplace marketplace)
            throws ObjectNotFoundException, OperationNotPermittedException,
            ConcurrentModificationException, ValidationException,
            UserRoleAssignmentException {
        return null;
    }

    @Override
    public VOMarketplace createMarketplace(VOMarketplace marketplace)
            throws OperationNotPermittedException, ObjectNotFoundException,
            ValidationException, UserRoleAssignmentException {
        return null;
    }

    @Override
    public void deleteMarketplace(String marketplaceId)
            throws ObjectNotFoundException {
    }

    @Override
    public void addOrganizationsToMarketplace(List<String> organizationIds,
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException, OrganizationAuthorityException,
            OrganizationAlreadyExistsException,
            MarketplaceAccessTypeUneligibleForOperationException {
    }

    @Override
    public void banOrganizationsFromMarketplace(List<String> organizationIds,
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException, OrganizationAuthorityException,
            OrganizationAlreadyBannedException,
            MarketplaceAccessTypeUneligibleForOperationException {
    }

    @Override
    public void removeOrganizationsFromMarketplace(
            List<String> organizationIds, String marketplaceId)
            throws ObjectNotFoundException, OperationNotPermittedException,
            MarketplaceAccessTypeUneligibleForOperationException,
            OrganizationAuthorityException {
    }

    @Override
    public void liftBanOrganizationsFromMarketplace(
            List<String> organizationIds, String marketplaceId)
            throws ObjectNotFoundException, OperationNotPermittedException,
            MarketplaceAccessTypeUneligibleForOperationException,
            OrganizationAuthorityException {
    }

    @Override
    public List<VOOrganization> getBannedOrganizationsForMarketplace(
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException,
            MarketplaceAccessTypeUneligibleForOperationException {
        return null;
    }

    @Override
    public List<VOOrganization> getOrganizationsForMarketplace(
            String marketplaceId) throws ObjectNotFoundException,
            OperationNotPermittedException,
            MarketplaceAccessTypeUneligibleForOperationException {
        return null;
    }

    @Override
    public VOMarketplace getMarketplaceById(String marketplaceId)
            throws ObjectNotFoundException {
        return null;
    }

    @Override
    public String getBrandingUrl(String marketplaceId)
            throws ObjectNotFoundException {
        return null;
    }

    @Override
    public void saveBrandingUrl(VOMarketplace marketplace, String brandingUrl)
            throws ObjectNotFoundException, ValidationException,
            OperationNotPermittedException, ConcurrentModificationException {
    }

    @Override
    public void refreshLdapUser() throws ValidationException {

    }

    @Override
    public void reportIssue(String subscriptionId, String subject,
            String issueText) throws ValidationException,
            ObjectNotFoundException, OperationNotPermittedException,
            MailOperationException {

    }

    @Override
    public List<VOUdaDefinition> getUdaDefinitionsForCustomer(String supplierId)
            throws ObjectNotFoundException {
        return null;
    }

    @Override
    public List<VOUda> getUdasForCustomer(String targetType,
            long targetObjectKey, String supplierId)
            throws ValidationException, OrganizationAuthoritiesException,
            ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.internal.intf.ServiceProvisioningServiceInternal#
     * getServicesForMarketplace(java.lang.String,
     * org.oscm.internal.types.enumtypes.PerformanceHint)
     */
    @Override
    public List<VOService> getServicesForMarketplace(String marketplaceId,
            PerformanceHint performanceHint) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.internal.intf.ServiceProvisioningServiceInternal#
     * getSuppliedServices (org.oscm.internal.types.enumtypes.PerformanceHint)
     */
    @Override
    public List<VOService> getSuppliedServices(PerformanceHint performanceHint) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.internal.intf.ServiceProvisioningServiceInternal#
     * getTechnicalServices (org.oscm.types.enumtypes.OrganizationRoleType,
     * org.oscm.internal.types.enumtypes.PerformanceHint)
     */
    @Override
    public List<VOTechnicalService> getTechnicalServices(
            OrganizationRoleType role, PerformanceHint performanceHint)
            throws OrganizationAuthoritiesException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.internal.intf.ServiceProvisioningServiceInternal#
     * getPartnerForService(long, java.lang.String)
     */
    @Override
    public VOOrganization getPartnerForService(long serviceKey, String locale)
            throws ObjectNotFoundException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.internal.intf.ServiceProvisioningService#
     * getServiceSellerFallback(long, java.lang.String)
     */
    @Override
    public VOOrganization getServiceSellerFallback(long serviceKey,
            String locale) throws ObjectNotFoundException {
        return new VOOrganization();
    }

    @Override
    public List<VOSubscriptionIdAndOrganizations> getSubscriptionsForTerminate()
            throws OrganizationAuthoritiesException {
        List<VOSubscriptionIdAndOrganizations> result = new ArrayList<>();
        List<String> subscriptionIdentifiers = getSubscriptionIdentifiers();
        for (String subscriptionIdentifier : subscriptionIdentifiers) {
            List<VOOrganization> customers = getCustomersForSubscriptionId(subscriptionIdentifier);
            VOSubscriptionIdAndOrganizations subAndOrgs = new VOSubscriptionIdAndOrganizations();
            subAndOrgs.setSubscriptionId(subscriptionIdentifier);
            subAndOrgs.setOrganizations(customers);
            result.add(subAndOrgs);
        }
        return result;
    }

    @Override
    public boolean isServiceProvider() {
        return false;
    }

    @Override
    public void completeAsyncModifySubscription(String subscriptionId,
            String organizationId, VOInstanceInfo instanceId)
            throws ObjectNotFoundException, SubscriptionStateException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public void abortAsyncModifySubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public void completeAsyncUpgradeSubscription(String subscriptionId,
            String organizationId, VOInstanceInfo instance)
            throws ObjectNotFoundException, SubscriptionStateException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public void abortAsyncUpgradeSubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public POServiceReview writeReview(POServiceReview review)
            throws ValidationException, NonUniqueBusinessKeyException,
            ConcurrentModificationException, ObjectNotFoundException,
            OperationNotPermittedException {
        return null;
    }

    @Override
    public void deleteReview(POServiceReview review)
            throws OperationNotPermittedException, ObjectNotFoundException {
    }

    @Override
    public void deleteReviewByMarketplaceOwner(POServiceReview review,
            String reason) throws OperationNotPermittedException,
            ObjectNotFoundException {
    }

    @Override
    public List<VOServiceOperationParameterValues> getServiceOperationParameterValues(
            VOSubscription subscription, VOTechnicalServiceOperation operation)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TechnicalServiceNotAliveException {
        return new ArrayList<>();
    }

    @Override
    public Properties loadMessagePropertiesFromDB(String marketplaceId,
            String localeString) {
        return new Properties();
    }

    @Override
    public void updateAccessInformation(String subscriptionId,
            String organizationId, VOInstanceInfo instanceInfo)
            throws ObjectNotFoundException, SubscriptionStateException,
            OperationNotPermittedException, ValidationException {
    }

    @Override
    public void importUsersInOwnOrganization(byte[] csvData,
            String marketplaceId) throws BulkUserImportException,
            ObjectNotFoundException {
    }

    @Override
    public void importUsers(byte[] csvData, String organizationId,
            String marketplaceId) throws BulkUserImportException,
            ObjectNotFoundException {
    }

    @Override
    public boolean isCallerOrganizationAdmin() {
        return false;
    }

    @Override
    public boolean addRevokeUserUnitAssignment(String unitName,
            List<VOUser> usersToBeAdded, List<VOUser> usersToBeRevoked)
            throws ObjectNotFoundException, OperationNotPermittedException,
            MailOperationException, NonUniqueBusinessKeyException {
        return false;
    }

    @Override
    public long countRegisteredUsers() {
        return 0;
    }

    @Override
    public void deleteTriggerDefinition(VOTriggerDefinition triggerDefinition)
            throws ObjectNotFoundException, DeletionConstraintException,
            OperationNotPermittedException, ConcurrentModificationException {
    }

    @Override
    public void updateAsyncOperationProgress(String transactionId,
            OperationStatus status, List<VOLocalizedText> progress)
            throws OperationNotPermittedException, OperationStateException {
    }

    @Override
    public boolean searchLdapUsersOverLimit(String userIdPattern)
            throws ValidationException {
        return false;
    }

    @Override
    public void updateAsyncSubscriptionStatus(String subscriptionId,
            String organizationId, VOInstanceInfo instanceInfo)
            throws ObjectNotFoundException {
    }

    @Override
    public void updateActionParameters(long actionKey,
            List<VOTriggerProcessParameter> parameters)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TriggerProcessStatusException, ValidationException {

    }

    @Override
    public VOTriggerProcessParameter getActionParameter(long actionKey,
            TriggerProcessParameterType paramName)
            throws OperationNotPermittedException {
        return null;
    }

    @Override
    public List<VOPaymentInfo> getPaymentInfosForOrgAdmin() {
        return null;
    }

    @Override
    public List<VOReport> getAvailableReportsForOrgAdmin(ReportType filter) {
        return null;
    }

    @Override
    public List<VOServicePaymentConfiguration> getServicePaymentConfiguration(
            PerformanceHint performanceHint) {
        return null;
    }

    @Override
    public void grantUnitRole(VOUser user, UserRoleType role)
            throws ObjectNotFoundException, OperationNotPermittedException {
    }

    @Override
    public void revokeUnitRole(VOUser user, UserRoleType role)
            throws ObjectNotFoundException, OperationNotPermittedException {
    }

    @Override
    public VOServiceListResult getAccesibleServices(String marketplaceId,
            String locale, ListCriteria listCriteria,
            PerformanceHint performanceHint) throws ObjectNotFoundException {
        return new VOServiceListResult();
    }

    @Override
    public VOSubscriptionDetails validateSubscription(VOService service)
            throws OperationNotPermittedException, SubscriptionStateException,
            ObjectNotFoundException {
        return new VOSubscriptionDetails();
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetailsWithoutOwnerCheck(
            long subscriptionKey) throws ObjectNotFoundException {
        return new VOSubscriptionDetails();
    }
 
    @Override
    public List<VOTriggerProcess> getAllActionsForOrganizationRelatedSubscription() {
        List<VOTriggerDefinition> list = getAllDefinitions();

        VOTriggerProcess vo = new VOTriggerProcess();
        vo.setKey(4711l);
        vo.setActivationDate(System.currentTimeMillis());
        vo.setStatus(TriggerProcessStatus.WAITING_FOR_APPROVAL);
        vo.setTriggerDefinition(list.get(0));
        vo.setUser(voUserDetails);
        return Collections.singletonList(vo);
    }
}
