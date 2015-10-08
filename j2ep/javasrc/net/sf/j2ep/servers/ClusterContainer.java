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

package net.sf.j2ep.servers;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.j2ep.model.Rule;
import net.sf.j2ep.model.Server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A ServerContainer implementation that have multiple domains to choose from.
 * When a request is received one server is chosen to handle the request. If the
 * request is linked to a session this server will make sure that it's the
 * domain that created the session that will process this request.
 * 
 * @author Anders Nyman
 */
public abstract class ClusterContainer extends ServerContainerBase implements ServerStatusListener {
    
    /** 
     * Logging element supplied by commons-logging.
     */
    private static Log log;
    
    /** 
     * The servers in our cluster,
     */
    protected HashMap servers;
    
    /** 
     * Class that will check if our servers are online or offline.
     */
    private ServerStatusChecker statusChecker;
    
    /**
     * Basic constructor
     */
    public ClusterContainer() {
        servers = new HashMap();
        statusChecker = new ServerStatusChecker(this, 5*60*1000);
        statusChecker.start();
        log = LogFactory.getLog(ClusterContainer.class);
    }
    
    /**
     * Will create a new server based on the domainName and the directory.
     * @param domainName The domain
     * @param directory The directory
     * @return The created server
     */
    protected abstract ClusteredServer createNewServer(String domainName, String directory);
    
    /**
     * Returns the next server in out cluster.
     * Is used when we can't get a server from the requests session.
     * @return The next server
     */
    protected abstract ClusteredServer getNextServer();
    
    /**
     * Checks the request for any session. If there is a session created we
     * make sure that the server returned is the one the issued the session.
     * If no session is included in the request we will choose the next server
     * in a round-robin fashion.
     * 
     * @see net.sf.j2ep.model.ServerContainer#getServer(javax.servlet.http.HttpServletRequest)
     */
    public Server getServer(HttpServletRequest request) {
        String serverId = getServerIdFromCookie(request.getCookies());
        ClusteredServer server = (ClusteredServer) servers.get(serverId);
        if (server == null || !server.online()) {
            server = getNextServer();
        } else {
            log.debug("Server found in session");
        }
        
        if (server.online()) {
            log.debug("Using id " + server.getServerId() + " for this request"); 
        } else {
            log.error("All the servers in this cluster are offline. Using id " + server.getServerId() + ", will probably not work");
        }
        return server;
    }

    /**
     * Locates any specification of which server that issued a
     * session. If there is no session or the session isn't mapped
     * to a specific server null is returned.
     * 
     * @param cookies The cookies so look for a session in
     * @return the server's ID or null if no server is found
     */
    private String getServerIdFromCookie(Cookie[] cookies) {
        String serverId = null;
        if (cookies != null) {
            for (int i=0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if ( isSessionCookie(cookie.getName()) ) {
                    String value = cookie.getValue();
                    String id = value.substring(value.indexOf(".")+1);
                    if (id.startsWith("server")) {
                        serverId = id;
                    }
                }
            } 
        }
        return serverId;
    }
    
    /**
     * Checks if the supplied name of a cookie is known to be a 
     * session.
     * 
     * @param name The cookies name
     * @return true if this cookie is specifying a session
     */
    private boolean isSessionCookie(String name) {
        return name.equalsIgnoreCase("JSESSIONID")
                || name.equalsIgnoreCase("PHPSESSID")
                || name.equalsIgnoreCase("ASPSESSIONID")
                || name.equalsIgnoreCase("ASP.NET_SessionId");
    }
    
    /**
     * @see net.sf.j2ep.model.ServerContainer#getServerMapped(java.lang.String)
     */
    public Server getServerMapped(String location) {
        Iterator itr = servers.values().iterator();
        Server match = null;

        while (itr.hasNext() && match == null) {
            Server server = (Server) itr.next();
            String fullPath = server.getDomainName() + server.getPath() + "/";
            if (location.startsWith(fullPath)) {
                match = server;
            }
        }
        return match;
    }
    
