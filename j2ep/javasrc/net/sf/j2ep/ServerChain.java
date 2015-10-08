/*
 * Copyright 2000,2004 Anders Nyman.
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

package net.sf.j2ep;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.j2ep.model.Rule;
import net.sf.j2ep.model.Server;
import net.sf.j2ep.model.ServerContainer;

/**
 * A ServerChain is a list of server considered in order. The first server with
 * a rule that successfully matches tops the evaluation of servers
 * 
 * This is only a slightly modified version of the RuleChain used with the
 * balancer webapp shipped with tomcat.
 * 
 * @author Anders Nyman, Yoav Shapira
 */
public class ServerChain {

    /**
     * The list of servers to evaluate.
     */
    private List serverContainers;

    /**
     * Constructor.
     */
    public ServerChain(List serverContainers) {
        this.serverContainers = serverContainers;
    }

    /**
     * Returns the list of servers to evaluate.
     * 
     * @return The servers
     */
    protected List getServers() {
        return serverContainers;
    }

    /**
     * Returns an iterator over the list of servers to evaluate.
     * 
     * @return The iterator
     */
    protected Iterator getServerIterator() {
        return getServers().iterator();
    }

    /**
     * Adds a server to evaluate.
     * 
     * @param theServer
     *            The server to add
     */
    public void addServer(Server theServer) {
        if (theServer == null) {
            throw new IllegalArgumentException("The rule cannot be null.");
        } else {
            getServers().add(theServer);
        }
    }

    /**
     * Evaluates the given request to see if any of the rules matches. Returns
     * the the server linked to the first matching rule.
     * 
     * @param request
     *            The request
     * @return The first matching server, null if no rule matched the request
     * @see Rule#matches(HttpServletRequest)
     */
    public Server evaluate(HttpServletRequest request) {
        Iterator itr = getServerIterator();
        while (itr.hasNext()) {
            final ServerContainer container = (ServerContainer) itr.next();
            if (container.getRule().matches(request)) {
                return container.getServer(request);
            }
        }
        return null;
    }

    /**
     * Finds a server with the full path specified by the location sent in. This
     * is used when we want to find a server that can handle a request.
     * 
     * @param location
     *            The location we want a server for.
     * @return The matching server, if no server is found null is returned
     * @see ServerContainer#getServerMapped(String)
     */
    public Server getServerMapped(String location) {
        Iterator itr = getServerIterator();
        Server match = null;

        while (itr.hasNext() && match == null) {
            ServerContainer container = (ServerContainer) itr.next();
            Server next = container.getServerMapped(location);
            if (next != null) {
                match = next;
            }
        }
        return match;
    }

    /**
     * Returns a String representation of this object.
     * 
     * @return A string representation
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        buffer.append(getClass().getName());
        buffer.append(": ");

        Iterator iter = getServerIterator();
        Server currentServer = null;

        while (iter.hasNext()) {
            currentServer = (Server) iter.next();
            buffer.append(currentServer);

            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append("]");

        return buffer.toString();
    }
}
