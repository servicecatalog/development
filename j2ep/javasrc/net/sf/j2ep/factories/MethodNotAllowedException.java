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

package net.sf.j2ep.factories;

/**
 * An exception thrown when a factory can't handle the incoming method.
 *
 * @author Anders Nyman
 */
public class MethodNotAllowedException extends Exception {

    /** 
     * Our id
     */
    private static final long serialVersionUID = 4149736397823198286L;
   
    /** 
     * List of methods that are being allowed by the factory.
     */
    private String allowedMethods;
    
    /**
     * Basic constructor creating a exception.
     * @param message The exception message
     * @param allowedMethods The allowed methods
     */
    public MethodNotAllowedException(String message, String allowedMethods) {
        super(message);
        this.allowedMethods = allowedMethods;
    }
    
    /**
     * Returns which methods that are allowed by the instance throwing
     * this exception.
     * 
     * @return The allowed methods
     */
    public String getAllowedMethods() {
        return allowedMethods;
    }

}
