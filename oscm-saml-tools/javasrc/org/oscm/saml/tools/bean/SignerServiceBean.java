/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.tools.bean;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.SignerService;
import org.oscm.internal.types.exception.SAMLSigningException;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.saml.api.Saml20Signer;
import org.oscm.saml.tools.Saml20KeyLoader;
import org.w3c.dom.Element;

@Stateless
@Remote(SignerService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SignerServiceBean implements SignerService {

    @EJB(beanInterface = ConfigurationServiceLocal.class)
    private ConfigurationServiceLocal configService;

    @Override
    public Element signLogoutRequest(Element logoutRequest) throws SAMLSigningException {
        Saml20KeyLoader keyLoader = new Saml20KeyLoader(configService);
        Saml20Signer signer = new Saml20Signer(keyLoader.getPrivateKey());

        signer.setPublicCertificate(keyLoader.getPublicCertificate());
        try {
            return signer.signSamlElement(logoutRequest, null);
        } catch (SaaSSystemException e) {
            throw new SAMLSigningException();
        }
    }

}
