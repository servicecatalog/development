/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-11-09                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.data;

public interface LServerStatus {

    public static final String DEPLOYING = "DEPLOYING";
    public static final String RUNNING = "RUNNING";
    public static final String STOPPING = "STOPPING";
    public static final String STOPPED = "STOPPED";
    public static final String STARTING = "STARTING";
    public static final String FAILOVER = "FAILOVER";
    public static final String UNEXPECTED_STOP = "UNEXPECTED_STOP";
    public static final String RESTORING = "RESTORING";
    public static final String BACKUP_ING = "BACKUP_ING";
    public static final String ERROR = "ERROR";
    public static final String START_ERROR = "START_ERROR";
    public static final String STOP_ERROR = "STOP_ERROR";
    public static final String CHANGE_TYPE = "CHANGE_TYPE";
    public static final String REGISTERING = "REGISTERING";
    public static final String EXECUTE_IPCOM = "EXECUTE_IPCOM";
}
