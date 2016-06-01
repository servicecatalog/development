/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 2011-01-15                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.intf;

import javax.ejb.Remote;
import javax.xml.bind.JAXBElement;

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
    JAXBElement signLogoutRequest(JAXBElement logoutRequest) throws Exception;
}
