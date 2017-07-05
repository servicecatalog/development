/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *******************************************************************************/
package org.oscm.tenant.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.TenantSetting;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.internal.intf.TenantConfigurationService;
import org.oscm.internal.types.enumtypes.IdpSettingType;
import org.oscm.tenant.local.TenantConfigurationServiceLocal;

/**
 * Created by PLGrubskiM on 2017-06-30.
 */
@Singleton
@Startup
@Local(TenantConfigurationServiceLocal.class)
@Remote(TenantConfigurationService.class)
@Interceptors({ExceptionMapper.class})
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class TenantConfigurationBean implements TenantConfigurationServiceLocal, TenantConfigurationService {

    @EJB(beanInterface = DataService.class)
    DataService dm;

    Map<String, String> tenantIdToHttpMethod;
    Map<String, String> tenantIdToIssuer;
    Map<String, String> tenantIdToIdpUrl;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Schedule(minute = "*/10")
    @Lock(LockType.WRITE)
    public void refreshCache() {
        tenantIdToHttpMethod = getHttpMethodForAllTenants();
        tenantIdToIssuer = getIssuerForAllTenants();
        tenantIdToIdpUrl = getIdpUrlForAllTenants();
    }

    @Override
    public String getHttpMethodForTenant(String tenantId) {
        return tenantIdToHttpMethod.get(tenantId).toString();
    }

    @Override
    public String getIssuerForTenant(String tenantId) {
        return tenantIdToIssuer.get(tenantId).toString();
    }

    @Override
    public String getIdpUrlForTenant(String tenantId) {
        return tenantIdToIdpUrl.get(tenantId.toString());
    }

    private Map<String, String> getHttpMethodForAllTenants() {
        return getSettingForEachTenant(IdpSettingType.SSO_IDP_AUTHENTICATION_REQUEST_HTTP_METHOD);
    }

    private Map<String, String> getIssuerForAllTenants() {
        return getSettingForEachTenant(IdpSettingType.SSO_ISSUER_ID);
    }

    private Map<String, String> getIdpUrlForAllTenants() {
        return getSettingForEachTenant(IdpSettingType.SSO_IDP_URL);
    }

    private Map<String, String> getSettingForEachTenant(IdpSettingType type) {
        Query query = dm.createNamedQuery("TenantSetting.getSettingByNameForAllTenants",
                TenantSetting.class);
        query.setParameter("name", type);
        final List<TenantSetting> resultList = ParameterizedTypes.list(query.getResultList(), TenantSetting.class);

        Map map = new HashMap();
        for (TenantSetting setting : resultList) {
            map.put(setting.getTenant().getTenantId(), setting.getValue());
        }
        return map;
    }
}
