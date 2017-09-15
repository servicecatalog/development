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
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;

/**
 * @author stavreva
 *
 */
public class WSValidator
        extends org.apache.openejb.server.cxf.OpenEJBLoginValidator {
        
private static final Log4jLogger logger = LoggerFactory
.getLogger(WSValidator.class);
        
    @Override
    public Credential validate(Credential credential, RequestData requestData) {
        logger.logDebug("DD1");

        try {

            final SecurityService securityService = SystemInstance.get()
                    .getComponent(SecurityService.class);
            final Object token;
            logger.logDebug("DD2");
            try {
                securityService.disassociate();

                logger.logDebug("DD3");
                token = securityService.login(
                        credential.getUsernametoken().getName(),
                        credential.getUsernametoken().getPassword());
                logger.logDebug("DD4");
                logger.logDebug("DDlogin = " + credential.getUsernametoken().getName());
                logger.logDebug("DDpwd = " + credential.getUsernametoken().getPassword());
                if (AbstractSecurityService.class.isInstance(securityService)
                        && AbstractSecurityService.class.cast(securityService)
                                .currentState() == null) {
                    logger.logDebug("DD5");
                    securityService.associate(token);
                }
            } catch (final LoginException e) {
                logger.logDebug("DD6");
                throw new SecurityException("cannot log user "
                        + credential.getUsernametoken().getName(), e);
            }

        } catch (Exception e) {
            logger.logDebug("DD7");
            e.printStackTrace();
        }
        logger.logDebug("DD8");
        return credential;
    }

}
