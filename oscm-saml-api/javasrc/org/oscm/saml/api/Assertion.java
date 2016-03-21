/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.oscm.saml.api.ResponseParser.XmlParser;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 2.3.2
 */
public class Assertion {

    public static final String ASSERTION = "Assertion";
    public static final String ASSERTION_ID = "AssertionID";
    public static final String ISSUER = "Issuer";
    public static final String ISSUE_INSTANT = "IssueInstant";

    private static final String TAG_BEGIN = "   <saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" \n"
            + "        AssertionID=\"[ASSERTION_ID]\" \n"
            + "        IssueInstant=\"[ASSERTION_INSTANT]\" Issuer=\"[ASSERTION_ISSUER]\" \n"
            + "        MajorVersion=\"1\" MinorVersion=\"1\">\n";
    private static final String TAG_END = "   </saml:Assertion>\n";

    private String issuer;
    private Date issueInstant;
    private String id;
    private Conditions conditions;
    private List<AuthenticationStatement> authenticationStatements = new ArrayList<AuthenticationStatement>();

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getIssueInstant() {
        return issueInstant;
    }

    public void setIssueInstant(Date issueInstant) {
        this.issueInstant = issueInstant;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    public List<AuthenticationStatement> getAuthenticationStatements() {
        return authenticationStatements;
    }

    public StringBuffer toXML() {
        validate();
        StringBuffer xml = new StringBuffer();
        String tag = TAG_BEGIN;
        // AssertionID
        tag = tag.replaceAll(Pattern.quote("[ASSERTION_ID]"), getID());
        // Issuer
        tag = tag.replaceAll(Pattern.quote("[ASSERTION_ISSUER]"), getIssuer());
        // IssueInstant
        tag = tag.replaceAll(Pattern.quote("[ASSERTION_INSTANT]"),
                Response.formatIssueInstant(getIssueInstant()));
        xml.append(tag);

        // <Conditions> (optional)
        if (getConditions() != null) {
            xml.append(getConditions().toXML());
        }

        // <AuthenticationStatement>
        for (AuthenticationStatement authnStatement : getAuthenticationStatements()) {
            xml.append(authnStatement.toXML());
        }

        xml.append(TAG_END);
        return xml;
    }

    public void validate() {
        if (getID() == null || getID().trim().length() <= 0) {
            throw new IllegalStateException("AssertionID is mandatory");
        }
        if (getIssuer() == null || getIssuer().trim().length() <= 0) {
            throw new IllegalStateException("Issuer is mandatory");
        }
        if (getIssueInstant() == null) {
            throw new IllegalStateException("IssueInstant is mandatory");
        }
        if (getAuthenticationStatements().isEmpty()) {
            throw new IllegalStateException(
                    "At least one of the following elements is mandatory: <Statement>, <SubjectStatement>, <AuthenticationStatement>, <AuthorizationDecisionStatement>, <AttributeStatement>");
        }
    }

    public static Assertion unmarshall(Node assertionNode)
            throws ParseException {

        Assertion assertion = new Assertion();
        assertion.setID(XmlParser.parseAttribute(assertionNode,
                Assertion.ASSERTION_ID));
        assertion.setIssuer(XmlParser.parseAttribute(assertionNode,
                Assertion.ISSUER));
        assertion.setIssueInstant(Response.parseIssueInstant(XmlParser
                .parseAttribute(assertionNode, Assertion.ISSUE_INSTANT)));

        NodeList children = assertionNode.getChildNodes();
        boolean hasConditions = false;
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (Conditions.CONDITIONS.equals(child.getLocalName())) {
                if (hasConditions) {
                    throw new IllegalStateException(
                            "Duplicate <Conditions> occurence inside <Assertion>");
                }
                hasConditions = true;
                assertion.setConditions(Conditions.unmarshall(child));
            } else if (AuthenticationStatement.AUTHENTICATION_STATEMENT
                    .equals(child.getLocalName())) {
                assertion.getAuthenticationStatements().add(
                        AuthenticationStatement.unmarshall(child));
            }
        }
        assertion.validate();
        return assertion;
    }

}
