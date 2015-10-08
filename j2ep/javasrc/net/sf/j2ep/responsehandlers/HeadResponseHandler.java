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

package net.sf.j2ep.responsehandlers;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.methods.HeadMethod;

/**
 * Handler for the HEAD method.
 *
 * @author Anders Nyman
 */
public class HeadResponseHandler extends ResponseHandlerBase {
    
    /**
     * Default constructor, will only call the super-constructor
     * for BasicResponseHandler. 
     * 
     * @param method The method used for this response
     */
    public HeadResponseHandler(HeadMethod method) {
        super(method);
    }

    /**
     * Will only set the headers and status code, no response is sent.
     * 
     * @see net.sf.j2ep.model.ResponseHandler#process(javax.servlet.http.HttpServletResponse)
     */
    public void process(HttpServletResponse response) {
        setHeaders(response);
        response.setStatus(getStatusCode());
    }

}
