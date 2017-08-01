/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 28.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.security;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

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

        try {
            credential.getUsernametoken().getName();
            credential.getUsernametoken().getPassword();

            UserPrincipal userPrincipal = new UserPrincipal(
                    credential.getUsernametoken().getName());

            Subject subject = new Subject();
            subject.getPrincipals().add(userPrincipal);

            List<String> userGroups = new ArrayList<String>();
            if (userGroups != null && userGroups.size() > 0) {
                for (String groupName : userGroups) {
                    RolePrincipal rolePrincipal = new RolePrincipal(groupName);
                    subject.getPrincipals().add(rolePrincipal);
                }
            }

            LoginContext loginContext = new LoginContext("bss-realm",
                    new WSCallbackHandler(
                            credential.getUsernametoken().getName(),
                            credential.getUsernametoken().getPassword()));
            loginContext.login();
            
            credential.setSubject(loginContext.getSubject());
         
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return credential;
    }

}
