/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 27.06.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.mocksts.handler;

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

    private final String userId = "STSUser1";
    private final String password = "password1";

    @Override
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(userId);
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
