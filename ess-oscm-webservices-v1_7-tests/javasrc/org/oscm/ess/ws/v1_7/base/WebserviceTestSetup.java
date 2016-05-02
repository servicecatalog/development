/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                              
 *  Creation Date: Sep 22, 2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ess.ws.v1_7.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oscm.internal.vo.VOPSP;
import org.oscm.internal.vo.VOPSPAccount;
import com.fujitsu.bss.intf.AccountService;
import com.fujitsu.bss.intf.IdentityService;
import com.fujitsu.bss.intf.MarketplaceService;
import com.fujitsu.bss.intf.ServiceProvisioningService;
import com.fujitsu.bss.intf.SubscriptionService;
import com.fujitsu.bss.intf.TriggerDefinitionService;
import com.fujitsu.bss.intf.TriggerService;
import com.fujitsu.bss.types.enumtypes.OrganizationRoleType;
import com.fujitsu.bss.types.enumtypes.ParameterModificationType;
import com.fujitsu.bss.types.enumtypes.PaymentInfoType;
import com.fujitsu.bss.types.enumtypes.PriceModelType;
import com.fujitsu.bss.types.enumtypes.TriggerProcessStatus;
import com.fujitsu.bss.types.enumtypes.UserRoleType;
import com.fujitsu.bss.vo.VOBillingContact;
import com.fujitsu.bss.vo.VOMarketplace;
import com.fujitsu.bss.vo.VOOrganization;
import com.fujitsu.bss.vo.VOParameter;
import com.fujitsu.bss.vo.VOPaymentInfo;
import com.fujitsu.bss.vo.VOPriceModel;
import com.fujitsu.bss.vo.VOService;
import com.fujitsu.bss.vo.VOServiceActivation;
import com.fujitsu.bss.vo.VOServiceDetails;
import com.fujitsu.bss.vo.VOSubscription;
import com.fujitsu.bss.vo.VOSubscriptionDetails;
import com.fujitsu.bss.vo.VOTechnicalService;
import com.fujitsu.bss.vo.VOTriggerDefinition;
import com.fujitsu.bss.vo.VOTriggerProcess;
import com.fujitsu.bss.vo.VOUda;
import com.fujitsu.bss.vo.VOUsageLicense;
import com.fujitsu.bss.vo.VOUserDetails;

/**
 * @author barzu
 */
public class WebserviceTestSetup {

    private static final Set<TriggerProcessStatus> CANCEL_STATES = Collections
            .unmodifiableSet(EnumSet.of(
                    TriggerProcessStatus.WAITING_FOR_APPROVAL,
                    TriggerProcessStatus.INITIAL));

    private AccountService accSrvAsSupplier;
    private ServiceProvisioningService serviceProvisioningSrvAsSupplier,
            serviceProvisioningSrvAsTechnologyProvider;

    private MarketplaceService mpSrvAsSupplier;
    protected IdentityService identitySrvAsSupplier;

    private IdentityService identitySrvAsTechnologyProvider;
    private VOTechnicalService voTechnicalService;
    private VOTechnicalService voAsynTechnicalService;

    private VOUserDetails voCustomerUser;
    private VOPaymentInfo voCustomerPaymentInfo;
    private VOBillingContact voCustomerBillingContact;
    private String supplierUserKey;
    private String supplierUserId;
    private String resellerUserKey;
    private String resellerUserId;
    private String technologyProviderUserKey;
    private String serviceManagerUserKey;
    private String technologyManagerUserKey;
    private AccountService accSrvCustomer;
    private VOFactory factory = new VOFactory();

    private VOTechnicalService voTechnicalServiceWithOperations;

