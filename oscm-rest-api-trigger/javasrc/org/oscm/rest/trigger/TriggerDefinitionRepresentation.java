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

import org.oscm.rest.common.Representation;
import org.oscm.rest.trigger.TriggerActionRepresentation.Action;
import org.oscm.rest.trigger.interfaces.OrganizationRest;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRest;

/**
 * Representation class of trigger definitions.
 * 
 * @author miethaner
 */
public class TriggerDefinitionRepresentation extends Representation implements
        TriggerDefinitionRest {

    public static class Owner implements OrganizationRest {
        private String id;
        private String description;

        public Owner() {
        }

        public Owner(String id, String description) {
            this.id = id;
            this.description = description;
        }

        public Owner(OrganizationRest owner) {
            this.id = owner.getResourceId();
            this.description = owner.getName();
        }

        @Override
        public String getResourceId() {
            return id;
        }

        public void setResourceId(String id) {
            this.id = id;
        }

        @Override
        public String getName() {
            return description;
        }

        public void setName(String description) {
            this.description = description;
        }
    }

    public static class Links {
        private String owner_id;

        public Links() {
        }

        public Links(String owner_id) {
            this.owner_id = owner_id;
        }

        public String getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(String owner_id) {
            this.owner_id = owner_id;
        }
    }

    private String description;
    private Boolean suspend;
    private String target_url;
    private Owner owner;
    private Action action;
    private Links links;

    public TriggerDefinitionRepresentation() {
    }

    public TriggerDefinitionRepresentation(UUID id, String description,
            Boolean suspend, String targetURL, Owner owner, Links links) {
        super(id);
        this.description = description;
        this.suspend = suspend;
        this.target_url = targetURL;
        this.owner = owner;
        this.links = links;
    }

    public TriggerDefinitionRepresentation(TriggerDefinitionRest definition) {
        this.setId(UUID.fromString(definition.getResourceId()));
        this.description = definition.getDescription();
        this.suspend = definition.isSuspending();
        this.target_url = definition.getTargetURL();
        this.owner = new Owner(definition.getOwner().getResourceId(),
                definition.getOwner().getName());
        this.links = new Links(definition.getOwnerId());
    }

    @Override
    public String getResourceId() {
        return getId().toString();
    }

    public void setResourceId(String id) {
        setId(UUID.fromString(id));
    }

    @Override
    public String getOwnerId() {
        if (links != null) {
            return links.getOwner_id();
        } else {
            return null;
        }
    }

    public void setOwnerId(String owner_id) {
        if (links == null) {
            links = new Links();
        }

        links.setOwner_id(owner_id);
    }

    @Override
    public OrganizationRest getOwner() {
        return owner;
    }

    public void setOwner(OrganizationRest owner) {
        this.owner = new Owner(owner);
    }

    @Override
    public String getAction() {
        return action.toString();
    }

    public void setAction(String action) {
        if (action == null) {
            this.action = null;
        } else {
            this.action = Action.valueOf(action);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTargetURL() {
        return target_url;
    }

    public void setTargetURL(String target_url) {
        this.target_url = target_url;
    }

    @Override
    public Boolean isSuspending() {
        return suspend;
    }

    public void setSuspending(Boolean suspend) {
        this.suspend = suspend;
    }

    @Override
    public void validateContent() throws WebApplicationException {

        // TODO validate content
    }

    @Override
    public void update() {

        // nothing to do
    }

    @Override
    public void convert() {

        // nothing to do
    }

}
