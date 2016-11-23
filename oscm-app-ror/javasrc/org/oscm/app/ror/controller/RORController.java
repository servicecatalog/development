/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2012-11-27                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.ror.controller;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.controller.IaasController;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.intf.APPlatformController;

/**
 * ROR Controller
 */
@Stateless(mappedName = "bss/app/controller/" + RORController.ID)
@Remote(APPlatformController.class)
public class RORController extends IaasController {
    public static final String ID = "ess.ror";

    @Override
    protected String getControllerID() {
        return ID;
    }

    @Override
    public void validateDiskName(PropertyHandler paramHandler)
            throws APPlatformException {
    }
}
