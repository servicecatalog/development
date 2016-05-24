/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2016-05-24                                                       
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.business;

import java.util.List;

import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.vmware.i18n.Messages;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDeviceFileBackingInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;

public class DiskManager {

    private static final Logger logger = LoggerFactory
            .getLogger(DiskManager.class);

    /**
     * Reconfigures VMware disk instances (system and data disk).
     * 
     * @param vmw
     *            connected VMware client entity
     * @param paramHandler
     *            entity which holds all properties of the instance.
     */
    public static void reconfigureDisks(VMwareClient vmw,
            VirtualMachineConfigSpec vmConfigSpec,
            VMPropertyHandler paramHandler, ManagedObjectReference vmwInstance)
            throws Exception {
        logger.debug("");
        long systemDiskMB = (long) paramHandler.getConfigDiskSpaceMB();
        Double[] dataDisksMB = paramHandler.getDataDisksMB();

        VirtualMachineConfigInfo configSpec = (VirtualMachineConfigInfo) vmw
                .getServiceUtil().getDynamicProperty(vmwInstance, "config");
        List<VirtualDevice> devices = configSpec.getHardware().getDevice();
        VirtualDisk vdSystemDisk = getVMSystemDisk(devices,
                configSpec.getName());
        if (systemDiskMB > 0) {
            long newDiskSpace = systemDiskMB * 1024;

            if (newDiskSpace < vdSystemDisk.getCapacityInKB()) {
                logger.error("Cannot reduce size of system disk \""
                        + vdSystemDisk.getDeviceInfo().getLabel() + "\"");
                logger.error("Current disk size: "
                        + vdSystemDisk.getCapacityInKB() + " new disk space: "
                        + newDiskSpace);

                throw new Exception(Messages
                        .getAll("error_invalid_diskspacereduction").get(0)
                        .getText());
            } else if (newDiskSpace > vdSystemDisk.getCapacityInKB()) {
                logger.info("reconfigureDisks() extend system disk space to "
                        + newDiskSpace + " ("
                        + vdSystemDisk.getDeviceInfo().getLabel() + ")");
                vdSystemDisk.setCapacityInKB(newDiskSpace);
                VirtualDeviceConfigSpec vmDeviceSpec = new VirtualDeviceConfigSpec();
                vmDeviceSpec
                        .setOperation(VirtualDeviceConfigSpecOperation.EDIT);
                vmDeviceSpec.setDevice(vdSystemDisk);
                vmConfigSpec.getDeviceChange().add(vmDeviceSpec);
            } else {
                logger.debug("System disk size has not been changed. "
                        + newDiskSpace + " KB");
            }
        } else {
            logger.error("Reconfiguration of system disk not possible because system disk size is not defined.");
        }

        if (dataDisksMB.length > 0) {
            int maxDeviceKey = 0;
            int maxUnitNumber = 0;
            for (VirtualDevice vdDev : devices) {
                if (vdDev instanceof VirtualDisk) {
                    if (vdDev.getKey() > maxDeviceKey) {
                        maxDeviceKey = vdDev.getKey();
                    }
                    if (vdDev.getUnitNumber().intValue() > maxUnitNumber) {
                        maxUnitNumber = vdDev.getUnitNumber().intValue();
                    }
                }
            }

            int vdIndex = 0;
            for (Double dataDiskMB : dataDisksMB) {
                vdIndex++;

                int vdKey = paramHandler.getDataDiskKey(vdIndex);
                long newDiskSpace = dataDiskMB.longValue() * 1024;
                if (vdKey == 0) {
                    logger.info("reconfigureDisks() create data disk space with "
                            + newDiskSpace + " KB");

                    ManagedObjectReference vmDatastore = ((VirtualDeviceFileBackingInfo) vdSystemDisk
                            .getBacking()).getDatastore();
                    String vmDatastoreName = (String) vmw.getServiceUtil()
                            .getDynamicProperty(vmDatastore, "summary.name");

                    VirtualDisk vdDataDisk = new VirtualDisk();
                    VirtualDiskFlatVer2BackingInfo diskfileBacking = new VirtualDiskFlatVer2BackingInfo();
                    diskfileBacking.setFileName("[" + vmDatastoreName + "]");
                    diskfileBacking.setDiskMode("persistent");

                    vdDataDisk.setKey(++maxDeviceKey);
                    vdDataDisk
                            .setControllerKey(vdSystemDisk.getControllerKey());
                    vdDataDisk.setUnitNumber(new Integer(++maxUnitNumber));
                    vdDataDisk.setBacking(diskfileBacking);
                    vdDataDisk.setCapacityInKB(newDiskSpace);

                    VirtualDeviceConfigSpec vmDeviceSpec = new VirtualDeviceConfigSpec();
                    vmDeviceSpec
                            .setOperation(VirtualDeviceConfigSpecOperation.ADD);
                    vmDeviceSpec
                            .setFileOperation(VirtualDeviceConfigSpecFileOperation.CREATE);
                    vmDeviceSpec.setDevice(vdDataDisk);
                    vmConfigSpec.getDeviceChange().add(vmDeviceSpec);

                    paramHandler.setDataDiskKey(vdIndex, maxDeviceKey);

                } else {
                    VirtualDisk vdDataDisk = getVMDataDisk(devices, vdKey);

                    if (vdDataDisk != null
                            && newDiskSpace > vdDataDisk.getCapacityInKB()) {

                        logger.info("reconfigureDisks() extend data disk #"
                                + vdKey + " space to " + newDiskSpace + " ("
                                + vdDataDisk.getDeviceInfo().getLabel() + ")");

                        if (newDiskSpace < vdDataDisk.getCapacityInKB()) {
                            logger.error("Cannot reduce size of data disk "
                                    + vdDataDisk.getDeviceInfo().getLabel());
                            logger.error("Current disk space: "
                                    + vdDataDisk.getCapacityInKB()
                                    + " new disk space: " + newDiskSpace);

                            throw new Exception(Messages
                                    .getAll("error_invalid_diskspacereduction")
                                    .get(0).getText());
                        } else if (newDiskSpace > vdDataDisk.getCapacityInKB()) {
                            vdDataDisk.setCapacityInKB(newDiskSpace);
                            VirtualDeviceConfigSpec vmDeviceSpec = new VirtualDeviceConfigSpec();
                            vmDeviceSpec
                                    .setOperation(VirtualDeviceConfigSpecOperation.EDIT);
                            vmDeviceSpec.setDevice(vdDataDisk);
                            vmConfigSpec.getDeviceChange().add(vmDeviceSpec);
                        } else {
                            logger.debug("Data disk size has not been changed. "
                                    + newDiskSpace + " KB");
                        }
                    }

                }
            }
        } else {
            logger.debug("Reconfiguration of data disk not possible because data disk size is not defined.");
        }

    }

