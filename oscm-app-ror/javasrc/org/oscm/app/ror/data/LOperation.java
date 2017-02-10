/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.app.ror.data;

public interface LOperation {
    public static final String LIST_SERVER_TYPE = "ListServerType";
    public static final String CREATE_LPLATFORM = "CreateLPlatform";
    public static final String LIST_LPLATFORM = "ListLPlatform";
    public static final String LIST_LPLATFORM_DESCR = "ListLPlatformDescriptor";
    public static final String LIST_DISKIMAGE = "ListDiskImage";
    public static final String GET_LPLATFORM_DESCR_CONFIG = "GetLPlatformDescriptorConfiguration";
    public static final String CREATE_LSERVER = "CreateLServer";
    public static final String GET_LPLATFORM_STATUS = "GetLPlatformStatus";
    public static final String GET_LPLATFORM_CONFIG = "GetLPlatformConfiguration";
    public static final String DESTROY_LPLATFORM = "DestroyLPlatform";
    public static final String STOP_LPLATFORM = "StopLPlatform";
    public static final String START_LPLATFORM = "StartLPlatform";
    public static final String DESTROY_LSERVER = "DestroyLServer";
    public static final String GET_LSERVER_INIT_PASSWD = "GetLServerInitialPassword";
    public static final String GET_LSERVER_STATUS = "GetLServerStatus";
    public static final String GET_LSERVER_CONFIG = "GetLServerConfiguration";
    public static final String UPDATE_LSERVER_CONFIG = "UpdateLServerConfiguration";
    public static final String CREATE_IMAGE = "CreateImage";
    public static final String STOP_LSERVER = "StopLServer";
    public static final String START_LSERVER = "StartLServer";
}
