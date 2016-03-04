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

public class OrganizationalUnitService_revokeAccessibleServices implements
        IOperationDescriptor<OrganizationalUnitService> {

    private static final String PARAM_UNIT_ID = "unitId";
    private static final String PARAM_MARKETPLACE_ID = "marketplaceId";
    private static final String PARAM_SERVICES = "servicesId";


    @Override
    public String getName() {
        return "OrganizationalUnitService.revokeAccessibleServices";
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
        return "Sets the listed services as not accessible for this organizational unit.";
    }

    @Override
    public void call(OrganizationalUnitService service,
                     RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {

        List<VOService> visibleServices = null;

        String unitId = parameters.get(PARAM_UNIT_ID);
        unitId = unitId.isEmpty() ? null : unitId;
        String marketplaceId = parameters.get(PARAM_MARKETPLACE_ID);

        service.revokeAccessibleServices(unitId, visibleServices, marketplaceId);

        logEntry.setResult(visibleServices);
    }
}
