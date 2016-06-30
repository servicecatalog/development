/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 1, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.Representation;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRest.Action;

/**
 * Representation class for trigger actions
 * 
 * @author miethaner
 */
public class ActionRepresentation extends Representation {

    private Action description;

    public ActionRepresentation() {
    }

    public ActionRepresentation(Long id, Action description) {
        super(id);
        this.description = description;
    }

    public Action getDescription() {
        return description;
    }

    public void setDescription(Action description) {
        this.description = description;
    }

    @Override
    public void validateContent() throws WebApplicationException {

        // nothing to validate
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
