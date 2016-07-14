/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 2011-01-15                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.intf;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Remote interface for single sign-on based on SAML (Security Assertion Markup
 * Language).
 * 
 * @deprecated as of release 16.1.0
 * 
 */
@Deprecated
@Remote
@WebService(targetNamespace = "http://oscm.org/xsd")
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
    @WebMethod
    String createSamlResponse(@WebParam(name = "requestId") String requestId);

}
