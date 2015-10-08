/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2015 
 *******************************************************************************/

package org.oscm.saml.api;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import org.oscm.saml.api.ResponseParser.XmlParser;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core 2.3.2.1
 */
public class Conditions {

    public static final String CONDITIONS = "Conditions";
    public static final String NOT_BEFORE = "NotBefore";
    public static final String NOT_ON_OR_AFTER = "NotOnOrAfter";

    private static final String TAG_BEGIN_1 = "      <saml:Conditions";
    public static final String OPT_NOT_BEFORE = "          NotBefore=\"[NotBefore]\"";
    public static final String OPT_NOT_ON_OR_AFTER = "          NotOnOrAfter=\"[NotOnOrAfter]\"";
    private static final String TAG_BEGIN_2 = "/>\n";

    private Date notBefore;
    private Date notOnOrAfter;

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotOnOrAfter() {
        return notOnOrAfter;
    }

    public void setNotOnOrAfter(Date notOnOrAfter) {
        this.notOnOrAfter = notOnOrAfter;
    }

    public StringBuffer toXML() {
        StringBuffer xml = new StringBuffer();
        xml.append(TAG_BEGIN_1);
        // NotBefore (optional)
        if (getNotBefore() != null) {
            xml.append(OPT_NOT_BEFORE.replaceAll(
                    Pattern.quote(Response.getPlaceHolder(NOT_BEFORE)),
                    Response.formatIssueInstant(getNotBefore())));
        }
        // NotOnOrAfter (optional)
        if (getNotOnOrAfter() != null) {
            xml.append(OPT_NOT_ON_OR_AFTER.replaceAll(
                    Pattern.quote(Response.getPlaceHolder(NOT_ON_OR_AFTER)),
                    Response.formatIssueInstant(getNotOnOrAfter())));
        }
        xml.append(TAG_BEGIN_2);
        return xml;
    }

    public static Conditions unmarshall(Node node) throws ParseException {
        Conditions conditions = new Conditions();
        // NotBefore (optional)
        String notBefore = XmlParser
                .parseAttribute(node, Conditions.NOT_BEFORE);
        if (notBefore != null && notBefore.trim().length() > 0) {
            conditions.setNotBefore(Response.parseIssueInstant(notBefore));
        }
        // NotOnOrAfter (optional)
        String notOnOrAfter = XmlParser.parseAttribute(node,
                Conditions.NOT_ON_OR_AFTER);
        if (notOnOrAfter != null && notOnOrAfter.trim().length() > 0) {
            conditions
                    .setNotOnOrAfter(Response.parseIssueInstant(notOnOrAfter));
        }
        return conditions;
    }

}
