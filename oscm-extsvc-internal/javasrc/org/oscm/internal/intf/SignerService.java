/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 2011-01-15                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.intf;

import org.w3c.dom.Element;

import javax.ejb.Remote;

/**
 * Remote interface for signing SAML requests.
 * 
 */
@Remote
public interface SignerService {

    /**
     * Adds a signature to the SAML LogoutRequest form.
     *
     * @param logoutRequest
     * @return
     */

    Element signLogoutRequest(Element logoutRequest) throws Exception;
}
