/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.Representation;
import org.oscm.rest.trigger.interfaces.TriggerProcessRest;
import org.oscm.rest.trigger.interfaces.UserRest;

/**
 * Representation class of trigger processes.
 * 
 * @author miethaner
 */
public class TriggerProcessRepresentation extends Representation implements
        TriggerProcessRest {

    public static class Author implements UserRest {
        private UUID id;
        private String email;

        public Author() {
        }

        public Author(UUID id, String email) {
            this.id = id;
            this.email = email;
        }

        public Author(UserRest author) {
            this.id = author.getId();
            this.email = author.getEmail();
        }

        @Override
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        @Override
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Links {
        private String definition_id;
        private String author_id;

        public Links() {
        }

        public Links(String definition_id, String author_id) {
            this.definition_id = definition_id;
            this.author_id = author_id;
        }

        public String getDefinition_id() {
            return definition_id;
        }

        public void setDefinition_id(String definition_id) {
            this.definition_id = definition_id;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

    }

    private Date activation_time;
    private Status status;
    private String comment;
    private Author author;
    private Links links;

    public TriggerProcessRepresentation() {
    }

    public TriggerProcessRepresentation(UUID id, Date activation_time,
            Status status, String comment, Author author, Links links) {
        super(id);
        this.activation_time = activation_time;
        this.status = status;
        this.comment = comment;
        this.author = author;
        this.links = links;
    }

    public TriggerProcessRepresentation(TriggerProcessRest process) {
        super(UUID.fromString(process.getResourceId()));
        this.activation_time = process.getActivitionTime();
        this.status = process.getStatus();
        this.comment = process.getComment();
        this.author = new Author(process.getAuthor());
        this.links = new Links(process.getDefinitionId(), process.getAuthorId());
    }

    @Override
    public String getResourceId() {
        return getId().toString();
    }

    public void setResourceId(String id) {
        setId(UUID.fromString(id));
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Date getActivitionTime() {
        return activation_time;
    }

    public void setActivitionTime(Date activition_time) {
        this.activation_time = activition_time;
    }

    @Override
    public String getDefinitionId() {
        if (links != null) {
            return links.getDefinition_id();
        } else {
            return null;
        }
    }

    public void setDefinitionId(String definition_id) {
        if (links == null) {
            links = new Links();
        }

        links.setDefinition_id(definition_id);
    }

    @Override
    public String getAuthorId() {
        if (links != null) {
            return links.getAuthor_id();
        } else {
            return null;
        }
    }

    public void setAuthorId(String author_id) {
        if (links == null) {
            links = new Links();
        }

        links.setAuthor_id(author_id);
    }

    @Override
    public UserRest getAuthor() {
        return author;
    }

    public void setAuthor(UserRest author) {
        this.author = new Author(author);
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
