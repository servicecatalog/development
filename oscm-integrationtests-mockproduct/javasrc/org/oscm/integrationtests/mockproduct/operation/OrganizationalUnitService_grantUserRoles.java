/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *  Creation Date: 21.07.15 11:45
 *
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.OrganizationalUnitService;
import org.oscm.types.enumtypes.UnitRoleType;
import org.oscm.vo.VOOrganizationalUnit;
import org.oscm.vo.VOUser;

public class OrganizationalUnitService_grantUserRoles implements
        IOperationDescriptor<OrganizationalUnitService> {

    public static final String PARAM_USER_KEY = "userKey";
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_ROLE_TYPE = "roleType";
    public static final String PARAM_ORG_UNIT_KEY = "organizationalUnitKey";

    @Override
    public String getName() {
        return "OrganizationalUnitService.grantUserRoles";
    }

    @Override
    public Class<OrganizationalUnitService> getServiceType() {
        return OrganizationalUnitService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(PARAM_USER_KEY, PARAM_USER_ID, PARAM_ROLE_TYPE,
                PARAM_ORG_UNIT_KEY);
    }

    @Override
    public String getComment() {
        return "Grants user given role type. If user already has a role nothing will happen. Provide either userKey or userId. If no userKey is given or with value '0' then userId will be used to get user.";
    }

    @Override
    public void call(OrganizationalUnitService service,
            RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {

        String userKeyStr = parameters.get(PARAM_USER_KEY);
        long userKey = Long.parseLong(userKeyStr.isEmpty() ? "0" : userKeyStr);
        String userId = parameters.get(PARAM_USER_ID);
        UnitRoleType roleType = UnitRoleType.valueOf(parameters
                .get(PARAM_ROLE_TYPE));
        String orgUnitStr = parameters.get(PARAM_ORG_UNIT_KEY);
        long orgUnitKey = Long.parseLong(orgUnitStr.isEmpty() ? "0" : orgUnitStr);

        service.grantUserRoles(createUser(userKey, userId),
                Collections.singletonList(roleType), createUnit(orgUnitKey));

    }

    private VOUser createUser(long userKey, String userId) {
        VOUser user = new VOUser();

        user.setKey(userKey);
        user.setUserId(userId);

        return user;
    }

    private VOOrganizationalUnit createUnit(long unitKey) {
        VOOrganizationalUnit unit = new VOOrganizationalUnit();

        unit.setKey(unitKey);

        return unit;
    }
}
