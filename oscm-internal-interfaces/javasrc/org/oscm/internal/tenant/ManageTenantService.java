package org.oscm.internal.tenant;

import javax.ejb.Remote;
import java.util.List;

/**
 * Created by BadziakP on 2016-08-30.
 */
@Remote
public interface ManageTenantService {
    public List<POTenant> getAllTenants();
}
