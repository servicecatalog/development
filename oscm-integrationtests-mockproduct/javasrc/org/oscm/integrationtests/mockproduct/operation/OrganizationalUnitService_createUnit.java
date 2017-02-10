/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
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
import org.oscm.vo.VOOrganizationalUnit;

public class OrganizationalUnitService_createUnit implements
        IOperationDescriptor<OrganizationalUnitService> {

    private static final String PARAM_UNIT_NAME = "unitName";
    private static final String PARAM_DESC = "description";
    private static final String PARAM_REF_ID = "referenceId";

    @Override
    public String getName() {
        return "OrganizationalUnitService.createUnit";
    }

    @Override
    public Class<OrganizationalUnitService> getServiceType() {
        return OrganizationalUnitService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList(PARAM_UNIT_NAME, PARAM_DESC, PARAM_REF_ID);
    }

    @Override
    public String getComment() {
        return "Creates new unit based on given parameters.";
    }

    @Override
    public void call(OrganizationalUnitService service,
            RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {

        String unitName = parameters.get(PARAM_UNIT_NAME);
        unitName = unitName.isEmpty() ? null : unitName;
        String description = parameters.get(PARAM_DESC);
        String referenceId = parameters.get(PARAM_REF_ID);

        VOOrganizationalUnit unit = service.createUnit(unitName, description,
                referenceId);
        
        logEntry.setResult(unit);
    }
}
