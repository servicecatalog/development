/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 2013-1-10                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.webservices;

import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.oscm.logging.LoggerFactory;
import org.oscm.dataservice.local.DataService;
import org.oscm.webservices.logger.WebServiceLogger;
import org.oscm.intf.SamlService;

/**
 * @author Wenxin Gao
 * 
 */
@WebService(endpointInterface = "org.oscm.intf.SamlService")
public class SamlServiceWS implements SamlService {

     WebServiceLogger WS_LOGGER = new WebServiceLogger(
            LoggerFactory.getLogger(SamlServiceWS.class));

     org.oscm.internal.intf.SamlService delegate;
     DataService ds;
     WebServiceContext wsContext;

    @Override
    public String createSamlResponse(String requestId) {
        WS_LOGGER.logAccess(wsContext, ds);
        return delegate.createSamlResponse(requestId);
    }

}