    public VOOrganization createSupplier(String namePrefix) throws Exception {
        supplierUserId = namePrefix + "_"
                + WebserviceTestBase.createUniqueKey();
        VOOrganization supplier = WebserviceTestBase.createOrganization(
                supplierUserId, namePrefix,
                OrganizationRoleType.TECHNOLOGY_PROVIDER,
                OrganizationRoleType.SUPPLIER);
        supplierUserKey = WebserviceTestBase.readLastMailAndSetCommonPassword();
        WebserviceTestBase.savePaymentInfoToSupplier(supplier,
                PaymentInfoType.INVOICE);
        accSrvAsSupplier = ServiceFactory.getDefault().getAccountService(
                supplierUserKey, WebserviceTestBase.DEFAULT_PASSWORD);
        WebserviceTestBase.setDefaultPaymentType(accSrvAsSupplier,
                PaymentInfoType.INVOICE);

        serviceProvisioningSrvAsSupplier = ServiceFactory.getDefault()
                .getServiceProvisioningService(supplierUserKey,
                        WebserviceTestBase.DEFAULT_PASSWORD);
        mpSrvAsSupplier = ServiceFactory.getDefault().getMarketPlaceService(
                supplierUserKey, WebserviceTestBase.DEFAULT_PASSWORD);
        identitySrvAsSupplier = ServiceFactory.getDefault().getIdentityService(
                supplierUserKey, WebserviceTestBase.DEFAULT_PASSWORD);

        // create (non admin) user with role SERVICE_MANAGER
        List<UserRoleType> userRoles = new ArrayList<UserRoleType>();
        userRoles.add(UserRoleType.SERVICE_MANAGER);

        VOUserDetails user = factory.createUserVO("ServiceManager" + "_"
                + WebserviceTestBase.createUniqueKey());

        user.setOrganizationId(supplier.getOrganizationId());

        identitySrvAsSupplier.createUser(user, userRoles,
                getGlobalMarketplaceId());
        serviceManagerUserKey = WebserviceTestBase
                .readLastMailAndSetCommonPassword();

        return supplier;
    }

    public VOOrganization createReseller(String namePrefix) throws Exception {
        resellerUserId = namePrefix + "_"
                + WebserviceTestBase.createUniqueKey();
        VOOrganization reseller = WebserviceTestBase.createOrganization(
                resellerUserId, namePrefix, OrganizationRoleType.RESELLER);
        resellerUserKey = WebserviceTestBase.readLastMailAndSetCommonPassword();
        List<VOPSP> psps = WebserviceTestBase.getOperator().getPSPs();
        for (VOPSP voPsp : psps) {
            VOPSPAccount newPspAccount = new VOPSPAccount();
            newPspAccount.setPsp(voPsp);
            newPspAccount.setPspIdentifier("123");
            WebserviceTestBase.getOperator().savePSPAccount(
                    VOConverter.convertToUp(reseller), newPspAccount);
        }

        // add payment types AVAILABLE for customers
        Set<String> types = new HashSet<String>();
        types.add(PaymentInfoType.INVOICE.name());
        types.add(PaymentInfoType.CREDIT_CARD.name());
        types.add(PaymentInfoType.DIRECT_DEBIT.name());
        ServiceFactory
                .getDefault()
                .getOperatorService()
                .addAvailablePaymentTypes(VOConverter.convertToUp(reseller),
                        types);
        WebserviceTestBase.savePaymentInfoToSupplier(reseller,
                PaymentInfoType.CREDIT_CARD);
        accSrvAsSupplier = ServiceFactory.getDefault().getAccountService(
                resellerUserKey, WebserviceTestBase.DEFAULT_PASSWORD);
        WebserviceTestBase.setDefaultPaymentType(accSrvAsSupplier,
                PaymentInfoType.CREDIT_CARD);

        serviceProvisioningSrvAsSupplier = ServiceFactory.getDefault()
                .getServiceProvisioningService(supplierUserKey,
                        WebserviceTestBase.DEFAULT_PASSWORD);
        mpSrvAsSupplier = ServiceFactory.getDefault().getMarketPlaceService(
                supplierUserKey, WebserviceTestBase.DEFAULT_PASSWORD);
        identitySrvAsSupplier = ServiceFactory.getDefault().getIdentityService(
                supplierUserKey, WebserviceTestBase.DEFAULT_PASSWORD);

        // create (non admin) user with role RESELLER_MANAGER
        List<UserRoleType> userRoles = new ArrayList<UserRoleType>();
        userRoles.add(UserRoleType.RESELLER_MANAGER);

        VOUserDetails user = factory.createUserVO("ServiceManager" + "_"
                + WebserviceTestBase.createUniqueKey());

        user.setOrganizationId(reseller.getOrganizationId());

        return reseller;
    }

