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
import org.oscm.vo.VOBillingContact;

/**
 * @author weiser
 * 
 */
public class AccountService_saveBillingContact implements
        IOperationDescriptor<AccountService> {

    private static final String ORG_ADDRESS_USED = "orgAddressUsed";
    private static final String KEY = "key";
    private static final String VERSION = "version";
    private static final String NAME = "orgName";
    private static final String EMAIL = "email";
    private static final String ADDRESS = "address";
    private static final String ID = "id";

    @Override
    public void call(AccountService service, RequestLogEntry logEntry,
            Map<String, String> parameters) throws Exception {
        VOBillingContact bc = new VOBillingContact();
        bc.setAddress(parameters.get(ADDRESS));
        bc.setEmail(parameters.get(EMAIL));
        bc.setCompanyName(parameters.get(NAME));
        bc.setKey(Long.parseLong(parameters.get(KEY)));
        bc.setVersion(Integer.parseInt(parameters.get(VERSION)));
        bc.setOrgAddressUsed(Boolean.parseBoolean(parameters
                .get(ORG_ADDRESS_USED)));
        bc.setId(parameters.get(ID));
        service.saveBillingContact(bc);
    }

    @Override
    public String getName() {
        return "AccountService.saveBillingContact";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(KEY, VERSION, ID, EMAIL, NAME, ADDRESS,
                ORG_ADDRESS_USED);
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
