/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 25, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

import java.util.Collection;

import javax.ejb.Remote;

import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.BadDataException;
import org.oscm.rest.external.exceptions.ConflictException;
import org.oscm.rest.external.exceptions.DataException;
import org.oscm.rest.external.exceptions.NotFoundException;

/**
 * Interface for trigger definition services called by the rest api
 * 
 * @author miethaner
 */
@Remote
public interface TriggerDefinitionRestService {

    /**
     * Creates a new TriggerDefiniton entry from the given object and returns
     * the generated entity key
     * 
     * @param definition
     *            the trigger definition object
     * @return the generated resource UUID
     * @throws ConflictException
     * @throws BadDataException
     */
    public Long createDefinition(TriggerDefinitionRest definition)
            throws ConflictException, BadDataException;

    /**
     * Deletes the TriggerDefiniton entry with the given entity key
     * 
     * @param key
     *            the entity key
     * @throws ConflictException
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public void deleteDefinition(Long key) throws ConflictException,
            AuthorizationException, NotFoundException;

    /**
     * Updates the TriggerDefinition entry corresponding to the given object.
     * 
     * @param definition
     *            the trigger definition object with entity key
     * @throws ConflictException
     * @throws AuthorizationException
     * @throws NotFoundException
     * @throws BadDataException
     * @throws DataException
     */
    public void updateDefinition(TriggerDefinitionRest definition)
            throws ConflictException, AuthorizationException,
            NotFoundException, BadDataException, DataException;

    /**
     * Gets all available TriggerDefinition entries.
     * 
     * @return the trigger definition entries
     */
    public Collection<TriggerDefinitionRest> getDefinitions();

    /**
     * Gets the TriggerDefinition entry with the given entity key
     * 
     * @param key
     *            the entity key
     * @return the trigger definition entry
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public TriggerDefinitionRest getDefinition(Long key)
            throws AuthorizationException, NotFoundException;
}
