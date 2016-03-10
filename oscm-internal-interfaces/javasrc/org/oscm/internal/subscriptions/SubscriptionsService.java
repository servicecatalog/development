/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Nov 12, 2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.subscriptions;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;

import org.oscm.internal.components.response.Response;
import org.oscm.internal.tables.Pagination;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.vo.VOSubscriptionDetails;

/**
 * @author tokoda
 * 
 */
@Remote
public interface SubscriptionsService {

    /**
     * Retrieves all the subscriptions for which the calling organization is
     * subscribing.
     * 
     * @param states
     *            the set of subscription status in which subscriptions should
     *            be returned. If status is null, all subscriptions owned by the
     *            organization are returned.
     * @return A <code>Response</code> object containing the subscription data
     *         as <code>POSubscriptionForList</code>.
     */
    public Response getSubscriptionsForOrg(Set<SubscriptionStatus> states);

    /**
     * Retrieves the subscriptions for the user who is currently logged in. The
     * result is wrapped into a response object.
     */
    public Response getMySubscriptions();

    /**
     * Retrieves the subscriptions and customers for the user who is currently
     * logged in. The result is wrapped into a response object.
     * 
     * @exception OrganizationAuthoritiesException
     */
    public Response getSubscriptionsAndCustomersForManagers()
            throws OrganizationAuthoritiesException;

    /**
     * Retrieves the subscriptions and customers for the user who is currently
     * logged in. The result contains a list which is in a subscriptions range
     * specified with the pagination parameter. The result is wrapped into a
     * response object.
     * 
     * @exception OrganizationAuthoritiesException
     */
    public Response getSubscriptionsAndCustomersForManagers(
            Pagination pagination) throws OrganizationAuthoritiesException;


    Response getSubscriptionsForOrg(Set<SubscriptionStatus> states, Pagination pagination)
            throws OrganizationAuthoritiesException;

    @RolesAllowed({ "ORGANIZATION_ADMIN", "SUBSCRIPTION_MANAGER",
            "UNIT_ADMINISTRATOR" })
    Integer getSubscriptionsForOrgSizeWithFiltering(Set<SubscriptionStatus> states,
                                                    org.oscm.paginator.Pagination pagination) throws OrganizationAuthoritiesException;

    Response getMySubscriptions(Pagination pagination) throws OrganizationAuthoritiesException;

    Integer getSubscriptionsAndCustomersForManagersSize(Pagination pagination) throws OrganizationAuthoritiesException;

    Response getSubscriptionsForOrgWithFiltering(Set<SubscriptionStatus> states, org.oscm.paginator.Pagination pagination)
            throws OrganizationAuthoritiesException;

    Integer getSubscriptionsForOrgSize(Set<SubscriptionStatus> states, Pagination pagination)
            throws OrganizationAuthoritiesException;

    public Response getMySubscriptionsWithFiltering(org.oscm.paginator.Pagination pagination) throws OrganizationAuthoritiesException;

    Integer getMySubscriptionsSizeWithFiltering(org.oscm.paginator.Pagination pagination) throws OrganizationAuthoritiesException;

    Integer getMySubscriptionsSize(Pagination pagination) throws OrganizationAuthoritiesException;
    
    boolean isSubscriptionVisible(long subscriptionKey);

    VOSubscriptionDetails getSubscriptionDetails(long subscriptionKey) throws ObjectNotFoundException;

    boolean isCurrentUserAssignedToSubscription(long subscriptionKey);

    POSubscription getMySubscriptionDetails(long key);
}
