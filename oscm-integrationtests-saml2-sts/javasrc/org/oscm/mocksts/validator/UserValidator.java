/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 27.06.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.mocksts.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback.PasswordValidationException;

/**
 * @author gao
 */
public class UserValidator implements
        PasswordValidationCallback.PasswordValidator {

    private static Map<String, String> userList;

    @Override
    public boolean validate(PasswordValidationCallback.Request request)
            throws PasswordValidationCallback.PasswordValidationException {
        PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest = (PasswordValidationCallback.PlainTextPasswordRequest) request;
        try {
            return checkUser(plainTextRequest.getUsername(),
                    plainTextRequest.getPassword());
        } catch (IOException e) {
            throw new PasswordValidationCallback.PasswordValidationException(
                    "Load user data failed. Authentication failed.");
        }
    }

    private boolean checkUser(String userName, String password)
            throws IOException, PasswordValidationException {
        if (userList == null) {
            loadUserList();
        }
        Set<String> userIds = userList.keySet();
        for (String userId : userIds) {
            Pattern pattern = Pattern.compile(userId);
            Matcher matcher = pattern.matcher(userName);
            boolean result = matcher.find();
            if (result && userList.get(userId).equals(password)) {
                return true;
            }
        }
        throw new PasswordValidationCallback.PasswordValidationException(
                "Invalid credentials provided. Authentication failed.");
    }

    private void loadUserList() throws IOException {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("UserList");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));) {
            userList = new HashMap<String, String>();
            String data = null;
            while ((data = reader.readLine()) != null) {
                if (!data.trim().isEmpty()) {
                    String[] userData = data.split("@");
                    userList.put(userData[0], userData[1]);
                }
            }
        }
    }
}
