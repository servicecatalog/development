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

package net.sf.j2ep;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.j2ep.model.ServerContainer;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The config parser uses Digester to parse the config file. A rule chain with
 * links to the servers will be constructed.
 * 
 * Based on the work by Yoav Shapira for the balancer webapp distributed with
 * Tomcat.
 * 
 * @author Anders Nyman, Yoav Shapira
 */
public class ConfigParser {

    /**
     * The resulting server chain.
     */
    private ServerChain serverChain;

    /**
     * A logging instance supplied by commons-logging.
     */
    private static Log log;

    /**
     * Standard constructor only specifying the input file. The constructor will
     * parse the config and build a corresponding rule chain with the server
     * mappings included.
     * 
     * @param data
     *            The config file containing the XML data structure.
     */
    public ConfigParser(File data) {
        log = LogFactory.getLog(ConfigParser.class);
        try {
            LinkedList serverContainer = createServerList(data);
            if (log.isDebugEnabled()) {
                debugServers(serverContainer); 
            }
            serverChain = new ServerChain(serverContainer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the parsed server chain.
     * 
     * @return The resulting ServerChain
     */
    public ServerChain getServerChain() {
        return serverChain;
    }

    /**
     * Creates the rules.
     * 
     * @return The rules all put into a rule chain
     */
    private LinkedList createServerList(File data) throws Exception {
        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);

        // Construct server list
        digester.addObjectCreate("config", LinkedList.class);

        // Create servers
        digester.addObjectCreate("config/server", null, "className");
        digester.addSetProperties("config/server");
        // Create rule
        digester.addObjectCreate("config/server/rule", null, "className");
        digester.addSetProperties("config/server/rule");
        digester.addSetNext("config/server/rule", "setRule");
        // Create composite rule
        digester.addObjectCreate("config/server/composite-rule", null,
                "className");
        digester.addSetProperties("config/server/composite-rule");
        digester.addObjectCreate("config/server/composite-rule/rule", null,
                "className");
        digester.addSetProperties("config/server/composite-rule/rule");
        digester.addSetNext("config/server/composite-rule/rule", "addRule");
        digester.addSetNext("config/server/composite-rule", "setRule");
        // Add server to list
        digester.addSetNext("config/server", "add");

        // Create cluster servers
        digester.addObjectCreate("config/cluster-server", null, "className");
        digester.addSetProperties("config/cluster-server");
        // Create the servers in this cluster
        digester.addCallMethod("config/cluster-server/server", "addServer", 2);
        digester.addCallParam("config/cluster-server/server", 0, "domainName");
        digester.addCallParam("config/cluster-server/server", 1, "path");
        // Create rule
        digester.addObjectCreate("config/cluster-server/rule", null,
                "className");
        digester.addSetProperties("config/cluster-server/rule");
        digester.addSetNext("config/cluster-server/rule", "setRule");
        // Create composite rule
        digester.addObjectCreate("config/cluster-server/composite-rule", null,
                "className");
        digester.addSetProperties("config/cluster-server/composite-rule");
        digester.addObjectCreate("config/cluster-server/composite-rule/rule",
                null, "className");
        digester.addSetProperties("config/cluster-server/composite-rule/rule");
        digester.addSetNext("config/cluster-server/composite-rule/rule",
                "addRule");
        digester.addSetNext("config/cluster-server/composite-rule", "setRule");

        // Add server to list
        digester.addSetNext("config/cluster-server", "add");

        return (LinkedList) digester.parse(data);
    }

    /**
     * Will iterate over the server and print out the mappings between servers
     * and rules.
     * 
     * @param servers The server to debug
     */
    private void debugServers(LinkedList servers) {
        Iterator itr = servers.iterator();
        
        while (itr.hasNext()) {
            ServerContainer container = (ServerContainer) itr.next();
            log.debug(container + " mapped to --> " + container.getRule());
        }
    }
}
