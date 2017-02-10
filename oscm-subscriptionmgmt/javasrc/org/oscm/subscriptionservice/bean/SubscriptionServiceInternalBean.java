/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: cheld                                                    
 *                                                                              
 *  Creation Date: 17.11.2011                                                      
 *                                                                              
 *  Completion Time: 17.11.2011                                           
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.SubscriptionServiceInternal;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserSubscription;

/**
 * 
 * Implementation of performance optimized search service functionality. This
 * class contains methods that provide performance optimized access. It is
 * intended only as a temporary optimization and will be replaced by other
 * means. Only for internal usage.
 * 
 * @author cheld
 * 
 */
@Stateless
@Remote(SubscriptionServiceInternal.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SubscriptionServiceInternalBean extends SubscriptionServiceBean
        implements SubscriptionServiceInternal {

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "SUBSCRIPTION_MANAGER", "UNIT_ADMINISTRATOR" })
    public List<VOSubscription> getSubscriptionsForOrganization(
            PerformanceHint performanceHint) {
        return super.getSubscriptionsForOrganization(performanceHint);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "SUBSCRIPTION_MANAGER", "UNIT_ADMINISTRATOR" })
    public List<VOSubscription> getAllSubscriptionsForOrganization(
            PerformanceHint performanceHint) {
        return super.getAllSubscriptionsForOrganization(performanceHint);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "SUBSCRIPTION_MANAGER", "UNIT_ADMINISTRATOR" })
    public List<VOSubscription> getSubscriptionsForOrganizationWithFilter(
            Set<SubscriptionStatus> requiredStatus,
            PerformanceHint performanceHint) {
        return super.getSubscriptionsForOrganizationWithFilter(requiredStatus,
                performanceHint);
    }

    @Override
    @RolesAllowed("ORGANIZATION_ADMIN")
    public List<VOUserSubscription> getSubscriptionsForUser(VOUser user,
            PerformanceHint performanceHint) throws ObjectNotFoundException,
            OperationNotPermittedException {
        return super.getSubscriptionsForUser(user, performanceHint);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "SUBSCRIPTION_MANAGER", "UNIT_ADMINISTRATOR" })
    public boolean validateSubscriptionIdForOrganization(String subscriptionId) {
        return super.validateSubscriptionIdForOrganization(subscriptionId);
    }
}