    public VOOrganization createTechnologyProvider(String namePrefix)
            throws Exception {
        String technologyProviderUserId = namePrefix + "_"
                + WebserviceTestBase.createUniqueKey();
        VOOrganization technologyProvider = WebserviceTestBase
                .createOrganization(technologyProviderUserId, namePrefix,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER);
        technologyProviderUserKey = WebserviceTestBase
                .readLastMailAndSetCommonPassword();

        serviceProvisioningSrvAsTechnologyProvider = ServiceFactory
                .getDefault().getServiceProvisioningService(
                        technologyProviderUserKey,
                        WebserviceTestBase.DEFAULT_PASSWORD);

        // create (non admin) user with role TECHNOLOGY_MANAGER
        List<UserRoleType> userRoles = new ArrayList<UserRoleType>();
        userRoles.add(UserRoleType.TECHNOLOGY_MANAGER);

        VOUserDetails user = factory.createUserVO("TechnologyManager" + "_"
                + WebserviceTestBase.createUniqueKey());

        user.setOrganizationId(technologyProvider.getOrganizationId());
        identitySrvAsTechnologyProvider = ServiceFactory.getDefault()
                .getIdentityService(supplierUserKey,
                        WebserviceTestBase.DEFAULT_PASSWORD);

        identitySrvAsTechnologyProvider.createUser(user, userRoles,
                getGlobalMarketplaceId());
        technologyManagerUserKey = WebserviceTestBase
                .readLastMailAndSetCommonPassword();

        return technologyProvider;
    }

    public VOTechnicalService createTechnicalService() throws Exception {
        return createTechnicalService(serviceProvisioningSrvAsSupplier);
    }

    public VOTechnicalService createAsynTechnicalService(
            String technicalProductId) throws Exception {
        return createAsynTechnicalService(serviceProvisioningSrvAsSupplier,
                technicalProductId);
    }

    /**
     * Creates the technical service including the operation 'SNAPSHOT' with
     * parameters 'SERVER' and 'COMMENT'. Without events, parameters and roles.
     * 
     * @param technicalProductId
     *            the technical service id
     * @param service
     *            the {@link ServiceProvisioningService} to use
     * @return the {@link VOTechnicalService}
     * @throws Exception
     */
    public VOTechnicalService createTechnicalServiceWithOperationsAndOperationParameters(
            String technicalProductId, ServiceProvisioningService service)
            throws Exception {
        String tsxml = TSXMLForWebService
                .createTSXMLWithOpsAndOpParams(technicalProductId);
        voTechnicalServiceWithOperations = WebserviceTestBase
                .createTechnicalService(tsxml, technicalProductId, service);
        return voTechnicalServiceWithOperations;
    }

    public VOTechnicalService createTechnicalServiceWithParameterDefinition(
            String serviceId) throws Exception {
        return createTechnicalServiceWithParameterDefinition(
                serviceProvisioningSrvAsSupplier, serviceId);
    }

    public VOTechnicalService createTechnicalServiceWithParameterDefinition()
            throws Exception {
        return createTechnicalServiceWithParameterDefinition(
                serviceProvisioningSrvAsSupplier, null);
    }

    public VOTechnicalService createTechnicalServiceWithParameterDefinition(
            ServiceProvisioningService service, String serviceId)
            throws Exception {
        String tsxml = "";
        if (serviceId != null) {
            tsxml = TSXMLForWebService
                    .createTSXMLWithModificationTypeAttribute(serviceId);
        } else {
            tsxml = TSXMLForWebService
                    .createTSXMLWithModificationTypeAttribute();
        }
        VOTechnicalService voTechnicalServiceWithParaDef = WebserviceTestBase
                .createTechnicalService(tsxml, service);
        return voTechnicalServiceWithParaDef;
    }

    public VOTechnicalService createTechnicalService(
            ServiceProvisioningService service) throws Exception {
        String tsxml = TSXMLForWebService
                .createTSXMLWithAllowingOnBehalfActingConnectable(Boolean.FALSE
                        .toString());
        voTechnicalService = WebserviceTestBase.createTechnicalService(tsxml,
                service);
        return voTechnicalService;
    }

    public VOTechnicalService createAsynTechnicalService(
            ServiceProvisioningService service, String technicalProductId)
            throws Exception {
        String tsxml = TSXMLForWebService.createAsynTSXML();
        voAsynTechnicalService = WebserviceTestBase.createAsynTechnicalService(
                tsxml, technicalProductId, service);
        return voAsynTechnicalService;
    }

