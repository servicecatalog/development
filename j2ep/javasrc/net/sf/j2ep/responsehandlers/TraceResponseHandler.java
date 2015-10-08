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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.TraceMethod;

/**
 * A handler for the TRACE method. This handler will make
 * a check if the trace is directed to the proxy or a underlying 
 * server. If the trace directed to the proxy the response will 
 * be made in compliance to HTTP 1.1 RFC.
 *
 * @author Anders Nyman
 */
public class TraceResponseHandler extends ResponseHandlerBase {

    /** 
     * Set a construction to indicate if the request is directed to the
     * proxy directly by using Max-Forwards: 0 or using URI *.
     */
    private boolean proxyTargeted;
    
    /**
     * Basic constructor setting the method, and also checks if
     * the request is targeted to the proxy or the underlying server.
     * @param method The http method
     */
    public TraceResponseHandler(TraceMethod method) {
        super(method);
        proxyTargeted = !method.hasBeenUsed();
    }

    /**
     * Will either respond with data from the underlying server
     * or the proxy's own data.
     * @see net.sf.j2ep.model.ResponseHandler#process(javax.servlet.http.HttpServletResponse)
     */
    public void process(HttpServletResponse response) throws IOException {
        
        if (proxyTargeted) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("content-type", "message/http");
            response.setHeader("Connection", "close");
            
            String path = method.getPath();
            String protocol = method.getParams().getVersion().toString();
            PrintWriter writer = response.getWriter();
            writer.println("TRACE " + path + " " + protocol);
            Header[] headers = method.getRequestHeaders();
            for (int i=0; i < headers.length; i++) {
                writer.print(headers[i]);
            }
            writer.flush();
            writer.close();
            
        } else {
            setHeaders(response);
            response.setStatus(getStatusCode());
            sendStreamToClient(response);
        }
    }
    
    /**
     * Returns 200 if the request is targeted to the proxy
     * otherwise the normal status code is returned.
     * 
     * @see net.sf.j2ep.model.ResponseHandler#getStatusCode()
     */
    public int getStatusCode() {
        if (proxyTargeted) {
            return 200;
        } else {
            return super.getStatusCode();
        }
    }

}
