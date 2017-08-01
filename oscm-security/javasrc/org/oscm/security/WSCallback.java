/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 24.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.security;

import java.io.IOException;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;
import org.apache.wss4j.common.ext.WSPasswordCallback;

/**
 * @author stavreva
 *
 */
//Not used
public class WSCallback implements CallbackHandler {

    private static final Logger logger = Logger
            .getLogger(WSCallback.class.getName());

    @Override
    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
       
        String userId = pc.getIdentifier();

        if (userId == null) {
            throw new IOException("wrong login name " + pc.getIdentifier());
        }
        
        //get pwd from database
        pc.setPassword("secret");
        
//        SecurityService securityService = SystemInstance.get().getComponent(SecurityService.class);
//        Object token = null;
//        try {
//            securityService.disassociate();
//            token = securityService.login("bss-realm", pc.getIdentifier(), pc.getPassword());
//            securityService.associate(token);
//
//        } catch (LoginException e) {
//            e.printStackTrace();
//            throw new SecurityException("wrong password");
//        }
//        
        

        //TODO lookup BSSDS failed in this context
//        ADMRealmImpl realmImpl = new ADMRealmImpl(logger);
//        try {
//            realmImpl.authenticateUser(pc.getIdentifier(), pc.getPassword());
//        } catch (LoginException e) {
//            throw new IOException("wrong password " + pc.getPassword() + " for "
//                    + pc.getIdentifier());
//        }
        
//        userPrincipal = new UserPrincipal(login);
//        subject.getPrincipals().add(userPrincipal);
//
//        if (userGroups != null && userGroups.size() > 0) {
//            for (String groupName : userGroups) {
//                rolePrincipal = new RolePrincipal(groupName);
//                subject.getPrincipals().add(rolePrincipal);
//            }
//        }
    }
}
