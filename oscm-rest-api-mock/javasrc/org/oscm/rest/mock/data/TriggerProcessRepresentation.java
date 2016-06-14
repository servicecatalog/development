/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.mock.data;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.Representation;

/**
 * Representation class of trigger processes.
 * 
 * @author miethaner
 */
public class TriggerProcessRepresentation extends Representation {

    public enum Status {
        APPROVED, FAILED, REJECTED, CANCELED, WAITING_FOR_APPROVEL
    }

    public static class Author {
        private Long id;
        private String email;

        public Author() {
        }

        public Author(Long id, String email) {
            this.id = id;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Links {
        private Long definition_id;
        private Long author_id;
        private Long subscription_id;

        public Links() {
        }

        public Links(Long definition_id, Long author_id, Long subscription_id) {
            this.definition_id = definition_id;
            this.author_id = author_id;
            this.subscription_id = subscription_id;
        }

        public Long getDefinition_id() {
            return definition_id;
        }

        public void setDefinition_id(Long definition_id) {
            this.definition_id = definition_id;
        }

        public Long getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(Long author_id) {
            this.author_id = author_id;
        }

        public Long getSubscription_id() {
            return subscription_id;
        }

        public void setSubscription_id(Long subscription_id) {
            this.subscription_id = subscription_id;
        }
    }

    private Date activation_time;
    private Status status;
    private String comment;
    private Author author;
    private Links links;

    public TriggerProcessRepresentation() {
    }

    public TriggerProcessRepresentation(Long id, Date activation_time,
            Status status, String comment, Author author, Links links) {
        super(id);
        this.activation_time = activation_time;
        this.status = status;
        this.comment = comment;
        this.author = author;
        this.links = links;
    }

    public String getStatusRest() {
        if (status != null) {
            return status.toString();
        } else {
            return null;
        }
    }

    public void setStatus(String status) {
        if (status != null) {
            this.status = Status.valueOf(status);
        } else {
            this.status = null;
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getActivationTime() {
        return activation_time;
    }

    public void setActivitionTime(Date activition_time) {
        this.activation_time = activition_time;
    }

    public Long getDefinitionId() {
        if (links != null) {
            return links.getDefinition_id();
        } else {
            return null;
        }
    }

    public void setDefinitionId(Long definition_id) {
        if (links == null) {
            links = new Links();
        }

        links.setDefinition_id(definition_id);
    }

    public Long getAuthorId() {
        if (links != null) {
            return links.getAuthor_id();
        } else {
            return null;
        }
    }

    public void setAuthorId(Long author_id) {
        if (links == null) {
            links = new Links();
        }

        links.setAuthor_id(author_id);
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Long getSubscriptionId() {
        if (links != null) {
            return links.getSubscription_id();
        } else {
            return null;
        }
    }

    public void setSubscriptionId(Long subscription_id) {
        if (links == null) {
            links = new Links();
        }

        links.setSubscription_id(subscription_id);
    }

    @Override
    public void validateContent() throws WebApplicationException {
    }

    @Override
    public void update() {
    }

    @Override
    public void convert() {
    }
}
