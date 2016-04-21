/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2014 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 24.04.2014                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.business;

import org.oscm.app.vmware.remote.vmware.VMwareClient;

import com.vmware.vim25.GuestInfo;

/**
 * Custom generation of access information output.
 */
public class VMwareAccessInfo {

    private static final String PATTERN_IP = "${IP}";
    private static final String PATTERN_HOST = "${HOST}";
    private static final String PATTERN_CPU = "${CPU}";
    private static final String PATTERN_MEM = "${MEM}";
    private static final String PATTERN_DISKS = "${DISK}";
    private static final String PATTERN_RESPUSER = "${RESPUSER}";

    private VMPropertyHandler paramHandler;

    public VMwareAccessInfo(@SuppressWarnings("unused") VMwareClient vmw,
            VMPropertyHandler paramHandler) {
        this.paramHandler = paramHandler;
    }

    /**
     * Returns the generated access info for the given VM.
     */
    public String generateAccessInfo(GuestInfo guestInfo) throws Exception {
        String accessInfo = "";
        String myIP = guestInfo.getIpAddress();
        if (myIP==null){
        	myIP="Unkown";
        }
        String myHOST = guestInfo.getHostName();
        String hostName;
        if (myHOST!=null){
        	hostName= guestInfo.getHostName().split("\\.", 2)[0];
        }
        else{
        	hostName="Unkown hostname (probably missing vmware tools).\nInstance name "+paramHandler.getInstanceName()+".";
        	myHOST="Unkown(InstanceName "+paramHandler.getInstanceName()+")";
        }
        String accessInfoPattern = paramHandler.getAccessInfo();
        if (accessInfoPattern == null || accessInfoPattern.length() == 0) {
            accessInfo = hostName;
        } else {
            String myCPU = Integer.toString(paramHandler.getConfigCPUs());
            String myMEM = paramHandler
                    .formatMBasGB(paramHandler.getConfigMemoryMB());
            String myDISKS = paramHandler.getDataDisksAsString();
            String respuser = paramHandler
                    .getResponsibleUserAsString(paramHandler.getLocale());
            if (respuser == null) {
                respuser = "";
            }

            accessInfo = accessInfoPattern.replace(PATTERN_IP, myIP);
            accessInfo = accessInfo.replace(PATTERN_HOST, myHOST);
            accessInfo = accessInfo.replace(PATTERN_CPU, myCPU);
            accessInfo = accessInfo.replace(PATTERN_MEM, myMEM);
            accessInfo = accessInfo.replace(PATTERN_DISKS, myDISKS);
            accessInfo = accessInfo.replace(PATTERN_RESPUSER, respuser);
            accessInfo = accessInfo.replace("<br>", "<br>\r\n");
        }
        return accessInfo;
    }
}
