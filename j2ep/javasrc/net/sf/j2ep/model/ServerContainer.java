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
 * A server container is a object can can hold a server. It 
 * is used to do initial processing of the data in a request
 * before a server is created. This can be used to make some
 * decisions on the server creating based on the request to
 * allow more advanced server.
 * 
 * Usually the server can implement both this interface and
 * the server interface since the usage is rather linked.
 *
 * @author Anders Nyman
 */
public interface ServerContainer {

    /**
     * Do any processing needed before this server can be used.
     * Specifically important for cluster servers that needs 
     * to choose which server to use for the current request.
     * 
     * @return The server that is finished to be used.
     */
    Server getServer(HttpServletRequest request);

    /**
     * Returns the mapped rule. This method must return the same
     * rule as a call to the underlying servers getRule().
     * 
     * @return The rule we are mapped to
     * @see Server#getRule()
     */
    Rule getRule();

    /**
     * Sets the rule that is mapped for this server. Will
     * be used when we rewrite links to know how a absolute 
     * path should be rewritten.
     * 
     * @param rule The rule
     */
    void setRule(Rule rule);

    /**
     * Finds a server with the full path specified by the 
     * location sent in.
     * 
     * @param link The start of a link that a server is mapped to
     * @return The server that can handle the link, null if no server is found
     */
    Server getServerMapped(String link);
}
