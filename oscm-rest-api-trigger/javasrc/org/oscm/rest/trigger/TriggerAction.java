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

/**
 * Representation class of trigger process identifiers.
 * 
 * @author miethaner
 *
 */
public class TriggerAction extends Representation {

    private String description;

    public TriggerAction() {
    }

    public TriggerAction(UUID id, String description) {
        super(id);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