    public void addTags(VOTechnicalService technicalService, List<String> tags)
            throws Exception {
        technicalService.setTags(tags);
        serviceProvisioningSrvAsSupplier
                .saveTechnicalServiceLocalization(technicalService);
    }

    public VOService createAndActivateService(String namePrefix,
            VOMarketplace voMarketPlace) throws Exception {
        VOPriceModel priceModel = factory.createPriceModelVO("EUR");
        return createAndActivateService(namePrefix, voMarketPlace, priceModel);
    }

    public VOService createAndActivateService(String namePrefix,
            VOMarketplace voMarketPlace, VOPriceModel priceModel)
            throws Exception {
        VOServiceDetails serviceDetails = createService(namePrefix,
                voMarketPlace);
        serviceDetails = savePriceModel(serviceDetails, priceModel);
        return activateService(serviceDetails);
    }

    public VOService createAndActivateAsynService(String namePrefix,
            VOMarketplace voMarketPlace, VOPriceModel priceModel)
            throws Exception {
        VOServiceDetails serviceDetails = createAsynService(namePrefix,
                voMarketPlace);
        serviceDetails = savePriceModel(serviceDetails, priceModel);
        return activateService(serviceDetails);
    }

    public VOService createAndActivateService(String namePrefix,
            VOTechnicalService voTS, VOMarketplace voMarketplace,
            List<VOParameter> paras) throws Exception {
        VOPriceModel priceModel = factory.createPriceModelVO("EUR");
        priceModel.setType(PriceModelType.FREE_OF_CHARGE);
        VOServiceDetails serviceDetails = createService(namePrefix, voTS,
                voMarketplace, paras);
        serviceDetails = savePriceModel(serviceDetails, priceModel);
        return activateService(serviceDetails);
    }

    public VOServiceDetails createService(String namePrefix,
            VOMarketplace voMarketPlace) throws Exception {
        VOServiceDetails serviceDetails = serviceProvisioningSrvAsSupplier
                .createService(
                        voTechnicalService,
                        factory.createMarketableServiceVO(namePrefix + "_"
                                + WebserviceTestBase.createUniqueKey()), null);
        WebserviceTestBase.publishToMarketplace(serviceDetails, true,
                mpSrvAsSupplier, voMarketPlace);
        return serviceDetails;
    }

    public VOServiceDetails createService(String namePrefix,
            VOTechnicalService voTS, VOMarketplace voMarketPlace,
            List<VOParameter> paras) throws Exception {
        VOService voService = factory.createMarketableServiceVO(namePrefix
                + "_" + WebserviceTestBase.createUniqueKey());
        voService.setParameters(paras);
        VOServiceDetails serviceDetails = serviceProvisioningSrvAsSupplier
                .createService(voTS, voService, null);
        WebserviceTestBase.publishToMarketplace(serviceDetails, true,
                mpSrvAsSupplier, voMarketPlace);
        return serviceDetails;
    }

    public VOServiceDetails createService(String namePrefix) throws Exception {
        VOServiceDetails serviceDetails = serviceProvisioningSrvAsSupplier
                .createService(
                        voTechnicalService,
                        factory.createMarketableServiceVO(namePrefix + "_"
                                + WebserviceTestBase.createUniqueKey()), null);
        return serviceDetails;
    }

    public VOServiceDetails createAsynService(String namePrefix,
            VOMarketplace voMarketPlace) throws Exception {
        VOServiceDetails serviceDetails = serviceProvisioningSrvAsSupplier
                .createService(
                        voAsynTechnicalService,
                        factory.createMarketableServiceVO(namePrefix + "_"
                                + WebserviceTestBase.createUniqueKey()), null);
        WebserviceTestBase.publishToMarketplace(serviceDetails, true,
                mpSrvAsSupplier, voMarketPlace);
        return serviceDetails;
    }

    public VOService createFreeService(String namePrefix) throws Exception {
        VOService freeService = createService(namePrefix);
        assertNotNull(freeService);
        assertTrue(freeService.getKey() > 0);
        return freeService;
    }

    public VOService savePriceModelAndActivateService(VOPriceModel priceModel,
            VOServiceDetails serviceDetails) throws Exception {
        serviceDetails = savePriceModel(serviceDetails, priceModel);
        return activateService(serviceDetails);
    }

