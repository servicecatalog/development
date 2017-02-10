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
import org.oscm.pagination.Pagination;
import org.oscm.vo.VOOrganizationalUnit;

public class OrganizationalUnitService_getOrganizationalUnits implements IOperationDescriptor<OrganizationalUnitService>  {
    
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_PAGESIZE = "pageSize";
    
    @Override
    public String getName() {
        return "OrganizationalUnitService.getOrganizationalUnits";
    }
    
    @Override
    public Class<OrganizationalUnitService> getServiceType() {
        return OrganizationalUnitService.class;
    }
    
    @Override
    public List<String> getParameters() {
        return Arrays.asList(PARAM_OFFSET, PARAM_PAGESIZE);
    }
    
    @Override
    public String getComment() {
        return "Returns organizational units in given range defined by: " 
                + "Offset - starting element number. " 
                + "PageSize - number of elements to return. " 
                + "If one of those values is empty then all units are returned.";
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
        
        List<VOOrganizationalUnit> units = service.getOrganizationalUnits(
                pagination);
        
        logEntry.setResult(units);
    }
}
