/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 05.06.2013                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.mocksts.ws;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author gao
 * 
 */
@HandlerChain(file = "handlers.xml", name = "SoapHandlerService")
@WebService(serviceName = "MockWebService")
public class MockWebService {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
}
