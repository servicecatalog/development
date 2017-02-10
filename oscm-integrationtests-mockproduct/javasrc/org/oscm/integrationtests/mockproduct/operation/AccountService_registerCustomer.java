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
public class AccountService_registerCustomer implements
        IOperationDescriptor<AccountService> {

    private static final String SUPPLIER_ID = "supplierId";
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
    private static final String PASSWORD = "password";

    private static final String SERVICE_ID = "serviceId";

    @Override
    public void call(AccountService service, RequestLogEntry logEntry,
            Map<String, String> parameters) throws Exception {
        VOOrganization org = new VOOrganization();
        org.setAddress(parameters.get(ADDRESS));
        org.setEmail(parameters.get(EMAIL));
        org.setLocale(parameters.get(LOCALE));
        org.setName(parameters.get(NAME));
        org.setOrganizationId(parameters.get(ORGANIZATION_ID));
        org.setPhone(parameters.get(PHONE));

        VOUserDetails admin = new VOUserDetails();
        admin.setAdditionalName(parameters.get(ADDITIONAL_NAME));
        admin.setAddress(parameters.get(ADDRESS));
        admin.setEMail(parameters.get(EMAIL));
        admin.setFirstName(parameters.get(FIRST_NAME));
        admin.setLastName(parameters.get(LAST_NAME));
        admin.setLocale(parameters.get(LOCALE));
        admin.addUserRole(UserRoleType.ORGANIZATION_ADMIN);
        admin.setOrganizationId(parameters.get(ORGANIZATION_ID));
        admin.setPhone(parameters.get(PHONE));
        admin.setUserId(parameters.get(USER_ID));
        admin.setSalutation(Salutation.valueOf(parameters.get(SALUTATION)));

        String password = parameters.get(PASSWORD);

        Long serviceKey = null;
        String string = parameters.get(SERVICE_ID);
        if (string != null && string.trim().length() > 0) {
            serviceKey = Long.valueOf(string);
        }

        VOOrganization customer = service.registerCustomer(org, admin,
                password, serviceKey, null, parameters.get(SUPPLIER_ID));

        logEntry.setResult(customer);
    }

    @Override
    public String getName() {
        return "AccountService.registerCustomer";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(SUPPLIER_ID, ORGANIZATION_ID, EMAIL, NAME,
                ADDRESS, PHONE, LOCALE, USER_ID, SALUTATION, FIRST_NAME,
                LAST_NAME, ADDITIONAL_NAME, PASSWORD, SERVICE_ID);
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
