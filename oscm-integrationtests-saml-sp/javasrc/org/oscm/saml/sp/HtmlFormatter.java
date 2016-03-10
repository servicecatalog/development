/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.sp;

/**
 * Provides utility methods for formating strings for being displayed in HTML
 * pages.
 * 
 * @author barzu
 */
public class HtmlFormatter {

    public static String format(String s) {
        if (s == null) {
            return null;
        }
        s = s.replaceAll(" ", "&nbsp;");
        return s.replaceAll("\\n", "<br>");
    }

}
