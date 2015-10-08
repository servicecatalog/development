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


/**
 * A cluster using round-robin to get the next server in the
 * cluster.
 *
 * @author Anders Nyman
 */
public class RoundRobinCluster extends ClusterContainer {

    /**
     * The current number of servers, only used at when the servers are added to
     * the hash map. It is assumed that this variable is only modified in a
     * single threaded environment.
     */
    private int numberOfServers;
    
    /**
     * The currentServer we are using.
     */
    private int currentServerNumber;
    
    /**
     * Creates a new round-robin cluster
     */
    public RoundRobinCluster() {
        super();
        currentServerNumber = 0;
        numberOfServers = 0;
    }
    
    /**
     * Returns the next in the cluster. The server if found
     * using round-robin and checking that the server is marked
     * as online.
     *  
     * @return The next server
     */
    protected ClusteredServer getNextServer() {
        ClusteredServer server;
        int start = currentServerNumber;
        int current = start;
        do {
            current = (current + 1) % numberOfServers;
            server = (ClusteredServer) servers.get("server" + current); 
        } while (!server.online() && start != current);
        
        currentServerNumber = current;
        return server;
    }
    
    /**
     * @see net.sf.j2ep.servers.ClusterContainer#createNewServer(java.lang.String, java.lang.String)
     */
    protected ClusteredServer createNewServer(String domainName, String directory) {
        String id = "server" + numberOfServers;
        numberOfServers++;
        ClusteredServer server = new ClusteredServer(domainName, directory, id);
        return server;
    }
}
