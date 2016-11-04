/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 11.10.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.samlsp.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.junit.Before;
import org.junit.Test;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.TenantService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.IdpSettingType;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOTenant;
import org.oscm.internal.vo.VOTenantSetting;
import org.oscm.intf.IdentityService;
import org.oscm.samlsp.ws.base.WebserviceSAMLSPTestSetup;
import org.oscm.types.constants.Configuration;
import org.oscm.types.enumtypes.OrganizationRoleType;
import org.oscm.vo.VOOrganization;
import org.oscm.vo.VOUserDetails;
import org.oscm.ws.base.ServiceFactory;
import org.oscm.ws.base.WebserviceTestBase;

public class TenantRelatedSampleWSTest {

    private final static String USER_ID_FOR_TENANT = "IntegrationTest_UserId_1000";
    private final static String USER_ID = "IntegrationTest_UserId_2000";

    private TenantService tenantService;
    private IdentityService identityService;
    private ConfigurationService configurationService;

    private static final String SSO_STS_URL = "SSO_STS_URL";
    private static final String SSO_STS_ENCKEY_LEN = "SSO_STS_ENCKEY_LEN";
    private static final String SSO_STS_METADATA_URL = "SSO_STS_METADATA_URL";

    private static final String TENANT_ID_1 = "tenant1";
    private static final String TENANT_ID_2 = "tenant2";
    private static final String TENANT_ID_3 = "tenant3";
    private static final String TENANT_ID_4 = "tenant4";
    private static final String TENANT_ID_5 = "tenant5";

    @Before
    public void setUp() throws Exception {
        new WebserviceSAMLSPTestSetup();

        tenantService = ServiceFactory.getDefault().getTenantService();
        configurationService = ServiceFactory.getDefault()
                .getConfigurationService();
    }

    @Test
    public void testWSWithValidTenant() throws Exception {

        // given
        VOTenant tenant = createTenantWithSettings(TENANT_ID_1);
        WebserviceTestBase.createOrganization(USER_ID_FOR_TENANT, "org1",
                tenant.getKey(), OrganizationRoleType.SUPPLIER);

        identityService = ServiceFactory.getSTSServiceFactory(TENANT_ID_1, null)
                .getIdentityService(USER_ID_FOR_TENANT, "secret");

        // when
        VOUserDetails user = invokeSampleWSMethod();

        // then
        assertEquals(USER_ID_FOR_TENANT, user.getUserId());
    }
    
    /*
    @Test
    public void testWSWithDuplicatedUserId() throws Exception {

        // given
        VOTenant tenant = createTenantWithSettings(TENANT_ID_2);
        VOOrganization org2 = WebserviceTestBase.createOrganization(
                USER_ID_FOR_TENANT, "org2", tenant.getKey(),
                OrganizationRoleType.SUPPLIER);

        VOTenant anotherTenant = createTenantWithSettings(TENANT_ID_3);
        VOOrganization org3 = WebserviceTestBase.createOrganization(
                USER_ID_FOR_TENANT, "org3", anotherTenant.getKey(),
                OrganizationRoleType.SUPPLIER);

        IdentityService idServiceForFirstTenant = ServiceFactory
                .getSTSServiceFactory(TENANT_ID_2, null)
                .getIdentityService(USER_ID_FOR_TENANT, "secret");

        IdentityService idServiceForSecTenant = ServiceFactory
                .getSTSServiceFactory(TENANT_ID_3, null)
                .getIdentityService(USER_ID_FOR_TENANT, "secret");

        // when
        VOUserDetails userFirst = idServiceForFirstTenant
                .getCurrentUserDetails();
        VOUserDetails userSec = idServiceForSecTenant.getCurrentUserDetails();

        // then
        assertEquals(USER_ID_FOR_TENANT, userFirst.getUserId());
        assertEquals(USER_ID_FOR_TENANT, userSec.getUserId());
        assertFalse(userFirst.getKey() == userSec.getKey());
        assertEquals(org2.getOrganizationId(), userFirst.getOrganizationId());
        assertEquals(org3.getOrganizationId(), userSec.getOrganizationId());
    }

    @Test(expected = WebServiceException.class)
    public void testWSWithNotExistingTenant() throws Exception {

        // given
        WebserviceTestBase.createOrganization(USER_ID,
                OrganizationRoleType.SUPPLIER);
        identityService = ServiceFactory.getSTSServiceFactory(TENANT_ID_4, null)
                .getIdentityService(USER_ID, "secret");

        // when
        invokeSampleWSMethod();

        // then
        // expecting javax.xml.ws.WebServiceException
    }
    
    @Test
    public void testWSWithOrganizationInContext() throws Exception {

        // given
        
        VOTenant tenant = createTenantWithSettings(TENANT_ID_5);
        VOOrganization org = WebserviceTestBase.createOrganization(
                USER_ID_FOR_TENANT, "org5", tenant.getKey(),
                OrganizationRoleType.SUPPLIER);
        
        identityService = ServiceFactory.getSTSServiceFactory(null, org.getOrganizationId())
                .getIdentityService(USER_ID_FOR_TENANT, "secret");

        // when
        VOUserDetails user = invokeSampleWSMethod();

        // then
        assertEquals(USER_ID_FOR_TENANT, user.getUserId());
        assertEquals(org.getOrganizationId(), user.getOrganizationId());
    }
*/
    private VOUserDetails invokeSampleWSMethod() throws Exception {
        VOUserDetails user = identityService.getCurrentUserDetails();
        return user;
    }

    private VOTenant createTenantWithSettings(String tenantId)
            throws Exception {
        WebserviceSAMLSPTestSetup.createTenant(tenantId);
        VOTenant tenant = tenantService.getTenantByTenantId(tenantId);

        VOConfigurationSetting stsUrlSetting = configurationService
                .getVOConfigurationSetting(
                        ConfigurationKey.valueOf(SSO_STS_URL),
                        Configuration.GLOBAL_CONTEXT);
        VOConfigurationSetting stsMetadataUrlSetting = configurationService
                .getVOConfigurationSetting(
                        ConfigurationKey.valueOf(SSO_STS_METADATA_URL),
                        Configuration.GLOBAL_CONTEXT);
        VOConfigurationSetting stsKeyLenSetting = configurationService
                .getVOConfigurationSetting(
                        ConfigurationKey.valueOf(SSO_STS_ENCKEY_LEN),
                        Configuration.GLOBAL_CONTEXT);

        List<VOTenantSetting> settings = new ArrayList<>();
        settings.add(getTenantSetting(SSO_STS_URL, stsUrlSetting.getValue(),
                tenant));
        settings.add(getTenantSetting(SSO_STS_METADATA_URL,
                stsMetadataUrlSetting.getValue(), tenant));
        settings.add(getTenantSetting(SSO_STS_ENCKEY_LEN,
                stsKeyLenSetting.getValue(), tenant));

        tenantService.addTenantSettings(settings, tenant);
        return tenant;
    }

    private VOTenantSetting getTenantSetting(String key, String value,
            VOTenant tenant) {

        VOTenantSetting voTenantSetting = new VOTenantSetting();
        voTenantSetting.setName(IdpSettingType.valueOf(key));
        voTenantSetting.setValue(value);
        voTenantSetting.setVoTenant(tenant);
        return voTenantSetting;
    }

}
