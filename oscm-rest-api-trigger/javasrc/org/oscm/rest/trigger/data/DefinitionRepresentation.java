/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.WebException;
import org.oscm.rest.trigger.config.TriggerCommonParams;
import org.oscm.rest.trigger.interfaces.OrganizationRest;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRest;
import org.oscm.validator.ADMValidator;

/**
 * Representation class of trigger definitions.
 * 
 * @author miethaner
 */
public class DefinitionRepresentation extends Representation implements
        TriggerDefinitionRest {

    public static class Owner implements OrganizationRest {
        private Long id;
        private String description;

        public Owner() {
        }

        public Owner(Long id, String description) {
            this.id = id;
            this.description = description;
        }

        public Owner(OrganizationRest owner) {
            this.id = owner.getId();
            this.description = owner.getName();
        }

        @Override
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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
        private Long owner_id;

        public Links() {
        }

        public Links(Long owner_id) {
            this.owner_id = owner_id;
        }

        public Long getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(Long owner_id) {
            this.owner_id = owner_id;
        }
    }

    private String description;
    private Boolean suspend;
    private String target_type;
    private String target_url;
    private Owner owner;
    private String action;
    private Links links;

    public DefinitionRepresentation() {
    }

    public DefinitionRepresentation(Long id, String description,
            Boolean suspend, String target_type, String target_url,
            String action, Owner owner, Links links) {
        super(id);
        this.description = description;
        this.suspend = suspend;
        this.target_type = target_type;
        this.target_url = target_url;
        this.owner = owner;
        this.action = action;
        this.links = links;
    }

    public DefinitionRepresentation(TriggerDefinitionRest definition) {
        super(definition.getId());
        setTag(definition.getTag());
        this.description = definition.getDescription();
        this.suspend = definition.isSuspending();
        this.target_type = definition.getServiceType();
        this.target_url = definition.getTargetURL();
        this.action = definition.getAction();
        if (definition.getOwner() != null) {
            this.owner = new Owner(definition.getOwner().getId(), definition
                    .getOwner().getName());
        } else {
            this.owner = null;
        }
        this.links = new Links(definition.getOwnerId());
    }

    @Override
    public Long getOwnerId() {
        if (links != null) {
            return links.getOwner_id();
        } else {
            return null;
        }
    }

    public void setOwnerId(Long owner_id) {
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
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getServiceType() {
        return target_type;
    }

    public void setServiceType(String target_type) {
        this.target_type = target_type;
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

        if (description != null
                && !description.matches(CommonParams.PATTERN_STRING)) {
            throw WebException.badRequest()
                    .property(TriggerCommonParams.PROPERTY_DESCRIPTION)
                    .message(CommonParams.ERROR_BAD_PROPERTY).build();
        }

        if (target_type != null) {
            try {
                TargetType.valueOf(target_type);
            } catch (IllegalArgumentException e) {
                throw WebException.badRequest()
                        .property(TriggerCommonParams.PROPERTY_TARGET_TYPE)
                        .message(CommonParams.ERROR_BAD_PROPERTY).build();
            }
        }

        if (target_url != null && !ADMValidator.isUrl(target_url)) {
            throw WebException.badRequest()
                    .property(TriggerCommonParams.PROPERTY_TARGET_URL)
                    .message(CommonParams.ERROR_BAD_PROPERTY).build();
        }

        if (action != null) {
            try {
                TriggerDefinitionRest.Action.valueOf(action);
            } catch (IllegalArgumentException e) {
                throw WebException.badRequest()
                        .property(TriggerCommonParams.PROPERTY_ACTION)
                        .message(CommonParams.ERROR_BAD_PROPERTY).build();
            }
        }
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
