/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 24.07.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.business.model;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwareVirtualMachine {

    private String name;
    private String hostName;
    private int numCpu;
    private int memorySizeMB;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getNumCpu() {
        return numCpu;
    }

    public void setNumCpu(int numCpu) {
        this.numCpu = numCpu;
    }

    public int getMemorySizeMB() {
        return memorySizeMB;
    }

    public void setMemorySizeMB(int memorySizeMB) {
        this.memorySizeMB = memorySizeMB;
    }
}
