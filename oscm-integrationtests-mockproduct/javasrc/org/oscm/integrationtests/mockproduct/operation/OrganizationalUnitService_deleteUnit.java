/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *  Creation Date: 21.08.15 14:55
 *
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.OrganizationalUnitService;

public class OrganizationalUnitService_deleteUnit implements
        IOperationDescriptor<OrganizationalUnitService> {
    
    private static final String PARAM_UNIT_NAME = "organizationalUnitName";
    
    @Override
    public String getName() {
        return "OrganizationalUnitService.deleteUnit";
    }
    
    @Override
    public Class<OrganizationalUnitService> getServiceType() {
        return OrganizationalUnitService.class;
    }

    @Override
    public List<String> getParameters() {
        return Collections.singletonList(PARAM_UNIT_NAME);
    }

    @Override
    public String getComment() {
        return "Deletes unit based on unit name.";
    }

    @Override
    public void call(OrganizationalUnitService service,
            RequestLogEntry logEntry, Map<String, String> parameters)
            throws Exception {

        String unitName = parameters.get(PARAM_UNIT_NAME);
        service.deleteUnit(unitName);
    }
}
