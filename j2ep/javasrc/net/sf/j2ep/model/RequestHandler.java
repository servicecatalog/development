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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;

/**
 * A handler for all requests. Will set the headers, process any
 * input stream and do any method specific actions.
 *
 * @author Anders Nyman
 */
public interface RequestHandler {
    
    /**
     * Creates a new HttpMethod for this request. Will then
     * set the headers and any other information needed for this
     * request.
     * 
     * @param request The request we are processing
     * @param url URL to bind the method to
     * @return The method we have created
     * @throws IOException An exception is thrown when there is a problem with the input supplied by the request
     */
    public HttpMethod process(HttpServletRequest request, String url) throws IOException;

}
