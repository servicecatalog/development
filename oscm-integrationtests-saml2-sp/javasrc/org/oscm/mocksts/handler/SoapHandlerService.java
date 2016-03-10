/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 07.06.2013                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.mocksts.handler;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author Gao
 * 
 */
@WebService(targetNamespace = "http://oscm.org/xsd")
public interface SoapHandlerService {

    @WebMethod
    public void initMessageList();

    @WebMethod
    public List<String> getCollectedMessages();
}
