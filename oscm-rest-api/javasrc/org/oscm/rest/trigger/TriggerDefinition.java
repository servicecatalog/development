/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.BadRequestException;

import org.oscm.rest.common.RepresentationWithVersion;

/**
 * Representation class of trigger definitions.
 * 
 * @author miethaner
 */
public class TriggerDefinition extends RepresentationWithVersion {

    public enum Action {
        SUBSCRIBE_TO_SERVICE, UNSUBSCRIBE_FROM_SERVICE, MODIFY_SUBSCRIPTION
    };

    public static class Owner {
        private String org_id; // TODO consistent naming
        private String org_name;

        public String getOrg_id() {
            return org_id;
        }

        public void setOrg_id(String org_id) {
            this.org_id = org_id;
        }

        public String getOrg_name() {
            return org_name;
        }

        public void setOrg_name(String org_name) {
            this.org_name = org_name;
        }

        /**
         * Creates new owner
         */
        public Owner() {
        }

        /**
         * Creates new owner with all fields
         * 
         * @param org_id
         * @param org_name
         */
        public Owner(String org_id, String org_name) {
            this.org_id = org_id;
            this.org_name = org_name;
        }

    }

    private String id;
    private String name;
    private Action action;
    private boolean suspend;
    private String targetURL;
    private Owner owner;

    /**
     * Creates new trigger definition
     */
    public TriggerDefinition() {
    }

    /**
     * Creates new trigger definition with all fields
     * 
     * @param id
     *            the resource id
     * @param name
     *            the trigger name
     * @param action
     *            the trigger action
     * @param suspend
     *            true if is suspended
     * @param targetURL
     *            the target of the trigger
     * @param owner
     *            the trigger owner
     */
    public TriggerDefinition(String id, String name, Action action,
            boolean suspend, String targetURL, Owner owner) {
        super();
        this.id = id;
        this.name = name;
        this.action = action;
        this.suspend = suspend;
        this.targetURL = targetURL;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public void validateContent() throws BadRequestException {
        // TODO validate content
    }

    @Override
    public void update(int version) {
        setVersion(version);

        // nothing to update in version 1
    }

    @Override
    public void convert(int version) {
        setVersion(version);

        // nothing to convert in version 1
    }

}
