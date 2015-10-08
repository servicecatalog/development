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

import net.sf.j2ep.model.AllowedMethodHandler;
import net.sf.j2ep.model.ResponseHandler;
import net.sf.j2ep.responsehandlers.DeleteResponseHandler;
import net.sf.j2ep.responsehandlers.GetResponseHandler;
import net.sf.j2ep.responsehandlers.HeadResponseHandler;
import net.sf.j2ep.responsehandlers.OptionsResponseHandler;
import net.sf.j2ep.responsehandlers.PostResponseHandler;
import net.sf.j2ep.responsehandlers.PutResponseHandler;
import net.sf.j2ep.responsehandlers.TraceResponseHandler;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;

/**
 * A factory creating ResponseHandlers.
 * This factory is used to get the handler for each request, it has
 * a list of methods it can handle and will throw a MethodNotAllowedException
 * when it can't handle a method.
 *
 * @author Anders Nyman
 */
public class ResponseHandlerFactory {
    
    /** 
     * These methods are handled by this factory.
     */
    private static final String handledMethods = "OPTIONS,GET,HEAD,POST,PUT,DELETE,TRACE";

    /**
     * Checks the method being received and created a 
     * suitable ResponseHandler for this method.
     * 
     * @param method Method to handle
     * @return The handler for this response
     * @throws MethodNotAllowedException If no method could be choose this exception is thrown
     */
    public static ResponseHandler createResponseHandler(HttpMethod method) throws MethodNotAllowedException {
        if (!AllowedMethodHandler.methodAllowed(method)) {
            throw new MethodNotAllowedException("The method " + method.getName() + " is not in the AllowedHeaderHandler's list of allowed methods.", AllowedMethodHandler.getAllowHeader());
        }
        
        ResponseHandler handler = null;
        if (method.getName().equals("OPTIONS")) {
            handler = new OptionsResponseHandler((OptionsMethod) method);
        } else if (method.getName().equals("GET")) {
            handler = new GetResponseHandler((GetMethod) method);
        } else if (method.getName().equals("HEAD")) {
            handler = new HeadResponseHandler((HeadMethod) method);
        } else if (method.getName().equals("POST")) {
            handler = new PostResponseHandler((PostMethod) method);
        } else if (method.getName().equals("PUT")) {
            handler = new PutResponseHandler((PutMethod) method);
        } else if (method.getName().equals("DELETE")) {
            handler = new DeleteResponseHandler((DeleteMethod) method);
        } else if (method.getName().equals("TRACE")) {
            handler = new TraceResponseHandler((TraceMethod) method);
        } else {
            throw new MethodNotAllowedException("The method " + method.getName() + " was allowed by the AllowedMethodHandler, not by the factory.", handledMethods);
        }

        return handler;
    }



}
