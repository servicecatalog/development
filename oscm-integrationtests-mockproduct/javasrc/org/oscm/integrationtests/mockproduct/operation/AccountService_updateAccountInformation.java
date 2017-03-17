/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: weiser                                                     
 *                                                                              
 *  Creation Date: 28.10.2010                                                      
 *                                                                              
 *  Completion Time: 28.10.2010                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.AccountService;
import org.oscm.types.enumtypes.Salutation;
import org.oscm.types.enumtypes.UserRoleType;
import org.oscm.vo.VOOrganization;
import org.oscm.vo.VOUserDetails;

/**
 * @author weiser
 * 
 */
public class AccountService_updateAccountInformation implements
        IOperationDescriptor<AccountService> {

    private static final String MODIFY_USER = "modifyUser? (true/false)";

    private static final String ORG_KEY = "orgKey";
    private static final String ORG_VERSION = "orgVersion";
    private static final String PHONE = "phone";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String NAME = "orgName";
    private static final String LOCALE = "locale";
    private static final String EMAIL = "email";
    private static final String ADDRESS = "address";

    private static final String USER_ID = "userId";
    private static final String SALUTATION = "salutation";
    private static final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";
    private static final String ADDITIONAL_NAME = "additionalName";
    private static final String USER_KEY = "userKey";
    private static final String USER_VERSION = "userVersion";

    @Override
    public void call(AccountService service, RequestLogEntry logEntry,
            Map<String, String> parameters) throws Exception {
        VOOrganization org = new VOOrganization();
        org.setKey(Long.parseLong(parameters.get(ORG_KEY)));
        org.setVersion(Integer.parseInt(parameters.get(ORG_VERSION)));
        org.setAddress(parameters.get(ADDRESS));
        org.setEmail(parameters.get(EMAIL));
        org.setLocale(parameters.get(LOCALE));
        org.setName(parameters.get(NAME));
        org.setOrganizationId(parameters.get(ORGANIZATION_ID));
        org.setPhone(parameters.get(PHONE));

        VOUserDetails user = new VOUserDetails();
        if (Boolean.parseBoolean(parameters.get(MODIFY_USER))) {
            user.setKey(Long.parseLong(parameters.get(USER_KEY)));
            user.setVersion(Integer.parseInt(parameters.get(USER_VERSION)));
            user.setAdditionalName(parameters.get(ADDITIONAL_NAME));
            user.setAddress(parameters.get(ADDRESS));
            user.setEMail(parameters.get(EMAIL));
            user.setFirstName(parameters.get(FIRST_NAME));
            user.setLastName(parameters.get(LAST_NAME));
            user.setLocale(parameters.get(LOCALE));
            user.addUserRole(UserRoleType.ORGANIZATION_ADMIN);
            user.setOrganizationId(parameters.get(ORGANIZATION_ID));
            user.setPhone(parameters.get(PHONE));
            user.setUserId(parameters.get(USER_ID));
            user.setSalutation(Salutation.valueOf(parameters.get(SALUTATION)));
        } else {
            user = null;
        }

        service.updateAccountInformation(org, user, null, null);
    }

    @Override
    public String getName() {
        return "AccountService.updateAccountInformation";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(ORG_KEY, ORG_VERSION, ORGANIZATION_ID, EMAIL,
                NAME, ADDRESS, PHONE, LOCALE, MODIFY_USER, USER_KEY,
                USER_VERSION, USER_ID, SALUTATION, FIRST_NAME, LAST_NAME,
                ADDITIONAL_NAME);
    }

    @Override
    public Class<AccountService> getServiceType() {
        return AccountService.class;
    }

    @Override
    public String getComment() {
        return null;
    }

}
