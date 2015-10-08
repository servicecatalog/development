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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * Handler for POST and PUT methods.
 * 
 * @author Anders Nyman
 */
public class EntityEnclosingRequestHandler extends RequestHandlerBase {

    /**
     * Will set the input stream and the Content-Type header to match this
     * request. Will also set the other headers send in the request.
     * 
     * @throws IOException
     *             An exception is throws when there is a problem getting the
     *             input stream
     * @see net.sf.j2ep.model.RequestHandler#process(javax.servlet.http.HttpServletRequest,
     *      java.lang.String)
     */
    public HttpMethod process(HttpServletRequest request, String url)
            throws IOException {

        final EntityEnclosingMethod method;

        if (request.getMethod().equalsIgnoreCase("POST")) {
            method = new PostMethod(url);
        } else if (request.getMethod().equalsIgnoreCase("PUT")) {
            method = new PutMethod(url);
        } else {
            throw new IOException("Unknown request method: "
                    + request.getMethod());
        }

        setHeaders(method, request);

        InputStreamRequestEntity stream;
        stream = new InputStreamRequestEntity(request.getInputStream());
        method.setRequestEntity(stream);
        method.setRequestHeader("Content-type", request.getContentType());

        return method;

    }

}
