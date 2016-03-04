/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015                                           
 *                                                                                                                                  
 *  Creation Date: 21.07.15 11:45
 *
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.OrganizationalUnitService;
import org.oscm.pagination.Pagination;
import org.oscm.vo.VOService;

public class OrganizationalUnitService_addAccessibleServices implements
        IOperationDescriptor<OrganizationalUnitService> {

    private static final String PARAM_UNIT_ID = "unitId";
    private static final String PARAM_MARKETPLACE_ID = "marketplaceId";
    private static final String PARAM_SERVICES = "servicesId";


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
        return Arrays.asList(PARAM_UNIT_ID, PARAM_SERVICES, PARAM_MARKETPLACE_ID);
    }

    @Override
    public String getComment() {
        return "Sets the listed services as accessible for this organizational unit.";
    }

    @Override
    public void call(OrganizationalUnitService service,
            RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {

        List<VOService> accessibleServices = null;

        String unitId = parameters.get(PARAM_UNIT_ID);
        unitId = unitId.isEmpty() ? null : unitId;
        String marketplaceId = parameters.get(PARAM_MARKETPLACE_ID);

        service.addAccessibleServices(unitId, accessibleServices, marketplaceId);

        logEntry.setResult(accessibleServices);
    }
}
