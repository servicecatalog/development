/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-11-12                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.intf.VDiskCommunication;

@Stateless
@Local(VDiskCommunication.class)
public class RORVDiskCommunication extends RORCommonInfo implements
        VDiskCommunication {

    @Override
    public String createVDisk(PropertyHandler paramHandler) throws Exception {
        return null;
    }

    @Override
    public boolean isVDiskDeployed(PropertyHandler paramHandler)
            throws Exception {
        return false;
    }

    @Override
    public void attachVDisk(PropertyHandler paramHandler) throws Exception {
    }

    @Override
    public boolean isVDiskAttached(PropertyHandler paramHandler)
            throws Exception {
        return false;
    }

    @Override
    public boolean isAdditionalDiskSelected(PropertyHandler paramHandler)
            throws Exception {
        return false;
    }

    @Override
    public boolean areVDisksDetached(PropertyHandler paramHandler)
            throws Exception {
        return true;
    }

    @Override
    public boolean isAttachedVDisksFound(PropertyHandler paramHandler)
            throws Exception {
        return false;
    }

    @Override
    public void detachVDisks(PropertyHandler paramHandler) throws Exception {
    }

    @Override
    public boolean areVDisksDestroyed(PropertyHandler paramHandler)
            throws Exception {
        return true;
    }

    @Override
    public void destroyVDisks(PropertyHandler paramHandler) throws Exception {
    }
}
