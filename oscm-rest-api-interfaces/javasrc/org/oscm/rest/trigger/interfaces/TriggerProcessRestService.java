/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 25, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

import javax.ejb.Remote;

import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.ConflictException;
import org.oscm.rest.external.exceptions.DataException;
import org.oscm.rest.external.exceptions.NotFoundException;

/**
 * Interface for trigger process services called by the rest api
 * 
 * @author miethaner
 */
@Remote
public interface TriggerProcessRestService {

    /**
     * Approves the given trigger process
     * 
     * @param process
     *            the trigger process data
     * @throws NotFoundException
     * @throws AuthorizationException
     * @throws DataException
     * @throws ConflictException
     */
    public void approve(TriggerProcessRest process) throws NotFoundException,
            AuthorizationException, ConflictException;

    /**
     * Rejects the given trigger process
     * 
     * @param process
     *            the trigger process data
     * @throws NotFoundException
     * @throws AuthorizationException
     * @throws DataException
     */
    public void reject(TriggerProcessRest process) throws NotFoundException,
            AuthorizationException, ConflictException;

    /**
     * Cancels the given trigger process
     * 
     * @param process
     *            the trigger process data
     * @throws NotFoundException
     * @throws AuthorizationException
     * @throws DataException
     */
    public void cancel(TriggerProcessRest process) throws NotFoundException,
            AuthorizationException, ConflictException;

}
