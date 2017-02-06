/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-24
 *
 *******************************************************************************/

package org.oscm.app.vmware.business.balancer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.business.model.VMwareStorage;
import org.oscm.app.vmware.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage balancer implementation dynamically selecting the least used storage
 * from vSphere.
 * 
 * @author Oliver Petrovski
 * 
 */
public class DynamicEquipartitionStorageBalancer extends StorageBalancer {

    private static final Logger logger = LoggerFactory
            .getLogger(DynamicEquipartitionStorageBalancer.class);
    private static final String ELEMENT_BLACKLIST_STORAGE = "blackliststorage";
    private static final String ELEMENT_BALANCER = "balancer";
    List<String> blacklistStorages;
    @Override
    public VMwareStorage next(VMPropertyHandler properties)
            throws APPlatformException {
        VMwareStorage selectedStorage = null;
        XMLConfiguration xmlConfig = new XMLHostConfiguration();
        try {
            xmlConfig.load(new StringReader(properties
                    .getHostLoadBalancerConfig()));
        } catch (ConfigurationException e) {
            throw new APPlatformException(e.getMessage());
        }

        List<Object> storages = xmlConfig.configurationAt(ELEMENT_BALANCER)
                .getList(ELEMENT_BLACKLIST_STORAGE);
        blacklistStorages = new ArrayList<String>(storages.size());
        for (Object blstorage : storages) {
            blacklistStorages.add(blstorage.toString().toLowerCase());
        }
        String targetHost = properties
                .getServiceSetting(VMPropertyHandler.TS_TARGET_HOST);

        if (targetHost == null) {
            throw new APPlatformException(
                    Messages.getAll("error_target_host_not_defined"));
        }

        double maxFreeSpace = 0.0;
        for (VMwareStorage storage : inventory.getStorageByHost(targetHost)) {
            if (blacklistStorages.contains(storage.getName().toLowerCase())) {
                logger.debug("Blacklisted Storage: " + storage.getName());
                continue;
            }

            if (storage.getFree() > maxFreeSpace) {
                logger.debug("New Selected Storage: " + storage.getName()
                        + ". Free Space is " + storage.getFree() + "MB.");
                selectedStorage = storage;
                maxFreeSpace = storage.getFree();
            }
        }

        if (selectedStorage != null)

        {
            return selectedStorage;
        }

        throw new APPlatformException(Messages.getAll("error_outof_storage"));
    }
}
