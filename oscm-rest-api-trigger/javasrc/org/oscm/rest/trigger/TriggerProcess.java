/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.Representation;

/**
 * Representation class of trigger processes.
 * 
 * @author miethaner
 */
public class TriggerProcess extends Representation {

    public enum Status {
        APPROVED, FAILED, REJECTED, CANCELED, WAITING_FOR_APPROVEL
    }

    public static class Links {
        private String definition_id;
        private String author_id;
        private Collection<String> parameter_ids;

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

        public Collection<String> getParameter_ids() {
            return parameter_ids;
        }

        public void setParameter_ids(Collection<String> parameter_ids) {
            this.parameter_ids = parameter_ids;
        }

        public Links() {
        }

        public Links(String definition_id, String author_id,
                Collection<String> parameter_ids) {
            this.definition_id = definition_id;
            this.author_id = author_id;
            this.parameter_ids = parameter_ids;
        }

    }

    private Date activation_time;
    private Status status;
    private String comment;
    private Links links;

    public TriggerProcess() {
    }

    public TriggerProcess(UUID id, Date activation_time, Status status,
            String comment, Links links) {
        super(id);
        this.activation_time = activation_time;
        this.status = status;
        this.comment = comment;
        this.links = links;
    }

    public Date getActivation_time() {
        return activation_time;
    }

    public void setActivation_time(Date activation_time) {
        this.activation_time = activation_time;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public void validateContent() throws WebApplicationException {
        // TODO validate content
    }

    @Override
    public void update() {

        // nothing to update in version 1
    }

    @Override
    public void convert() {

        // nothing to convert in version 1
    }

}
