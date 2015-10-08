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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * A handler for all responses. Will set the headers, process any
 * output stream and do any method specific actions.
 *
 * @author Anders Nyman
 */
public interface ResponseHandler {
    
    /**
     * Will process the response to set headers and streams for
     * it. Each implementation of ResponseHandler can also add
     * it's one method specific actions to the process method.
     * 
     * @param response The response to process
     * @throws IOException An exception is thrown when there is a problem with writing the output
     */
    public void process(HttpServletResponse response) throws IOException;
    
    /**
     * Returns the HTTP status code we received from the server
     * 
     * @return The status code
     */
    public int getStatusCode();
    
    /**
     * Do any actions needed when we wont need the ResponseHandler any more.
     */
    public void close();

}
