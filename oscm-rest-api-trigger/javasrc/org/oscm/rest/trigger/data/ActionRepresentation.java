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

/**
 * Representation class for trigger actions
 * 
 * @author miethaner
 */
public class ActionRepresentation extends Representation {

    public enum Action {
        SUBSCRIBE_TO_SERVICE, UNSUBSCRIBE_FROM_SERVICE, MODIFY_SUBSCRIPTION
    }

    private Action description;

    public ActionRepresentation() {
    }

    public ActionRepresentation(Long id, Action description) {
        this.setId(id);
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
