/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 30.03.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.webtest.setup;

import java.util.ArrayList;
import java.util.List;

import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.vo.VORoleDefinition;
import org.oscm.internal.vo.VOUsageLicense;
import org.oscm.internal.vo.VOUser;

/**
 * @author kulle
 * 
 */
public class SubscriptionAddRevokeUserTask extends WebtestTask {

    private String subscriptionId;
    private String usersToAdd;
    private String usersToRevoke;
    private String serviceRole;

    @Override
    public void executeInternal() throws Exception {
        SubscriptionService ss = getServiceInterface(SubscriptionService.class);

        VORoleDefinition role = null;
        if (!isEmpty(serviceRole)) {
            List<VORoleDefinition> roles = ss
                    .getServiceRolesForSubscription(subscriptionId);
            for (VORoleDefinition r : roles) {
                if (r.getRoleId().equals(serviceRole)) {
                    role = r;
                    break;
                }
            }
        }

        ss.addRevokeUser(subscriptionId, getUsersToBeAdded(role),
                getUsersToBeRevoked());
    }

    protected List<VOUsageLicense> getUsersToBeAdded(VORoleDefinition role) {
        List<VOUsageLicense> result = new ArrayList<VOUsageLicense>();
        for (String key : splitString(usersToAdd)) {
            VOUser user = new VOUser();
            user.setKey(Long.parseLong(key));
            VOUsageLicense license = new VOUsageLicense();
            license.setUser(user);
            license.setRoleDefinition(role);
            result.add(license);
        }
        return result;
    }

    protected List<VOUser> getUsersToBeRevoked() {
        List<VOUser> result = new ArrayList<VOUser>();
        for (String key : splitString(usersToRevoke)) {
            VOUser user = new VOUser();
            user.setKey(Long.parseLong(key));
            result.add(user);
        }

        return result;
    }

    private List<String> splitString(String toSplit) {
        List<String> result = new ArrayList<String>();

        if (toSplit != null) {
            String[] splittedString = toSplit.split(",");
            for (int i = 0; i < splittedString.length; i++) {
                result.add(splittedString[i].trim());
            }
        }

        return result;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getUsersToAdd() {
        return usersToAdd;
    }

    public void setUsersToAdd(String usersToAdd) {
        this.usersToAdd = usersToAdd;
    }

    public String getUsersToRevoke() {
        return usersToRevoke;
    }

    public void setUsersToRevoke(String usersToRevoke) {
        this.usersToRevoke = usersToRevoke;
    }

    public String getServiceRole() {
        return serviceRole;
    }

    public void setServiceRole(String serviceRole) {
        this.serviceRole = serviceRole;
    }

}
