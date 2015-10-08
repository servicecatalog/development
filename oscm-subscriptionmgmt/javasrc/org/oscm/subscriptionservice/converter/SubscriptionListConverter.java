/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 24.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.converter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscm.domobjects.RoleDefinition;
import org.oscm.domobjects.Subscription;
import org.oscm.subscriptionservice.local.SubscriptionWithRoles;

/**
 * Converts query result lists better 'readable', type safe format.
 * 
 * @author weiser
 * 
 */
public class SubscriptionListConverter {

    /**
     * Expects a {@link Query} result list containing arrays with the first
     * element being a {@link Subscription} and the second one a
     * {@link RoleDefinition} or <code>null</code>. Converts it to a 'better
     * readable' format.
     * 
     * @param list
     *            the list to convert
     * @return the result
     */
    public List<SubscriptionWithRoles> convert(List<Object[]> list) {
        if (list == null) {
            return new ArrayList<SubscriptionWithRoles>();
        }
        ArrayList<SubscriptionWithRoles> result = new ArrayList<SubscriptionWithRoles>();
        Subscription last = null;
        SubscriptionWithRoles swr = new SubscriptionWithRoles();
        for (Object[] a : list) {
            if (last != a[0]) {
                swr = new SubscriptionWithRoles();
                swr.setSubscription((Subscription) a[0]);
                result.add(swr);
                last = swr.getSubscription();
            }
            if (a[1] != null) {
                swr.getRoles().add((RoleDefinition) a[1]);
            }
        }
        return result;
    }

}
