/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 04.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws.ror;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.oscm.ws.base.AppDbClient;
import org.oscm.ws.base.ServiceFactory;
import org.oscm.ws.base.TechnicalServiceReader;
import org.oscm.ws.base.VOFactory;
import org.oscm.ws.base.WebserviceTestBase;
import org.oscm.intf.MarketplaceService;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.SubscriptionService;
import org.oscm.vo.VOMarketplace;
import org.oscm.vo.VOParameter;
import org.oscm.vo.VOParameterDefinition;
import org.oscm.vo.VOPriceModel;
import org.oscm.vo.VOService;
import org.oscm.vo.VOServiceDetails;
import org.oscm.vo.VOSubscription;
import org.oscm.vo.VOTechnicalService;
import org.oscm.vo.VOUda;

/**
 * @author kulle
 * 
 */
@Ignore
public class PlatformTest {

    private static final boolean USE_ROR_STUB = true;
    private static final String TS_ID = "ROR_VirtualPlatform";
    private static final String FN_VIRTUAL_PLATFORM = "ROR_VirtualPlatform.xml";
    private static final VOFactory factory = new VOFactory();
    private static final Map<String, Object> testProperties = new HashMap<String, Object>();
    private static AppDbClient appDbClient;
    private static Properties configSettings;

    @BeforeClass
    public static void setup() throws Exception {
        appDbClient = new AppDbClient();
        configSettings = WebserviceTestBase.getConfigSetting();
        deleteEmails();
        addCurrency();
        setupSupplierOrganization();
        updateAppConfigurationSettings();
        importVirtualPlatformService();
        publishMarketableService();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        if (appDbClient != null) {
            appDbClient.close();
        }
    }

    private static void deleteEmails() throws Exception {
        WebserviceTestBase.getMailReader().deleteMails();
    }

    private static void addCurrency() throws Exception {
        WebserviceTestBase.getOperator().addCurrency("EUR");
    }

    private static void setupSupplierOrganization() throws Exception {
        Map<String, Object> result = WebserviceTestBase
                .setupSupplier("Supplier");
        testProperties.putAll(result);
    }

    private static void updateAppConfigurationSettings() throws Exception {
        insertRorControllerMapping();
        if (USE_ROR_STUB) {
            insertIaasApiUri();
        }
    }

    private static void insertRorControllerMapping() throws Exception {
        String organizationId = (String) testProperties.get("organizationId");
        appDbClient.insertRorControllerMapping(organizationId);
    }

    private static void insertIaasApiUri() throws Exception {
        String iaasApiUri = (String) configSettings.get("iaas.api.uri");
        appDbClient.insertIaasApiUri(iaasApiUri);
    }

    private static void importVirtualPlatformService() throws Exception {
        String userkey = (String) testProperties.get("userKey");
        configSettings.put("userKey", userkey);
        TechnicalServiceReader tsReader = new TechnicalServiceReader(
                configSettings);
        byte[] bytes = tsReader.loadTechnicalService(FN_VIRTUAL_PLATFORM);
        WebserviceTestBase.importTechnicalService(bytes, userkey);
    }

    private static void publishMarketableService() throws Exception {
        String userkey = (String) testProperties.get("userKey");

        // create
        VOPriceModel voPriceModel = factory.createPriceModelVO();
        List<VOParameter> userParameters = makeParameterConfigurable();
        VOService voService = WebserviceTestBase.createMarketableService(TS_ID,
                userkey, TS_ID, voPriceModel, userParameters);

        // publish
        VOMarketplace voMarketplace = (VOMarketplace) testProperties
                .get("voMarketplace");
        MarketplaceService mps = ServiceFactory.getDefault()
                .getMarketPlaceService(userkey,
                        WebserviceTestBase.DEFAULT_PASSWORD);
        boolean isFree = true;
        VOServiceDetails voServiceDetails = WebserviceTestBase
                .publishToMarketplace(voService, isFree, mps, voMarketplace);

        // activate
        ServiceProvisioningService sps = ServiceFactory.getDefault()
                .getServiceProvisioningService(userkey,
                        WebserviceTestBase.DEFAULT_PASSWORD);
        voService = sps.activateService(voServiceDetails);
        testProperties.put("voService", voService);
    }

    private static List<VOParameter> makeParameterConfigurable()
            throws Exception {

        List<VOParameter> result = new ArrayList<VOParameter>();
        String userkey = (String) testProperties.get("userKey");
        VOTechnicalService ts = WebserviceTestBase.findTechnicalService(
                userkey, TS_ID);
        List<VOParameterDefinition> paramDefinitions = ts
                .getParameterDefinitions();
        for (VOParameterDefinition param : paramDefinitions) {
            if (param.isConfigurable()) {
                VOParameter voParameter = new VOParameter(param);
                voParameter.setConfigurable(true);
                voParameter.setValue(param.getDefaultValue());
                result.add(voParameter);
            }
        }
        return result;
    }

    private VOParameter findParameter(VOService srv, String parameterId) {
        for (VOParameter param : srv.getParameters()) {
            if (parameterId.equals(param.getParameterDefinition()
                    .getParameterId())) {
                return param;
            }
        }
        return null;
    }

    @Ignore
    @Test
    public void subscribeLPlatform() throws Exception {
        // given
        VOSubscription voSubscription = factory
                .createSubscriptionVO("subscription_"
                        + System.currentTimeMillis());
        String supplierUserKey = (String) testProperties.get("userKey");
        VOService voService = (VOService) testProperties.get("voService");
        VOParameter instanceName = findParameter(voService, "INSTANCENAME");
        instanceName.setValue("t" + System.currentTimeMillis());
        SubscriptionService ss = ServiceFactory.getDefault()
                .getSubscriptionService(supplierUserKey);

        // when
        VOSubscription subscription = ss.subscribeToService(voSubscription,
                voService, null, null, null, new ArrayList<VOUda>());

        // then
        assertNotNull(subscription);
    }
}
