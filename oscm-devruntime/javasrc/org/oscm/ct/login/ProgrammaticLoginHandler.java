/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 12.03.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ct.login;

import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

/**
 * Provides the login handling for the glassfish application server.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class ProgrammaticLoginHandler implements LoginHandler {

    @Override
    public void login(String username, String password) throws Exception {
        ProgrammaticLogin pl = new ProgrammaticLogin();
        pl.login(username, password.toCharArray());
    }

    @Override
    public void logout() throws Exception {
        ProgrammaticLogin pl = new ProgrammaticLogin();
        pl.logout(false);
    }

}
