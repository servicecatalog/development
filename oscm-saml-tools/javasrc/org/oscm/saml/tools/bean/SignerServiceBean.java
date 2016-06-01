/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.tools.bean;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBElement;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.SamlService;
import org.oscm.internal.intf.SignerService;
import org.oscm.saml.api.Saml20Signer;
import org.oscm.saml.api.SamlSigner;
import org.oscm.saml.tools.Saml20KeyLoader;
import org.oscm.saml2.api.Marshalling;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;
import org.w3c.dom.Element;

@Stateless
@Remote(SamlService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SignerServiceBean implements SignerService {

    @EJB(beanInterface = ConfigurationServiceLocal.class)
    private ConfigurationServiceLocal configService;

    private Marshalling<LogoutRequestType>
            marshaller = new Marshalling<>();

    @Override
    public JAXBElement signLogoutRequest(JAXBElement logoutRequest) throws Exception {
        Saml20KeyLoader keyLoader = new Saml20KeyLoader(configService);
        Saml20Signer signer = new SamlSigner(keyLoader.getPrivateKey());

        signer.setPublicCertificate(keyLoader.getPublicCertificate());
        Element signed = signer.signSamlElement(marshaller.marshallElement(logoutRequest).getDocumentElement(), null);
        return marshaller.unmarshallDocument(signed, LogoutRequestType.class);
    }



}
