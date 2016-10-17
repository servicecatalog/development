/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Tenant;
import org.oscm.domobjects.TenantSetting;
import org.oscm.internal.types.exception.ObjectNotFoundException;

@Stateless
@LocalBean
public class TenantDao {

    @EJB
    DataService dataManager;

    public List<Tenant> getAllTenants() {
        Query query = dataManager.createNamedQuery("Tenant.getAll");
        return ParameterizedTypes.list(query.getResultList(), Tenant.class);
    }

    public Tenant getTenantByTenantId(String tenantId) throws ObjectNotFoundException {
        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        return (Tenant) dataManager.getReferenceByBusinessKey(tenant);
    }

    public List<TenantSetting> getAllTenantSettingsForTenant(Tenant tenant) {
        Query query = dataManager.createNamedQuery("TenantSetting.getAllForTenant");
        query.setParameter("tenant", tenant);
        return ParameterizedTypes.list(query.getResultList(), TenantSetting.class);
    }

    public List<Tenant> getTenantsByIdPattern(String tenantIdPattern) {
        Query query = dataManager.createNamedQuery("Tenant.getTenantsByIdPattern");
        query.setParameter("tenantIdPattern", tenantIdPattern);
        return ParameterizedTypes.list(query.getResultList(), Tenant.class);
    }

    public Tenant find(long tenantID) {
        return dataManager.find(Tenant.class, tenantID);
    }

    public long doesOrganizationForTenantExist(Tenant tenant) {
        Query query = dataManager.createNamedQuery("Tenant.checkOrganization");
        query.setParameter("tenant", tenant);
        return (long) query.getSingleResult();
    }

    public long doesMarketplaceAssignedToTenantExist(Tenant tenant) {
        Query query = dataManager.createNamedQuery("Tenant.checkMarketplace");
        query.setParameter("tenant", tenant);
        return (long) query.getSingleResult();
    }
    
    public List<String> getNonUniqueOrgUserIdsInTenant(String orgId, long tenantKey) {

        final String queryString = "SELECT groupedusers.userid " + "FROM "
                + "(SELECT users.userid, count(users.orgkey) as numberOfUsers FROM"
                + "(SELECT u.userid, o.tkey as orgkey FROM platformuser u "
                + "LEFT JOIN organization o ON u.organizationkey=o.tkey WHERE o.organizationid=:orgId "
                + "UNION  "
                + "SELECT u.userid, o.tkey as orgkey FROM platformuser u "
                + "LEFT JOIN organization o ON u.organizationkey=o.tkey WHERE (CASE :tenantKey WHEN 0 THEN o.tenant_tkey IS NULL ELSE o.tenant_tkey=:tenantKey END)) "
                + "as users " + "GROUP BY users.userid) " + "as groupedusers "
                + "WHERE groupedusers.numberOfUsers>1";

        Query query = dataManager.createNativeQuery(queryString);
        query.setParameter("orgId", orgId);
        query.setParameter("tenantKey", tenantKey);
        
        return ParameterizedTypes.list(query.getResultList(), String.class);
    }
}
