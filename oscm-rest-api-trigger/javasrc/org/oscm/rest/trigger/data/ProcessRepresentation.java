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
import org.oscm.rest.trigger.interfaces.TriggerProcessRest;

/**
 * Representation class of trigger processes.
 * 
 * @author miethaner
 */
public class ProcessRepresentation extends Representation implements
        TriggerProcessRest {

    private String comment;

    public ProcessRepresentation() {
    }

    public ProcessRepresentation(Long id, String comment) {
        super(id);
        this.comment = comment;
    }

    public ProcessRepresentation(TriggerProcessRest process) {
        super(process.getId());
        this.comment = process.getComment();
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void validateContent() throws WebApplicationException {

        if (comment != null && !comment.matches(CommonParams.PATTERN_STRING)) {
            throw WebException.badRequest().property("comment")
                    .message("property does not match allowed pattern").build();
        }
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
