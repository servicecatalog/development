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

import javax.servlet.http.HttpServletRequest;


/**
 * The Rule interface is implemented by
 * the rules for the reverse proxy.
 * Based on the work by Yoav Shapira 
 * for the balancer webapp.
 *
 * @author Anders Nyman
 */
public interface Rule {
    
    /**
     * Determines if the given request
     * matches the rule.
     *
     * @param request The request
     * @return true if the request is matched, otherwise false
     */
    boolean matches(HttpServletRequest request);

    /**
     * Returns the redirect URI for
     * requests that match this rule.
     * Process is used in order to let the rules
     * do rewriting of the URI before being handled
     * by the server.
     * 
     * The rule will not any check that a URI is matched
     * before processing it. If you want this type of 
     * control make make sure the calling class checks that 
     * there is a match before processing.
     * 
     * @param uri URI to be processed
     * @return The final URI
     */
    String process(String uri);
    
    /**
     * Returns the reverted URI, this means
     * that if a URI is processed and then reverted
     * it should be the same URI.
     * revert(process("some random string")) should
     * return "some random string".
     * 
     * @param uri URI to be reverted
     * @return The reverted URI
     */
    String revert(String uri);
}
