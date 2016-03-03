/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 2013-12-17                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.iaas;

public interface ProvisioningProcessor {

    public void process(String controllerId, String instanceId,
            PropertyHandler paramHandler) throws Exception;
}
