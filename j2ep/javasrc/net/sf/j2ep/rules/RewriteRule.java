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

package net.sf.j2ep.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A rule using regular expressions to rewrite the
 * URI. At first the expression will have to match the 
 * URI, after that the groups from the expression can
 * be used to rewrite the URI.
 *
 * @author Anders Nyman
 */
public class RewriteRule extends BaseRule {
    
    /** 
     * Pattern we match the URI on,
     */
    private Pattern matchPattern;
    
    /** 
     * The string we rewrite to.
     */
    private String rewriteTo;
    
    /** 
     * Pattern to match when we rewrite links found in HTML.
     */
    private Pattern revertPattern;
    
    /** 
     * The string we revert links to.
     */
    private String revertTo;
    
    /** 
     * Marks if we should rewrite incoming links.
     */
    private boolean isRewriting;
    
    /** 
     * Marks if we are rewriting outgoing links found in HTML.
     */
    private boolean isReverting;
    
    /** 
     * Logging element supplied by commons-logging.
     */
    private static Log log;
    
    /**
     * Basic constructor.
     */
    public RewriteRule() {
        isRewriting = false;
        log = LogFactory.getLog(RewriteRule.class);
    }

    /**
     * Will check if the URI matches the pattern we have set up.
     * 
     * @see net.sf.j2ep.model.Rule#matches(javax.servlet.http.HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        String uri = getURI(request);      
        Matcher matcher = matchPattern.matcher(uri);
        return matcher.matches();
    }
    
    /**
     * Will use the pattern and the rewriteTo string to
     * rewrite the URI before using it to connection to
     * the end server.
     * 
     * @see net.sf.j2ep.model.Rule#process(java.lang.String)
     */
    public String process(String uri) {
        String rewritten = uri;
        if (isRewriting) {
            Matcher matcher = matchPattern.matcher(uri);
            rewritten = matcher.replaceAll(rewriteTo);
            log.debug("Rewriting URI: " + uri + " >> " + rewritten); 
        }
        return rewritten;
    }
    
    /**
     * @see net.sf.j2ep.model.Rule#revert(java.lang.String)
     */
    public String revert(String uri) {
        String rewritten = uri;
        if (isReverting) {
            Matcher matcher = revertPattern.matcher(uri);
            rewritten = matcher.replaceAll(revertTo);
            log.debug("Reverting URI: " + uri + " >> " + rewritten); 
        }
        return rewritten;
    }
    
    
    /**
     * Sets the regex we will match incoming URIs on.
     * 
     * @param regex The regex
     */
    public void setFrom(String regex) {
        if (regex == null) {
            throw new IllegalArgumentException("From pattern cannot be null.");
        } else {
            matchPattern = Pattern.compile(regex);
        }
    }
    
    /**
     * Sets the string we will rewrite incoming URIs to.
     * 
     * @param to The string we rewrite to
     */
    public void setTo(String to) {
        if (to == null) {
            throw new IllegalArgumentException("To string cannot be null.");
        } else {
            rewriteTo = to;
            isRewriting = true;
        }
    }
    
    /**
     * Sets the regex we use to match outgoing links found.
     * 
     * @param regex The regex
     */
    public void setRevertFrom(String regex) {
        if (regex == null) {
            throw new IllegalArgumentException("Revert pattern cannot be null.");
        } else {
            revertPattern = Pattern.compile(regex);
        }
    }
    
    /**
     * Sets the string we rewrite outgoing links to.
     * 
     * @param to The string we rewrite to
     */
    public void setRevertTo(String to) {
        if (to == null) {
            throw new IllegalArgumentException("To string cannot be null.");
        } else {
            revertTo = to;
            isReverting = true;
        }
    }
    
    /**
     * Will build a URI but including the Query String. That means that it really
     * isn't a URI, but quite near.
     * 
     * @param httpRequest Request to get the URI and query string from
     * @return The URI for this request including the query string
     */
    private String getURI(HttpServletRequest httpRequest) {
        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI().substring(contextPath.length());
        if (httpRequest.getQueryString() != null) {
            uri += "?" + httpRequest.getQueryString();
        }
        return uri;
    }

}
