/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                
 *                                                                              
 *  Creation Date: 19.03.2012                                                      
 *                                                                                                                        
 *                                                                              
 *******************************************************************************/

package org.oscm.example.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility class for network lookups.
 * 
 * @author cheld
 * 
 */
public class InetLookup {

    private static volatile CachedHostname lastLookup;

    private static class CachedHostname {
        private String usedAddress;
        private String resolvedHost;
    }

    /**
     * Returns the hostname for the given address, or the given address if no
     * hostname is found. The lookup is cached, because they take a long time in
     * our network (at least in some cases one lookup required up to 20s)
     * 
     * @param address
     *            - IP to be used.
     * @return String
     */
    public static String resolveHost(String address) {
        if (lastLookup != null && address.equals(lastLookup.usedAddress)) {
            return lastLookup.resolvedHost;
        }
        CachedHostname lookup = new CachedHostname();
        lookup.usedAddress = address;
        try {
            InetAddress addr = InetAddress.getByName(address);
            lookup.resolvedHost = addr.getHostName();
        } catch (UnknownHostException e) {
            // try to continue
            lookup.resolvedHost = address;
            e.printStackTrace();
        }
        lastLookup = lookup;
        return lastLookup.resolvedHost;
    }

}
