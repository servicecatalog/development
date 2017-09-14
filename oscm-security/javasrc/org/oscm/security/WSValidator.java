/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 28.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.security;

import javax.security.auth.login.LoginException;

import org.apache.openejb.core.security.AbstractSecurityService;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.validate.Credential;

/**
 * @author stavreva
 *
 */
public class WSValidator
        extends org.apache.openejb.server.cxf.OpenEJBLoginValidator {

    @Override
    public Credential validate(Credential credential, RequestData requestData) {
        System.out.println("1");

        try {

            final SecurityService securityService = SystemInstance.get()
                    .getComponent(SecurityService.class);
            final Object token;
            System.out.println("2");
            try {
                securityService.disassociate();

                System.out.println("3");
                token = securityService.login(
                        credential.getUsernametoken().getName(),
                        credential.getUsernametoken().getPassword());
                System.out.println("4");
                System.out.println("login = " + credential.getUsernametoken().getName());
                System.out.println("pwd = " + credential.getUsernametoken().getPassword());
                if (AbstractSecurityService.class.isInstance(securityService)
                        && AbstractSecurityService.class.cast(securityService)
                                .currentState() == null) {
                    System.out.println("5");
                    securityService.associate(token);
                }
            } catch (final LoginException e) {
                System.out.println("6");
                throw new SecurityException("cannot log user "
                        + credential.getUsernametoken().getName(), e);
            }

        } catch (Exception e) {
            System.out.println("7");
            e.printStackTrace();
        }
        System.out.println("8");
        return credential;
    }

}
