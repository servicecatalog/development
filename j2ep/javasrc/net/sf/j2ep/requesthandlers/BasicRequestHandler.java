/*
 * Copyright 2000,2004 The Apache Software Foundation.
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

package net.sf.j2ep.requesthandlers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

/**
 * A handler for GET, HEAD, DELETE. Since these methods basically
 * only will need the headers set they can be handled by the same 
 * handler.
 *
 * @author Anders Nyman
 */
public class BasicRequestHandler extends RequestHandlerBase {

    /**
     * Will only set the headers.
     * @throws HttpException 
     * 
     * @see net.sf.j2ep.model.RequestHandler#process(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public HttpMethod process(HttpServletRequest request, String url) throws HttpException {
        
        HttpMethodBase method = null;
      
        if (request.getMethod().equalsIgnoreCase("GET")) {
            method = new GetMethod(url);
        } else if (request.getMethod().equalsIgnoreCase("HEAD")) {
            method = new HeadMethod(url);
        } else if (request.getMethod().equalsIgnoreCase("DELETE")) {
            method = new DeleteMethod(url);
        } else {
            return null;
        }
        
        setHeaders(method, request);
        return method;
    }
      

}
