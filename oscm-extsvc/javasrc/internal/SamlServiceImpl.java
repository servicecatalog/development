/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 2013-1-11                                                      
 *                                                                              
 *******************************************************************************/

package internal;

import javax.jws.WebService;

import org.oscm.intf.ReviewService;
import org.oscm.intf.SamlService;

/**
 * This is a stub implementation of the {@link ReviewService} as the Metro
 * jax-ws tools do not allow to generate WSDL files from the service interfaces.
 * 
 * <p>
 * <b>WARNING:</b> Do not use this class, all methods simply throw an
 * {@link UnsupportedOperationException}!
 * </p>
 * 
 * @author Wenxin Gao
 * 
 */
@WebService(serviceName = "SamlService", targetNamespace = "http://oscm.org/xsd", portName = "SamlServicePort", endpointInterface = "org.oscm.intf.SamlService")
public class SamlServiceImpl implements SamlService {

    @Override
    public String createSamlResponse(String requestId) {
        throw new UnsupportedOperationException();
    }

}
