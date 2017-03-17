/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: pock                                                     
 *                                                                              
 *  Creation Date: 14.01.2010                                             
 *                                                                              
 *******************************************************************************/
package org.oscm.security;

import javax.security.auth.login.LoginException;

import com.sun.appserv.security.AppservPasswordLoginModule;

/**
 * Glassfish JAAS LoginModule for the ADM Realm.
 * 
 */
public class ADMLoginModule extends AppservPasswordLoginModule {

    /**
     * Performs authentication for the current user.
     * 
     * @throws LoginException
     *             if the login failed.
     */
    @Override
    protected void authenticateUser() throws LoginException {
        if (!(_currentRealm instanceof ADMRealm)) {
            throw new LoginException("Bad realm.");
        }
        ADMRealm realm = (ADMRealm) _currentRealm;
        if (_password == null || _password.length() == 0) {
            throw new LoginException("Empty password.");
        }

        String grpList[] = realm.authenticateUser(_username, _password);
        String groupListToForward[] = new String[grpList.length];
        for (int i = 0; i < grpList.length; i++) {
            groupListToForward[i] = grpList[i];
        }

        commitUserAuthentication(groupListToForward);
    }

}
