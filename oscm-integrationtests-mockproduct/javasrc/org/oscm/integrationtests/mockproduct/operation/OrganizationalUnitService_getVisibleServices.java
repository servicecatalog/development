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
import org.oscm.pagination.Pagination;
import org.oscm.vo.VOOrganizationalUnit;
import org.oscm.vo.VOService;

public class OrganizationalUnitService_getVisibleServices implements
        IOperationDescriptor<OrganizationalUnitService> {

    private static final String PARAM_UNIT_ID = "unitId";
    private static final String PARAM_MARKETPLACE_ID = "marketplaceId";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_PAGESIZE = "pageSize";

    @Override
    public String getName() {
        return "OrganizationalUnitService.getVisibleServices";
    }

    @Override
    public Class<OrganizationalUnitService> getServiceType() {
        return OrganizationalUnitService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(PARAM_UNIT_ID, PARAM_OFFSET, PARAM_PAGESIZE, PARAM_MARKETPLACE_ID);
    }

    @Override
    public String getComment() {
        return "Returns services visible for organizational unit administrator and organization administrator.";
    }

    @Override
    public void call(OrganizationalUnitService service,
            RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {
        Pagination pagination;

        try {
            int offset = Integer.parseInt(parameters.get(PARAM_OFFSET));
            int pageSize = Integer.parseInt(parameters.get(PARAM_PAGESIZE));

            pagination = new Pagination(offset, pageSize);
        } catch (NumberFormatException e) {
            pagination = new Pagination(0,0);
        }

        String unitId = parameters.get(PARAM_UNIT_ID);
        unitId = unitId.isEmpty() ? null : unitId;
        String marketplaceId = parameters.get(PARAM_MARKETPLACE_ID);

        List<VOService> visibleServices = service.getVisibleServices(unitId, pagination, marketplaceId);
        
        logEntry.setResult(visibleServices);
    }
}
