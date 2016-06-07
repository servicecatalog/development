/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 25, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

import java.util.List;

import javax.ejb.Remote;

import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.DatabaseConflictException;
import org.oscm.rest.external.exceptions.DatabaseErrorException;

/**
 * Interface for trigger defintion services called by the rest api
 * 
 * @author miethaner
 */
@Remote
public interface TriggerDefinitionRestService {

    // TODO throws ...Exception, Parameter input

    /**
     * Creates a new TriggerDefiniton entry from the given object and returns
     * the generated resource UUID
     * 
     * @param definition
     *            the trigger definition object
     * @return the generated resource UUID
     * @throws DatabaseErrorException
     * @throws DatabaseConflictException
     */
    public String createDefiniton(TriggerDefinitionRest definition)
            throws DatabaseErrorException, DatabaseConflictException;

    /**
     * Deletes the TriggerDefiniton entry with the given resource ID
     * 
     * @param id
     *            the resource ID
     * @throws DatabaseErrorException
     * @throws DatabaseConflictException
     * @throws AuthorizationException
     */
    public void deleteDefinition(String id) throws DatabaseErrorException,
            DatabaseConflictException, AuthorizationException;

    /**
     * Updates the TriggerDefinition entry corresponding to the given object.
     * 
     * @param definition
     *            the trigger definition object with resource ID
     * @throws DatabaseErrorException
     * @throws DatabaseConflictException
     * @throws AuthorizationException
     */
    public void updateDefinition(TriggerDefinitionRest definition)
            throws DatabaseErrorException, DatabaseConflictException,
            AuthorizationException;

    /**
     * Gets all available TriggerDefinition entries.
     * 
     * @return the trigger definition entries
     */
    public List<TriggerDefinitionRest> getDefinitions();

    /**
     * Gets the TriggerDefinition entry with the given resource ID
     * 
     * @param id
     *            the resource ID
     * @return the trigger definition entry
     * @throws DatabaseErrorException
     * @throws AuthorizationException
     */
    public TriggerDefinitionRest getDefinition(String id)
            throws DatabaseErrorException, AuthorizationException;
}
