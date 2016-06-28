/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 2011-01-15                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.intf;

import org.oscm.internal.types.exception.SaaSApplicationException;

import javax.ejb.Remote;

/**
 * Remote interface for single sign-on based on SAML (Security Assertion Markup
 * Language).
 * 
 */
@Remote
public interface SamlService {

    /**
     * Creates a SAML assertion based on the credentials of the calling user.
     * This assertion can be sent to a service provider, for example, to grant
     * access to specific resources.
     * <p>
     * Basically, the assertion contains the user ID and the time period the
     * assertion is valid. The assertion is digitally signed to protect it
     * against modifications.
     * <p>
     * Required role: none
     * 
     * @param requestId
     *            optionally, the SAML authentication request ID to be embedded
     *            in the SAML response. If <code>null</code>, no request ID is
     *            included in the response.
     * @return the SAML response
     */

    String createSamlResponse(String requestId);

}
