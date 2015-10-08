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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper that will make sure sessions are rewritten so
 * that the server can be derived from the session.
 *
 * @author Anders Nyman
 */
public class ClusterResponseWrapper extends HttpServletResponseWrapper {
    
    /** 
     * The id of the server we are adding to the session.
     */
    private String serverId;
    
    /** 
     * Logging element supplied by commons-logging.
     */
    private static Log log = LogFactory.getLog(ClusterResponseWrapper.class);
    
    /** 
     * Regex to find sessions in cookies.
     */
    private static Pattern sessionPattern = Pattern.compile("(JSESSIONID=|PHPSESSID=|ASPSESSIONID=|ASP.NET_SessionId=)([^;\\s\\.]+)", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);

    /**
     * Basic constructor, will set the id that we should add to add
     * the sessions to make sure that the session is tracked to a specific
     * server.
     * 
     * @param response The response we wrapp
     * @param serverId The id of the server
     */
    public ClusterResponseWrapper(HttpServletResponse response, String serverId) {
        super(response);
        this.serverId = "." + serverId;
    }
    
    /**
     * Checks for the set-cookie header. This header will have to be
     * rewritten (if it is marking a session)
     * 
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String name, String originalValue) {
        String value;
        if (name.equalsIgnoreCase("set-cookie")) {
            value = rewriteSetCookie(originalValue);
        } else {
            value = originalValue;
        }
        super.addHeader(name, value);
    }
    
    /**
     * Checks for the set-cookie header. This header will have to be
     * rewritten (if it is marking a session)
     * 
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String name, String originalValue) {
        String value;
        if (name.equalsIgnoreCase("set-cookie")) {
            value = rewriteSetCookie(originalValue);
        } else {
            value = originalValue;
        }
        super.setHeader(name, value);
    }
    
    /**
     * Rewrites the header Set-Cookie so that path and domain 
     * is correct.
     * 
     * @param value The original header
     * @return The rewritten header
     */
    private String rewriteSetCookie(String value) {
        Matcher matcher = sessionPattern.matcher(value);
        String rewritten = matcher.replaceAll("$1$2" + serverId);
        if (log.isDebugEnabled() && !rewritten.equals(value)) {
            log.debug("Session found and rewritten \"" + value + "\" >> " + rewritten);
        }
        return rewritten;
    }
}
