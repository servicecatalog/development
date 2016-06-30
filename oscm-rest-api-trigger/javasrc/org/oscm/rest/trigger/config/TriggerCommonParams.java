/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 24, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.config;

/**
 * @author miethaner
 *
 */
public class TriggerCommonParams {

    // param names
    public static final String PARAM_PROCESS_ID = "processid";
    public static final String PARAM_OWNER_ID = "owner_id";
    public static final String PARAM_AUTHOR_ID = "author_id";

    // trigger path names
    public static final String PATH_TRIGGER = "/trigger";
    public static final String PATH_DEFINITIONS = "/triggers";
    public static final String PATH_PROCESSES = "/processes";
    public static final String PATH_TRIGGER_APPROVE = "/approve";
    public static final String PATH_TRIGGER_REJECT = "/reject";
    public static final String PATH_TRIGGER_CANCEL = "/cancel";
    public static final String PATH_ACTIONS = "/actions";

    // properties
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_TARGET_URL = "target_url";
    public static final String PROPERTY_ACTION = "action";
    public static final String PROPERTY_TARGET_TYPE = "target_type";

    private TriggerCommonParams() {
    }
}
