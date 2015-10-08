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

import java.util.EventListener;

import net.sf.j2ep.model.Server;

/**
 * A listener for status messages for the servers.
 * Classes implementing this interface can recieve
 * information when a server goes down and when it comes
 * back online.
 *
 * @author Anders Nyman
 */
public interface ServerStatusListener extends EventListener {
    
    /**
     * Notifies that a servers has gone down
     * 
     * @param server Server that is now offline
     */
    public void serverOffline(Server server);
    
    /**
     * Notifies that a server is online. This
     * means that the server previously has been marked
     * as being offline.
     * 
     * @param server Server that is now online
     */
    public void serverOnline(Server server);

}
