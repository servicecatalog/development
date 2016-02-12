/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 07.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author kulle
 * 
 */
@WebService(targetNamespace = "http://oscm.org/xsd")
public interface WsHandlerService {

    @WebMethod
    public String greetings(@WebParam(name = "name") String name);

}