    /**
     * Sets the server to offline status.
     * Will only handle servers that are ClusteredServers
     * @see net.sf.j2ep.servers.ServerStatusListener#serverOffline(net.sf.j2ep.model.Server)
     */
    public void serverOffline(Server server) {
        if (server instanceof ClusteredServer) {
            ((ClusteredServer) server).setOnline(false);
        }
    }
    
    /**
     * Sets the server to online status.
     * Will only handle servers that are ClusteredServers
     * @see net.sf.j2ep.servers.ServerStatusListener#serverOnline(net.sf.j2ep.model.Server)
     */
    public void serverOnline(Server server) {
        if (server instanceof ClusteredServer) {
            ((ClusteredServer) server).setOnline(true);
        }
    }
    
    /**
     * Will create a new ClusteredServer and add it to the hash map.
     * 
     * @param domainName The domain name for the new server
     * @param directory The director for the new server.
     */
    public synchronized void addServer(String domainName, String directory) {
        if (domainName == null) {
            throw new IllegalArgumentException("The domainName cannot be null");
        }
        if (directory == null) {
            directory = "";
        }
        
        ClusteredServer server = createNewServer(domainName, directory);
        servers.put(server.getServerId(), server);
        statusChecker.addServer(server);
        log.debug("Added server " + domainName + directory + " to the cluster on id " + server.getServerId());
    }
    
    /**
     * A server in the cluster. Will have access to the encapsulating Cluster
     * so that we can use its methods to get the rule and such.
     *
     * @author Anders Nyman
     */
    protected class ClusteredServer implements Server {
        
        /** 
         * The domain name mapping
         */
        private String domainName;
        
        /** 
         * The path mapping
         */
        private String path;
        
        /** 
         * This servers id
         */
        private String serverId;
        
        /** 
         * The status of this server
         */
        private boolean online;
        
        /**
         * Basic constructor that sets the domain name and directory.
         * 
         * @param domainName The domain name
         * @param path The directory
         */
        public ClusteredServer(String domainName, String path, String serverId) {
            this.domainName = domainName;
            this.path = path;
            this.serverId = serverId;
            this.online = true;
        }

        /**
         * Will wrap the request so the tailing .something,
         * identifying the server, is removed from the request.
         * 
         * @see net.sf.j2ep.model.Server#preExecute(javax.servlet.http.HttpServletRequest)
         */
        public HttpServletRequest preExecute(HttpServletRequest request) {
            return new ClusterRequestWrapper(request);
        }
        
        /**
         * Will wrap the response so that sessions are rewritten to
         * remove the tailing .something that indicated which server
         * the session is linked to.
         * @see net.sf.j2ep.model.Server#postExecute(javax.servlet.http.HttpServletResponse)
         */
        public HttpServletResponse postExecute(HttpServletResponse response) {
            return new ClusterResponseWrapper(response, serverId);
        }
        
        /**
         * Notifies the server status checker that a server
         * might have gone offline.
         * @see net.sf.j2ep.model.Server#setConnectionExceptionRecieved(java.lang.Exception)
         */
        public void setConnectionExceptionRecieved(Exception e) {
            ClusterContainer.this.statusChecker.interrupt();
        }

        /**
         * @see net.sf.j2ep.model.Server#getDomainName()
         */
        public String getDomainName() {
            return domainName;
        }

        /**
         * @see net.sf.j2ep.model.Server#getPath()
         */
        public String getPath() {
            return path;
        }
        
        /**
         * Returns the online status of this server
         * @return true if the server is online, otherwise false
         */
        public boolean online() {
            return online;
        }
        
        /**
         * Marks if this server should be considered online or
         * offline.
         * @param online The status of the server
         */
        public void setOnline(boolean online) {
            this.online = online;
        }

        /**
         * @see net.sf.j2ep.model.Server#getRule()
         */
        public Rule getRule() {
            return ClusterContainer.this.getRule();
        }
        
        /**
         * Returns this servers ID.
         * @return The server ID
         */
        public String getServerId() {
            return serverId;
        }
    }
}
