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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.TraceMethod;


/**
 * Handler for the OPTIONS and TRACE method.
 *
 * @author Anders Nyman
 */
public class MaxForwardRequestHandler extends RequestHandlerBase {

    /**
     * Sets the headers and does some checking for if this request
     * is meant for the server or for the proxy. This check is done
     * by looking at the Max-Forwards header.
     * 
     * @see net.sf.j2ep.model.RequestHandler#process(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public HttpMethod process(HttpServletRequest request, String url) throws IOException {
        HttpMethodBase method = null;
        
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            method = new OptionsMethod(url);
        } else if (request.getMethod().equalsIgnoreCase("TRACE")) {
            method = new TraceMethod(url);
        } else {
            return null;
        }
        
        try {
            int max = request.getIntHeader("Max-Forwards");
            if (max == 0 || request.getRequestURI().equals("*")) {
                setAllHeaders(method, request);
                method.abort();
            } else if (max != -1) {
                setHeaders(method, request);
                method.setRequestHeader("Max-Forwards", "" + max--);
            } else {
                setHeaders(method, request);
            }
        } catch (NumberFormatException e) {}
        
        return method;
    }
    
    /**
     * Will write all the headers included in the request to the method.
     * The difference between this method and setHeaders in BasicRequestHandler
     * is that the BasicRequestHandler will also add Via, x-forwarded-for, etc.
     * These "special" headers should not be added when the proxy is target
     * directly with a Max-Forwards: 0 headers.
     * @param method The method to write to
     * @param request The incoming request
     * @see RequestHandlerBase#setHeaders(HttpMethod, HttpServletRequest)
     */
    private void setAllHeaders(HttpMethod method, HttpServletRequest request) {
        Enumeration headers = request.getHeaderNames();
        
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            Enumeration value = request.getHeaders(name);
            
            while (value.hasMoreElements()) {
                method.addRequestHeader(name, (String) value.nextElement());
            }

        } 
    }
}
