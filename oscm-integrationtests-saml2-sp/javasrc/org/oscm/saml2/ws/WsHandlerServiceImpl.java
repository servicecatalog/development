/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 07.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.saml2.ws;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author kulle
 * 
 */
@WebService(endpointInterface = "org.oscm.saml2.ws.WsHandlerService", portName = "WsHandlerServiceImplPort", serviceName = "WsHandlerService", targetNamespace = "http://oscm.org/xsd")
@HandlerChain(file = "handlers.xml")
public class WsHandlerServiceImpl implements WsHandlerService {

    @Override
    @WebMethod
    public String greetings(@WebParam(name = "name") String name) {
        return "Hello " + name;
    }

}