    private static VirtualDisk getVMSystemDisk(List<VirtualDevice> devices,
            String vmName) throws APPlatformException {
        VirtualDisk vdInfoFirst = null;
        for (VirtualDevice vdInfo : devices) {
            if (vdInfo instanceof VirtualDisk) {
                vdInfoFirst = (VirtualDisk) vdInfo;
                logger.debug("Found system disk in VM " + vmName + "  label: "
                        + vdInfoFirst.getDeviceInfo().getLabel() + " type: "
                        + vdInfoFirst.getDeviceInfo().getDynamicType()
                        + " summary: "
                        + vdInfoFirst.getDeviceInfo().getSummary());
                break;
            }
        }

        if (vdInfoFirst == null) {
            logger.error("No virtual system disk in VM " + vmName + " found.");
            throw new APPlatformException(Messages.getAll(
                    "error_missing_systemdisk", new Object[] { vmName }));
        }

        return vdInfoFirst;

    }

    public static long getSystemDiskCapacity(List<VirtualDevice> devices,
            String vmName) throws APPlatformException {
        VirtualDisk vdInfoFirst = getVMSystemDisk(devices, vmName);
        return vdInfoFirst.getCapacityInKB();
    }

    /**
     * Search and return virtual disk with given key
     */
    private static VirtualDisk getVMDataDisk(List<VirtualDevice> devices,
            int vdKey) {
        VirtualDisk vdDataDisk = null;
        for (VirtualDevice vdInfo : devices) {
            if (vdInfo instanceof VirtualDisk) {
                if (vdInfo.getKey() == vdKey) {
                    vdDataDisk = (VirtualDisk) vdInfo;
                    break;
                }
            }
        }

        if (vdDataDisk == null) {
            logger.warn("  no virtual disk with key #" + vdKey + " found");
        }

        return vdDataDisk;
    }

}
