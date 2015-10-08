/*
 * Copyright 2005 Anders Nyman.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.j2ep.model;

import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpMethod;

/**
 * A utility class that will be used throughout the 
 * project to process headers to filter out those
 * not allowed by the proxy.
 *
 * @author Anders Nyman
 */
public class AllowedMethodHandler {
    
    /** 
     * The methods handled by the proxy.
     */
    private static String allowString;
    
    /** 
     * A set of the HTTP methods allowed by the proxy.
     */
    private static HashSet allowedMethods;
    
    /**
     * Will go through all the methods sent in
     * checking to see that the method is allowed.
     * If it's allowed it will be included
     * in the returned value.
     * 
     * @param allowSent The header returned by the server
     * @return The allowed headers for this request
     */
    public static String processAllowHeader(String allowSent) {
        StringBuffer allowToSend = new StringBuffer("");
        StringTokenizer tokenizer = new StringTokenizer(allowSent, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim().toUpperCase();
            if (allowedMethods.contains(token)) {
                allowToSend.append(token).append(",");
            }
        }

        return allowToSend.toString();
    }
    
    /**
     * Returns the allow methods for this proxy.
     * 
     * @return Allowed methods
     */ 
    public static String getAllowHeader() {
        return allowString;
    }
    
    /**
     * Will check if the specified method is allowed by
     * looking if it is included in the allowedMethods.
     * 
     * @param method The method that is checked
     * @return true if the method is allowed, false otherwise
     */
    public static boolean methodAllowed(String method) {
        return allowedMethods.contains(method.toUpperCase());
    }
    
    /**
     * Checks the method to see if it's allowed
     * 
     * @param method The method that is checked
     * @return true if the method is allowed, false otherwise
     * @see AllowedMethodHandler#methodAllowed(String)
     */
    public static boolean methodAllowed(HttpMethod method) {
        return methodAllowed(method.getName());
    }
    
    /**
     * Will set the allowed methods, both by setting the string
     * and also by adding all the methods to the set of allowed.
     * @param allowed The list of allowed methods, should be comma separated
     */
    public synchronized static void setAllowedMethods(String allowed) {
        allowedMethods = new HashSet();
        allowString = allowed;
        StringTokenizer tokenizer = new StringTokenizer(allowed, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim().toUpperCase();
            allowedMethods.add(token);
        }
    }

}
