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

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * This rule will check the IP for the remote user
 * allowing the user if his IP is in the specified range.
 *
 * @author Anders Nyman
 */
public class IPRule extends BaseRule {
    
    /** 
     * The starting IP range.
     */
    private String startRange;
    
    /** 
     * The ending IP range.
     */
    private String endRange;

    /**
     * Checks the IP for the remote user, if it's in the specified
     * range it's a match.
     * 
     * @see net.sf.j2ep.model.Rule#matches(javax.servlet.http.HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        boolean match = false;
        if (getStartRange() != null && getEndRange() != null) {
            String ip = request.getRemoteAddr();
            match = (ip.compareTo(getStartRange()) >= 0 && ip
                    .compareTo(getEndRange()) <= 0);
        }
        return match;
    }

    /**
     * Checks to see is a specified IP range is OK. OK means
     * that it's in the range 0.0.0.0 to 255.255.255.255 and
     * that the value is a correct IP address.
     * 
     * @param range The value to check
     * @return Will give true if the range is OK
     */
    private boolean validRange(String range) {
        StringTokenizer tokenizer = new StringTokenizer(range, ".");
        boolean correct = true;

        if (tokenizer.countTokens() != 4) {
            correct = false;
        }
        try {
            while (tokenizer.hasMoreTokens() && correct) {
                int tokenValue = Integer.parseInt(tokenizer.nextToken());
                correct = (tokenValue >= 0 && tokenValue <= 255);
            }
        } catch (NumberFormatException e) {
            correct = false;
        }
        
        return correct;
    }

    /**
     * Sets the beginning range that a connection has to be made from to be
     * triggered by this rule.
     * 
     * @param startRange The start of the IP range
     */
    public void setStartRange(String startRange) {
        if(startRange == null) {
            throw new IllegalArgumentException("The startRange cannot be null.");
        } else if(!validRange(startRange)) {
            throw new IllegalArgumentException("IP range has to be between \"0.0.0.0\" and \"255.255.255.255\".");
        } else if(getEndRange() != null && startRange.compareTo(getEndRange()) > 0) {
            throw new IllegalArgumentException("Starting range has to come before the ending range.");
        }
        this.startRange = startRange;
    }

    /**
     * Returns the beginning range that a connection has to be made from
     * to be triggered by this rule.
     * 
     * @return The start of the IP range
     */
    protected String getStartRange() {
        return startRange;
    }

    /**
     * Sets the ending range that a connection has to be made from
     * to be triggered by this rule.
     * 
     * @param endRange The end of the IP range
     */
    public void setEndRange(String endRange) {
        if(endRange == null) {
            throw new IllegalArgumentException("The endRange cannot be null.");
        } else if(!validRange(endRange)) {
            throw new IllegalArgumentException("IP range has to be between \"0.0.0.0\" and \"255.255.255.255\".");
        } else if(getStartRange() != null && endRange.compareTo(getStartRange()) < 0) {
            throw new IllegalArgumentException("Ending range has to come after the starting range.");
            
        }
        this.endRange = endRange;
    }

    /**
     * Returns the ending range that a connection has to be made from
     * to be triggered by this rule.
     * 
     * @return The end of the IP range
     */
    protected String getEndRange() {
        return endRange;
    }

}
