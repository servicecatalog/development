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

import javax.servlet.http.HttpServletResponse;

import net.sf.j2ep.model.AllowedMethodHandler;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handler for the OPTIONS method.
 * Will process the Allow header so that
 * no methods that the backing server can handle
 * but we can't are being sent to the client.
 *
 * @author Anders Nyman
 */
public class OptionsResponseHandler extends ResponseHandlerBase {

    /** 
     * The logger.
     */
    private static Log log = LogFactory.getLog(OptionsResponseHandler.class);
    
    /** 
     * Set a construction to indicate if the request is directed to the
     * proxy directly by using Max-Forwards: 0 or using URI *.
     */
    private boolean useOwnAllow;

    /**
     * Constructor checking if we should handle the Allow header
     * ourself or respond with the backing servers header.
     * 
     * @param method The method for this response
     */
    public OptionsResponseHandler(OptionsMethod method) {
        super(method);
        useOwnAllow = !method.hasBeenUsed();
    }

    /**
     * Will check if we are to handle this request, if so 
     * the http methods allowed by this proxy is returned in the 
     * Allow header.
     * If it is a request meant for the backing server its
     * allowed method will be returned.
     * 
     * @see net.sf.j2ep.model.ResponseHandler#process(javax.servlet.http.HttpServletResponse)
     */
    public void process(HttpServletResponse response) {
        if (useOwnAllow) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("allow", AllowedMethodHandler.getAllowHeader());
            response.setHeader("Connection", "close");
            response.setHeader("content-length", "0");
        } else {
            setHeaders(response);
            response.setStatus(getStatusCode());
            String allow = method.getResponseHeader("allow").getValue();
            response.setHeader("allow", AllowedMethodHandler.processAllowHeader(allow));
            Header contentLength = method.getResponseHeader("Content-Length");
            if (contentLength == null || contentLength.getValue().equals("0")) {
                response.setHeader("Content-Length", "0");
            } else {
                try {
                    sendStreamToClient(response);
                } catch (IOException e) {
                    log.error("Problem with writing response stream, solving by setting Content-Length=0", e);
                    response.setHeader("Content-Length", "0");
                }
            }
        }
    }

    /**
     * Returns 200 if the request is targeted to the proxy
     * otherwise the normal status code is returned.
     * 
     * @see net.sf.j2ep.model.ResponseHandler#getStatusCode()
     */
    public int getStatusCode() {
        if (useOwnAllow) {
            return 200;
        } else {
            return super.getStatusCode();
        }
    }

}