    public VOServiceDetails savePriceModel(VOServiceDetails serviceDetails,
            VOPriceModel voPriceModel) throws Exception {
        return serviceProvisioningSrvAsSupplier.savePriceModel(serviceDetails,
                voPriceModel);
    }

    public VOService activateService(VOService serviceDetails) throws Exception {
        return serviceProvisioningSrvAsSupplier.activateService(serviceDetails);
    }

    public VOService deactivateService(VOService serviceDetails)
            throws Exception {
        return serviceProvisioningSrvAsSupplier
                .deactivateService(serviceDetails);
    }

    public List<VOService> setActivationStates(
            List<VOServiceActivation> activations) throws Exception {
        return serviceProvisioningSrvAsSupplier
                .setActivationStates(activations);
    }

    public VOOrganization createCustomer(String namePrefix) throws Exception {
        VOOrganization voCustomer = registerCustomerForSupplier(namePrefix);
        voCustomerUser.setKey(Long.parseLong(WebserviceTestBase
                .readLastMailAndSetCommonPassword()));
        accSrvCustomer = ServiceFactory.getDefault().getAccountService(
                String.valueOf(voCustomerUser.getKey()),
                WebserviceTestBase.DEFAULT_PASSWORD);
        List<VOPaymentInfo> paymentInfos = accSrvCustomer.getPaymentInfos();
        assertEquals("Only the default INVOICE payment info expected", 1,
                paymentInfos.size());
        voCustomerPaymentInfo = paymentInfos.get(0);
        voCustomerBillingContact = accSrvCustomer.saveBillingContact(factory
                .createBillingContactVO());
        return voCustomer;
    }

    public AccountService getAccountServiceForCustomer() {
        return accSrvCustomer;
    }

    public VOOrganization registerCustomerForSupplier(String namePrefix)
            throws Exception {
        VOOrganization voCustomer = factory.createOrganizationVO();
        voCustomer.setName(namePrefix);
        voCustomerUser = factory.createUserVO(namePrefix + "_"
                + WebserviceTestBase.createUniqueKey());
        return accSrvAsSupplier.registerKnownCustomer(voCustomer,
                voCustomerUser, null, getGlobalMarketplaceId());
    }

    public VOSubscriptionDetails createSubscription(String namePrefix,
            VOService service) throws Exception {
        VOSubscription subscription = factory.createSubscriptionVO(namePrefix
                + "_" + WebserviceTestBase.createUniqueKey());
        List<VOUsageLicense> users = new ArrayList<VOUsageLicense>();
        users.add(factory.createUsageLicenceVO(voCustomerUser.getUserId()));
        SubscriptionService subscriptionSV = ServiceFactory.getDefault()
                .getSubscriptionService(
                        String.valueOf(voCustomerUser.getKey()),
                        WebserviceTestBase.DEFAULT_PASSWORD);
        subscription = subscriptionSV.subscribeToService(subscription, service,
                users, voCustomerPaymentInfo, voCustomerBillingContact,
                new ArrayList<VOUda>());
        return subscriptionSV.getSubscriptionDetails(subscription
                .getSubscriptionId());
    }

    public ServiceProvisioningService getServiceProvisioningSrvAsSupplier() {
        return serviceProvisioningSrvAsSupplier;
    }

    public ServiceProvisioningService getServiceProvisioningSrvAsTechnologyProvider() {
        return serviceProvisioningSrvAsTechnologyProvider;
    }

    public MarketplaceService getMpSrvAsSupplier() {
        return mpSrvAsSupplier;
    }

    public IdentityService getIdentitySrvAsSupplier() {
        return identitySrvAsSupplier;
    }

    public AccountService getAccountServiceAsSupplier() {
        return accSrvAsSupplier;
    }

    public VOUserDetails getCustomerUser() {
        return voCustomerUser;
    }

    public VOPaymentInfo getCustomerPaymentInfo() {
        return voCustomerPaymentInfo;
    }

    public VOBillingContact getCustomerBillingContact() {
        return voCustomerBillingContact;
    }

    public String getSupplierUserKey() {
        return supplierUserKey;
    }

    public String getSupplierUserId() {
        return supplierUserId;
    }

    public String getTechnologyProviderUserKey() {
        return technologyProviderUserKey;
    }

    public String getServiceManagerUserKey() {
        return serviceManagerUserKey;
    }

    public String getTechnologyManagerUserKey() {
        return technologyManagerUserKey;
    }

