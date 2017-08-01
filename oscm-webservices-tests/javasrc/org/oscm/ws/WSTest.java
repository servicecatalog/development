/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 24.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws;

import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import org.junit.Assert;
import org.junit.Test;
import org.oscm.intf.VatService;
import org.oscm.ws.base.SecurityHandler;

/**
 * @author stavreva
 *
 */
public class WSTest {

    @Test
    public void testSaveDefaultVat_vatNull() throws Exception {


        Service service = Service.create(
                new URL("http://eststavreva:8080/oscm-webservices/VatService/BASIC?wsdl"),
                new QName("http://oscm.org/xsd", "VatService"));
        assertNotNull(service);

        VatService vatService = service.getPort(VatService.class);

         final Binding binding = ((BindingProvider) vatService).getBinding();
         List<Handler> handlerList = binding.getHandlerChain();
         if (handlerList == null)
         handlerList = new ArrayList<Handler>();
        
         handlerList.add(new SecurityHandler("11000", "secret"));
         binding.setHandlerChain(handlerList); // <- important!

//        BindingProvider bindingProvider = (BindingProvider) vatService;
//        Map<String, Object> clientRequestContext = bindingProvider
//                .getRequestContext();
//        clientRequestContext.put(BindingProvider.USERNAME_PROPERTY, "11000");
//        clientRequestContext.put(BindingProvider.PASSWORD_PROPERTY, "secret");
//        clientRequestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
//                "http://eststavreva:8080/oscm-webservices/VatService/BASIC");


        // save default vat rate
        vatService.saveDefaultVat(null);

        // checks if default vat rate is null and vat support is disabled.
        Assert.assertFalse(vatService.getVatSupport());
        Assert.assertNull(vatService.getDefaultVat());

    }

    // @Test(expected = SOAPFaultException.class)
    // public void getTagsByLocale_null() throws MalformedURLException {
    //
    // Service service = Service.create(
    // new
    // URL("http://eststavreva:8080/oscm-webservices/TagService/BASIC?wsdl"),
    // new QName("http://oscm.org/xsd", "TagService"));
    // assertNotNull(service);
    //
    // TagService tagService = service.getPort(TagService.class);
    //
    // BindingProvider bindingProvider = (BindingProvider) tagService;
    // Map<String, Object> clientRequestContext = bindingProvider
    // .getRequestContext();
    // clientRequestContext.put(BindingProvider.USERNAME_PROPERTY, "11000");
    // clientRequestContext.put(BindingProvider.PASSWORD_PROPERTY, "secret");
    // clientRequestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
    // "http://eststavreva:8080/oscm-webservices/TagService/BASIC");
    // List<VOTag> tags = tagService.getTagsByLocale(null);
    // assertEquals(0, tags.size());
    // }

}
