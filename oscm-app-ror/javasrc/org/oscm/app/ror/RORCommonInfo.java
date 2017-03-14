/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-11-08                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;

/**
 * ROR common information.
 */
public class RORCommonInfo {

    /**
     * Initializes and returns a VDC client.
     * 
     * @param ph
     *            an FGCP parameter handler
     * @return the VDC client
     * @throws Exception
     */
    public RORClient getVdcClient(PropertyHandler ph) {
        RORClient vdcClient = new RORClient(ph.getURL(), ph.getTenantId(),
                ph.getUser(), ph.getPassword(), ph.getAPILocale());
        return vdcClient;
    }

    /**
     * Initializes and returns a VSYS client.
     * 
     * @param paramHandler
     *            an FGCP parameter handler
     * @return the VSYS client
     * @throws Exception
     */
    public LPlatformClient getLPlatformClient(PropertyHandler paramHandler)
            throws Exception {
        RORClient vdcClient = getVdcClient(paramHandler);
        LPlatformClient vsysClient = new LPlatformClient(vdcClient,
                paramHandler.getVsysId());
        return vsysClient;
    }

    /**
     * Initializes and returns a VServer client.
     * 
     * @param paramHandler
     *            an FGCP parameter handler
     * @return the VServer client
     * @throws Exception
     */
    public LServerClient getLServerClient(PropertyHandler paramHandler)
            throws Exception {
        LPlatformClient vsysClient = getLPlatformClient(paramHandler);
        LServerClient vserverClient = new LServerClient(vsysClient,
                paramHandler.getVserverId());
        return vserverClient;
    }

}
