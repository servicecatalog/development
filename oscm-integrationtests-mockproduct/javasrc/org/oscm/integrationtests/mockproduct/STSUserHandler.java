/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * @author gao
 * 
 */
public class STSUserHandler implements CallbackHandler {

    private String userKey;
    private String password;

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(userKey);
            } else if (callback instanceof PasswordCallback) {
                ((PasswordCallback) callback).setPassword(password
                        .toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback,
                        "Unrecognized Callback");
            }
        }
    }

}
