/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2016-05-24                                                       
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.remote.vmware;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VMClientFactory extends
        BaseKeyedPooledObjectFactory<String, VMwareClient> {

    private static final Logger logger = LoggerFactory
            .getLogger(VMClientFactory.class);

    @Override
    public VMwareClient create(String vcenter) throws Exception {
        logger.debug("vcenter: " + vcenter);
        VMwareClientFactory vmwFactory = new VMwareClientFactory("en");
        VMwareClient vmClient = vmwFactory.getInstance(vcenter);
        vmClient.connect();
        return vmClient;
    }

    @Override
    public PooledObject<VMwareClient> wrap(VMwareClient client) {
        return new DefaultPooledObject<VMwareClient>(client);
    }

    @Override
    public boolean validateObject(String vcenter, PooledObject<VMwareClient> p) {
        logger.debug("vcenter: " + vcenter);
        return p.getObject().isConnected();
    }

    @Override
    public void destroyObject(String vcenter, PooledObject<VMwareClient> p)
            throws Exception {
        logger.debug("vcenter: " + vcenter);
        p.getObject().close();
    }

}