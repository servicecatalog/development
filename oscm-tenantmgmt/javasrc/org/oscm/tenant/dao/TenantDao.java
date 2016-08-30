/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.dao;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Tenant;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

@Stateless
@LocalBean
public class TenantDao {

    @EJB(beanInterface = DataService.class)
    DataService dataService;

    public List<Tenant> getAllTenants() {
        Query query = dataService.createNamedQuery("Tenant.getAll");
        return ParameterizedTypes.list(query.getResultList(), Tenant.class);
    }
}
