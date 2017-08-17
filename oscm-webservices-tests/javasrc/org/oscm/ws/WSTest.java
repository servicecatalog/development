/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 24.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import org.junit.Test;
import org.oscm.intf.AccountService;
import org.oscm.test.ws.SecurityHandler;

/**
 * @author stavreva
 *
 */
public class WSTest {

    @Test
    public void testSaveDefaultVat_vatNull() throws Exception {

        Service service = Service.create(
                new URL("http://localhost:8180/oscm-webservices/AccountService/BASIC?wsdl"),
                new QName("http://oscm.org/xsd", "AccountService"));
        AccountService vatService = service.getPort(AccountService.class);

        final Binding binding = ((BindingProvider) vatService).getBinding();
        List<Handler> handlerList = binding.getHandlerChain();
        if (handlerList == null)
            handlerList = new ArrayList<>();

        handlerList.add(new SecurityHandler("23000", "secret"));
        binding.setHandlerChain(handlerList); // <- important!

        // save default vat rate
        vatService.getDefaultPaymentConfiguration();
    }

}
