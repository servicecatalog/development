/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2015 
 *******************************************************************************/

package org.oscm.saml.sp;

/**
 * Provides utility methods for encoding the SAML HTTP request.
 */
public class HtmlEncoder {

    /**
     * Returns the HTML encoded version of the specified string, ready to be
     * displayed in a HTML page.
     * 
     * @param s
     *            String to be HTML encoded
     * @return HTML the HTML encoded String
     */
    public static String htmlEncode(String s) {
        StringBuffer encodedString = new StringBuffer("");
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c == '<') {
                encodedString.append("&lt;");
            } else if (c == '>') {
                encodedString.append("&gt;");
            } else if (c == '\'') {
                encodedString.append("&apos;");
            } else if (c == '"') {
                encodedString.append("&quot;");
            } else if (c == '&') {
                encodedString.append("&amp;");
            } else {
                encodedString.append(c);
            }
        }
        return encodedString.toString();
    }

}
