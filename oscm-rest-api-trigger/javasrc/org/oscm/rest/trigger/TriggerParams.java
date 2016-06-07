/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 24, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

/**
 * @author miethaner
 *
 */
public class TriggerParams {

    // param names
    public static final String PARAM_PROCESS_ID = "processid";
    public static final String PARAM_OWNER_ID = "owner_id";
    public static final String PARAM_AUTHOR_ID = "author_id";

    // trigger path names
    public static final String PATH_TRIGGER = "/trigger";
    public static final String PATH_DEFINITIONS = "/triggerdefinitions";
    public static final String PATH_PROCESSES = "/triggerprocesses";
    public static final String PATH_TRIGGER_APPROVE = "/approve";
    public static final String PATH_TRIGGER_REJECT = "/reject";
    public static final String PATH_TRIGGER_CANCEL = "/cancel";
    public static final String PATH_PARAMETERS = "/parameters";
    public static final String PATH_ACTIONS = "/triggeractions";
    // only used to prevent double path param {id}
    public static final String PATH_PROCESS_ID = "/{" + PARAM_PROCESS_ID + "}";

    public static final String ACTION_SUBSCRIBE = "SUBSCRIBE_TO_SERVICE";
    public static final String ACTION_UNSUBSCRIBE = "UNSUBSCRIBE_FROM_SERVICE";
    public static final String ACTION_MODIFY = "MODIFY_SUBSCRIPTION";

    private TriggerParams() {
    }
}
