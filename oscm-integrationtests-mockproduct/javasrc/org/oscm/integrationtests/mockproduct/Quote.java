/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

/**
 * Interal utility to quote string to valid HTML data.
 * 
 * @author hoffmann
 */
public class Quote {

    public static final String html(final String s) {
        if (s == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        for (final char c : s.toCharArray()) {
            switch (c) {
            case '"':
                result.append("&quot;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            default:
                result.append(c);
                break;
            }
        }
        return result.toString();
    }

}
