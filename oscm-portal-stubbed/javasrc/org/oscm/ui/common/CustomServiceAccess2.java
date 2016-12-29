/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: pock                                                      
 *                                                                              
 *  Creation Date: 09.12.2010                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.ui.common;

import javax.naming.CommunicationException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.ui.services.MockService;
import org.oscm.internal.vo.VOUser;

/**
 * Implementation to access the mock service
 */
public class CustomServiceAccess2 extends ServiceAccess {

    protected CustomServiceAccess2() {
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        T service = clazz.cast(MockService.getInstance());
        return service;
    }

    @Override
    public void doLogin(VOUser userObject, String password,
            HttpServletRequest request, HttpServletResponse response)
            throws CommunicationException, LoginException {
        // do nothing
    }

    @Override
    protected boolean createSession() {
        return true;
    }
}
