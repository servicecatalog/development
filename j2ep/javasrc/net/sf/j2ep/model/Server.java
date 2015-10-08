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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A representation of the server. Its main use it to be able to open
 * a connection to the server sending back a executed method.
 * The server has to choose which type of method to invoke and should
 * support all method in the HTTP specification.
 *
 * @author Anders Nyman
 */
public interface Server {

    /**
     * Can do any handling needed of a request before
     * the HttpMethod is executed. Example of handling 
     * is to wrap the request. 
     * 
     * @param request The request we are receiving
     * @return Eventual modified HttpServletRequest
     */
    HttpServletRequest preExecute(HttpServletRequest request);
    
    /**
     * Can do handling of the response, if needed the
     * server can also return a new HttpServletResponse
     * if a wrapper of the response is needed.
     * 
     * @param response The response we are receiving
     * @return Eventual modified HttpServletResponse
     */
    HttpServletResponse postExecute(HttpServletResponse response);
    
    /**
     * Used to notify the server that there is a problem using the data this
     * server supplied to the user. Maybe the domainName isn't working or the
     * directory doesn't exist on the underlying server, in any case this method
     * can be used to let the server know about the problems.
     * 
     * The server should try to analyze the problem and if possible fix it so
     * that the next request to this server will succeed.
     * 
     * @param e The exception received when trying to use this servers data
     */
    void setConnectionExceptionRecieved(Exception e);
    
    /**
     * Returns the host name and port for this server.
     * @return Host name and port
     */
    String getDomainName();
    
    /**
     * Returns the path that we are mapping to. Starting from the 
     * servers root the path starts with a / but doesn't end with 
     * a /. A mapping to the root results in
     * path being an empty string "".
     * 
     * @return The path
     */
    String getPath();

    
    /**
     * Returns the mapped rule so we can rewrite links.
     * 
     * @return The rule we are mapped to
     */
    Rule getRule();
}
