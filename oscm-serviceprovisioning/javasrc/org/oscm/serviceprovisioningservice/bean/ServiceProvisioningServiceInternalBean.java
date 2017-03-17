/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: cheld                                                  
 *                                                                              
 *  Creation Date: 20.10.2011                                                      
 *                                                                              
 *  Completion Time: 20.10.2011                                             
 *                                                                              
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.bean;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.ServiceProvisioningServiceInternal;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOTechnicalService;

/**
 * Implementation of performance optimized listing of services. This class is
 * intended only as a temporary optimization and will be replaced by other
 * means. Only for internal usage.
 * 
 * @author cheld
 * 
 */
@Stateless
@Remote(ServiceProvisioningServiceInternal.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class ServiceProvisioningServiceInternalBean extends
        ServiceProvisioningServiceBean implements
        ServiceProvisioningServiceInternal {

    public List<VOService> getServicesForMarketplace(String marketplaceId,
            PerformanceHint performanceHint) {
        return super.getServicesForMarketplace(marketplaceId, performanceHint);
    }

    @RolesAllowed({ "SERVICE_MANAGER", "RESELLER_MANAGER", "BROKER_MANAGER" })
    public List<VOService> getSuppliedServices(PerformanceHint performanceHint) {
        return super.getSuppliedServices(performanceHint);
    }

    @RolesAllowed({ "SERVICE_MANAGER", "TECHNOLOGY_MANAGER" })
    public List<VOTechnicalService> getTechnicalServices(
            OrganizationRoleType role, PerformanceHint performanceHint) {
        return super.getTechnicalServices(role, performanceHint);
    }

}
