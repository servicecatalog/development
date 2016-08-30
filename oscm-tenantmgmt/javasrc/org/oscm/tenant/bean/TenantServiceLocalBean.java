/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.bean;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.oscm.domobjects.Tenant;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.tenant.dao.TenantDao;

@RolesAllowed("PLATFORM_OPERATOR")
@Interceptors({ InvocationDateContainer.class, org.oscm.interceptor.ExceptionMapper.class })
@LocalBean
public class TenantServiceLocalBean {

    @EJB(beanInterface = TenantDao.class)
    TenantDao tenantDao;

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<Tenant> getAllTenants() {
        return tenantDao.getAllTenants();
    }
}
