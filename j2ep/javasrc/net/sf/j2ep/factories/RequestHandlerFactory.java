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

import java.util.HashMap;

import net.sf.j2ep.model.AllowedMethodHandler;
import net.sf.j2ep.model.RequestHandler;
import net.sf.j2ep.requesthandlers.BasicRequestHandler;
import net.sf.j2ep.requesthandlers.EntityEnclosingRequestHandler;
import net.sf.j2ep.requesthandlers.MaxForwardRequestHandler;
import net.sf.j2ep.requesthandlers.RequestHandlerBase;

/**
 * A factory creating RequestHandlers.
 * This factory is used to get the handler for each request, it has
 * a list of methods it can handle and will throw a MethodNotAllowedException
 * when it can't handle a method.
 *
 * @author Anders Nyman
 */
public class RequestHandlerFactory {
    
    /** 
     * The RequestHandlers to return.
     */
    private static HashMap requestHandlers;
    
    /** 
     * These methods are handled by this factory.
     */
    private static final String handledMethods = "OPTIONS,GET,HEAD,POST,PUT,DELETE,TRACE";
    
    /** 
     * List of banned headers that should not be set.
     */
    private static final String bannedHeaders = "connection,accept-encoding,via,x-forwarded-for,x-forwarded-host,x-forwarded-server";
    
    static {
        RequestHandlerBase.addBannedHeaders(bannedHeaders);
        
        requestHandlers = new HashMap();
        MaxForwardRequestHandler optionsAndTrace = new MaxForwardRequestHandler();
        BasicRequestHandler basic = new BasicRequestHandler();
        EntityEnclosingRequestHandler entityEnclosing = new EntityEnclosingRequestHandler();
        
        requestHandlers.put("OPTIONS", optionsAndTrace);
        requestHandlers.put("GET", basic);
        requestHandlers.put("HEAD", basic);
        requestHandlers.put("POST", entityEnclosing);
        requestHandlers.put("PUT", entityEnclosing);
        requestHandlers.put("DELETE", basic);
        requestHandlers.put("TRACE", optionsAndTrace);
    }
    
    /**
     * Selects one suitable RequestMethod from the HashMap.
     * 
     * @param method The method of this request
     * @return A RequestHandler that can handle this request
     * @throws MethodNotAllowedException If there is no RequestHandler available an exception will be thrown
     */
    public static RequestHandler createRequestMethod(String method) throws MethodNotAllowedException{
        if (!AllowedMethodHandler.methodAllowed(method)) {
            throw new MethodNotAllowedException("The method " + method + " is not in the AllowedHeaderHandler's list of allowed methods.", AllowedMethodHandler.getAllowHeader());
        }
        
        RequestHandler handler = (RequestHandler) requestHandlers.get(method.toUpperCase());
        if (handler == null) {
            throw new MethodNotAllowedException("The method " + method + " was allowed by the AllowedMethodHandler, not by the factory.", handledMethods);
        } else {
            return handler;
        }
    }
}
