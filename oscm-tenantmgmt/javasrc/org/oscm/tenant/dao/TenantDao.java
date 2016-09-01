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
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Tenant;
import org.oscm.interceptor.ExceptionMapper;

@Stateless
@LocalBean
public class TenantDao {

    @EJB
    DataService dataManager;

    public List<Tenant> getAllTenants() {
        Query query = dataManager.createNamedQuery("Tenant.getAll");
        return ParameterizedTypes.list(query.getResultList(), Tenant.class);
    }

    public Tenant getTenantByTenantId(String tenantId) {
        Query query = dataManager.createNamedQuery("Tenant.findByTenantId");
        query.setParameter("tenantId", tenantId);
        return (Tenant) query.getSingleResult();
    }
}
