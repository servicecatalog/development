/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2019
 *                                                                                                                                 
 *  Creation Date: 2019-1-21                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.junit.Test;
import org.oscm.intf.IdentityService;

public class WSClient {
    
    @Test
    public void testClient() throws MalformedURLException{
        System.out.println("Starting WS client app");

        //https://g02dexxnow29066.g02.fujitsu.local:8181/oscm/IdentityService/BASIC?wsdl

        String baseHttp = "http://g02dexxnow29066.g02.fujitsu.local:8180";
        String serviceName = "IdentityService";
        String authType = "BASIC";

        String wsdlUrl = baseHttp + "/oscm/" + serviceName + "/" + authType + "?wsdl";

        System.out.println(wsdlUrl);

        URL url = new URL(wsdlUrl);
        QName qName = new QName("http://oscm.org/xsd", serviceName);
        Service service = Service.create(url, qName);

        IdentityService identityService = service.getPort(IdentityService.class);


        BindingProvider bindingProvider = (BindingProvider) identityService;
        Map<String, Object> clientRequestContext = bindingProvider.getRequestContext();

        clientRequestContext.put(BindingProvider.USERNAME_PROPERTY, "administrator");
        clientRequestContext.put(BindingProvider.PASSWORD_PROPERTY, "admin123");
        clientRequestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseHttp + "/" + serviceName + "/" + authType);

        System.out.println(identityService.getCurrentUserDetails());
    }

}
