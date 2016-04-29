/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 18.11.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.test.ws;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.oscm.apiversioning.handler.ClientVersionHandler;

import com.sun.xml.wss.XWSSConstants;

/**
 * @author stavreva
 * 
 */
public class WebServiceProxy {

    public static <T> T get(String baseUrl, String version, String auth,
            String namespace, Class<T> remoteInterface, String userName,
            String password) throws Exception {
        return get(baseUrl, version, version, auth, namespace, remoteInterface,
                userName, password);
    }

    public static <T> T get(String baseUrl, final String versionWSDL,
            String versionHeader, String auth, String namespace,
            Class<T> remoteInterface, String userName, String password)
            throws Exception {
        String wsdlUrl = baseUrl + "/oscm/" + versionWSDL + "/"
                + remoteInterface.getSimpleName() + "/" + auth + "?wsdl";
        URL url = new URL(wsdlUrl);
        QName qName = new QName(namespace, remoteInterface.getSimpleName());
        Service service = Service.create(url, qName);
        if ("v1.7".equals(versionWSDL) || "v1.8".equals(versionWSDL)) {
            service.setHandlerResolver(new HandlerResolver() {
                @SuppressWarnings("rawtypes")
                @Override
                public List<Handler> getHandlerChain(PortInfo portInfo) {
                    List<Handler> handlerList = new ArrayList<Handler>();
                    handlerList.add(new VersionHandlerCtmg(versionWSDL));
                    return handlerList;
                }
            });
        } else {
            ClientVersionHandler versionHandler = new ClientVersionHandler(
                    versionHeader);
            service = versionHandler.addVersionInformationToClient(service);
        }
        T port = service.getPort(remoteInterface);
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> clientRequestContext = bindingProvider
                .getRequestContext();
        if ("STS".equals(auth)) {
            clientRequestContext.put(XWSSConstants.USERNAME_PROPERTY, userName);
            clientRequestContext.put(XWSSConstants.PASSWORD_PROPERTY, password);
        } else {
            clientRequestContext.put(BindingProvider.USERNAME_PROPERTY,
                    userName);
            clientRequestContext.put(BindingProvider.PASSWORD_PROPERTY,
                    password);
            clientRequestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    baseUrl + "/" + remoteInterface.getSimpleName() + "/"
                            + auth);
        }
        return port;
    }
}
