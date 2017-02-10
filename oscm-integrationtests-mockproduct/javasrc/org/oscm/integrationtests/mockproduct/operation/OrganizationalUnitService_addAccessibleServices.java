/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.OrganizationalUnitService;

public class OrganizationalUnitService_addAccessibleServices implements
        IOperationDescriptor<OrganizationalUnitService> {

    private static final String PARAM_UNIT_ID = "unitId";
    private static final String PARAM_SERVICES = "services";


    @Override
    public String getName() {
        return "OrganizationalUnitService.addAccessibleServices";
    }

    @Override
    public Class<OrganizationalUnitService> getServiceType() {
        return OrganizationalUnitService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(PARAM_UNIT_ID, PARAM_SERVICES);
    }

    @Override
    public String getComment() {
        return "Sets the listed services as accessible for this organizational unit.";
    }

    @Override
    public void call(OrganizationalUnitService service,
            RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {

        String unitId = parameters.get(PARAM_UNIT_ID);
        unitId = unitId.isEmpty() ? null : unitId;

        final String[] ids = parameters.get(PARAM_SERVICES).split(",");
        service.addAccessibleServices(unitId, Arrays.asList(ids));

    }
}
