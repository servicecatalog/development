/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOTriggerDefinition;
import org.oscm.rest.common.Representation;

/**
 * Representation class of trigger definitions.
 * 
 * @author miethaner
 */
public class TriggerDefinition extends Representation {

    public static class Owner {
        private UUID id;
        private String description;

        public Owner() {
        }

        public Owner(UUID id, String description) {
            this.id = id;
            this.description = description;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Links {
        private String owner_id;
        private String action_id;

        public Links() {
        }

        public Links(String owner_id, String action_id) {
            this.owner_id = owner_id;
            this.action_id = action_id;
        }

        public String getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(String owner_id) {
            this.owner_id = owner_id;
        }

        public String getAction_id() {
            return action_id;
        }

        public void setAction_id(String action_id) {
            this.action_id = action_id;
        }
    }

    private String description;
    private Boolean suspend;
    private String target_url;
    private Links links;
    private Owner owner;

    public TriggerDefinition() {
    }

    public TriggerDefinition(UUID id, String description, Boolean suspend,
            String targetURL, Links links, Owner owner) {
        super(id);
        this.description = description;
        this.suspend = suspend;
        this.target_url = targetURL;
        this.links = links;
        this.owner = owner;
    }

    public TriggerDefinition(VOTriggerDefinition defintion) {
        this.setId(null);
        this.description = defintion.getName();
        this.suspend = new Boolean(defintion.isSuspendProcess());
        this.target_url = defintion.getTarget();
        this.owner = null;
        this.links = new Links(null, defintion.getType().name());

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(Boolean suspend) {
        this.suspend = suspend;
    }

    public String getTarget_url() {
        return target_url;
    }

    public void setTarget_url(String target_url) {
        this.target_url = target_url;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public void validateContent() throws WebApplicationException {
        // TODO validate content
    }

    @Override
    public void update() {

        // nothing to update
    }

    @Override
    public void convert() {

        // nothing to convert
    }

}