    public VOTechnicalService getVoTechnicalService() {
        return voTechnicalService;
    }

    public String getResellerUserKey() {
        return resellerUserKey;
    }

    public String getResellerUserId() {
        return resellerUserId;
    }

    public void createTriggerDefinition(VOTriggerDefinition voTriggerDefinition)
            throws Exception {
        createTriggerDefinition(voTriggerDefinition, getSupplierUserKey());
    }

    public void createTriggerDefinition(
            VOTriggerDefinition voTriggerDefinition, String userKey)
            throws Exception {
        TriggerDefinitionService tds = ServiceFactory.getDefault()
                .getTriggerDefinitionService(userKey,
                        WebserviceTestBase.DEFAULT_PASSWORD);
        tds.createTriggerDefinition(voTriggerDefinition);
    }

    public void deleteTriggersForUser() throws Exception {
        deleteTriggersForUser(getSupplierUserKey());
    }

    public void deleteTriggersForUser(String userKey) throws Exception {
        TriggerDefinitionService tds = ServiceFactory.getDefault()
                .getTriggerDefinitionService(userKey,
                        WebserviceTestBase.DEFAULT_PASSWORD);
        List<VOTriggerDefinition> triggerDefinitions = tds
                .getTriggerDefinitions();
        if (!triggerDefinitions.isEmpty()) {
            Thread.sleep(2000); // wait for the queue to process the trigger
                                // process
            TriggerService ts = ServiceFactory.getDefault().getTriggerService(
                    userKey, WebserviceTestBase.DEFAULT_PASSWORD);
            List<VOTriggerProcess> processes = ts.getAllActions();
            List<Long> processKeysToCancel = new ArrayList<Long>();
            List<Long> processKeysToDelete = new ArrayList<Long>();
            for (VOTriggerProcess process : processes) {
                if (CANCEL_STATES.contains(process.getStatus())) {
                    processKeysToCancel.add(Long.valueOf(process.getKey()));
                }
                processKeysToDelete.add(Long.valueOf(process.getKey()));
            }
            ts.cancelActions(processKeysToCancel, null);
            ts.deleteActions(processKeysToDelete);

            for (VOTriggerDefinition def : triggerDefinitions) {
                tds.deleteTriggerDefinition(def.getKey());
            }
        }
    }

    public String getGlobalMarketplaceId() throws Exception {
        return WebserviceTestBase.getGlobalMarketplaceId();
    }

    public VOTechnicalService createTechnicalServiceWithParameterDefinition(
            String serviceId, ParameterModificationType modificationType)
            throws Exception {
        return createTechnicalServiceWithParameterDefinition(
                serviceProvisioningSrvAsSupplier, serviceId, modificationType);
    }

    public VOTechnicalService createTechnicalServiceWithParameterDefinition(
            ServiceProvisioningService service, String serviceId,
            ParameterModificationType modificationType) throws Exception {

        VOTechnicalService voTechnicalServiceWithParaDef = null;
        String tsxml = "";
        if (modificationType != null) {
            tsxml = TSXMLForWebService
                    .createTSXMLWithModificationTypeAttribute(serviceId,
                            modificationType.name());
            voTechnicalServiceWithParaDef = WebserviceTestBase
                    .createTechnicalService(tsxml, serviceId, service);
        } else {
            tsxml = TSXMLForWebService
                    .createTSXMLWithModificationTypeAttribute();
            voTechnicalServiceWithParaDef = WebserviceTestBase
                    .createTechnicalService(tsxml, service);
        }

        return voTechnicalServiceWithParaDef;
    }

    public VOServiceDetails createService(String namePrefix,
            VOTechnicalService voTS, VOMarketplace voMarketPlace)
            throws Exception {
        VOServiceDetails serviceDetails = serviceProvisioningSrvAsSupplier
                .createService(
                        voTS,
                        factory.createMarketableServiceVO(namePrefix + "_"
                                + WebserviceTestBase.createUniqueKey()), null);
        WebserviceTestBase.publishToMarketplace(serviceDetails, true,
                mpSrvAsSupplier, voMarketPlace);
        return serviceDetails;
    }

    public byte[] exportTechnicalService(
            List<VOTechnicalService> technicalServices) throws Exception {
        byte[] content = serviceProvisioningSrvAsSupplier
                .exportTechnicalServices(technicalServices);
        return content;
    }

}
