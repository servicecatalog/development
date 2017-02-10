/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 2014-01-28                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.data;

import org.apache.commons.configuration.HierarchicalConfiguration;

import org.oscm.app.iaas.data.DiskImage;

/**
 * ROR-specific implementation of the disk image data object.
 */
public class RORDiskImage implements DiskImage {

    private HierarchicalConfiguration configuration;

    public RORDiskImage(HierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getDiskImageId() {
        return configuration.getString("diskimageId");
    }

    @Override
    public String getDiskImageName() {
        return configuration.getString("diskimageName");
    }

    @Override
    public String getMaxCpuCount() {
        return configuration.getString("numOfMaxCpu");
    }

    @Override
    public String getMaxCpuPerf() {
        return configuration.getString("maxCpuPerf");
    }

    @Override
    public String getMaxMemorySize() {
        return configuration.getString("maxMemorySize");
    }
}
