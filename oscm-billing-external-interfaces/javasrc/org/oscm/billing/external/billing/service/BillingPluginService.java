/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                  
 *                                                                                                                                 
 *  Creation Date: 26.01.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.billing.service;



import javax.ejb.Remote;

import org.oscm.billing.external.exception.BillingException;

/**
 * Interface for general tasks of external billing system
 */
@Remote
public interface BillingPluginService {

    /**
     * Test the connection to the external billing system
     * 
     * @throws BillingException
     *             if the connection cannot be established
     */
    public void testConnection() throws BillingException;

}
