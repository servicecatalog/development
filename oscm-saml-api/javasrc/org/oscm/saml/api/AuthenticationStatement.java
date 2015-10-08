/* 
 *  Copyright FUJITSU LIMITED 2015 
 **
 * 
 */
package org.oscm.saml.api;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.oscm.saml.api.ResponseParser.XmlParser;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 2.4.3, extends 2.4.2
 */
public class AuthenticationStatement {

    public static final String AUTHENTICATION_STATEMENT = "AuthenticationStatement";
    public static final String AUTHENTICATION_INSTANT = "AuthenticationInstant";
    public static final String AUTHENTICATION_METHOD = "AuthenticationMethod";

    private static final String TAG_BEGIN = "      <saml:AuthenticationStatement \n"
            + "          AuthenticationInstant=\"[AUTHENTICATION_INSTANT]\" AuthenticationMethod=\"[AUTHENTICATION_METHOD]\">\n";
    private static final String TAG_END = "      </saml:AuthenticationStatement>\n";

    /**
     * @see SAML 1.1 Core: 2.4.3, 7.1
     */
    public enum AuthenticationMethod {
        Unspecified("urn:oasis:names:tc:SAML:1.0:am:unspecified"), Password(
                "urn:oasis:names:tc:SAML:1.0:am:password"), Kerberos(
                "urn:ietf:rfc:1510"), SRP("urn:ietf:rfc:2945"), Hardware_Token(
                "urn:oasis:names:tc:SAML:1.0:am:HardwareToken"), SSL_TLS_CBCA(
                "urn:ietf:rfc:2246"), X509_Public_Key(
                "urn:oasis:names:tc:SAML:1.0:am:X509-PKI"), PGP_Public_Key(
                "urn:oasis:names:tc:SAML:1.0:am:PGP"), SPKI_Public_Key(
                "urn:oasis:names:tc:SAML:1.0:am:SPKI"), XKMS_Public_Key(
                "urn:oasis:names:tc:SAML:1.0:am:XKMS"), XML_Digital_Signature(
                "urn:ietf:rfc:3075");

        private String value;
        private static Map<String, AuthenticationMethod> values2Enums;

        private AuthenticationMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static AuthenticationMethod parse(String value) {
            if (value == null) {
                return null;
            }
            return getValues2Enums().get(value);
        }

        private static Map<String, AuthenticationMethod> getValues2Enums() {
            if (values2Enums == null) {
                values2Enums = new HashMap<String, AuthenticationMethod>();
                for (AuthenticationMethod method : AuthenticationMethod
                        .values()) {
                    values2Enums.put(method.getValue(), method);
                }
            }
            return values2Enums;
        }
    }

    private Subject subject;
    private AuthenticationMethod authenticationMethod;
    private Date authenticationInstant;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(
            AuthenticationMethod authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public Date getAuthenticationInstant() {
        return authenticationInstant;
    }

    public void setAuthenticationInstant(Date authenticationInstant) {
        this.authenticationInstant = authenticationInstant;
    }

    public StringBuffer toXML() {
        validate();
        StringBuffer xml = new StringBuffer();
        String tag = TAG_BEGIN;
        // AuthenticationInstant
        tag = tag.replaceAll(Pattern.quote("[AUTHENTICATION_INSTANT]"),
                Response.formatIssueInstant(getAuthenticationInstant()));
        // AuthenticationMethod
        tag = tag.replaceAll(Pattern.quote("[AUTHENTICATION_METHOD]"),
                getAuthenticationMethod().getValue());
        xml.append(tag);
        // <Subject>
        xml.append(getSubject().toXML());
        xml.append(TAG_END);
        return xml;
    }

    public void validate() {
        if (getAuthenticationInstant() == null) {
            throw new IllegalStateException(
                    "AuthenticationInstant is mandatory");
        }
        if (getAuthenticationMethod() == null) {
            throw new IllegalStateException("AuthenticationMethod is mandatory");
        }
        if (getSubject() == null) {
            throw new IllegalStateException("<Subject> is mandatory");
        }
    }

    public static AuthenticationStatement unmarshall(Node authnStatementNode)
            throws ParseException {
        AuthenticationStatement authnStatement = new AuthenticationStatement();
        authnStatement.setAuthenticationInstant(Response
                .parseIssueInstant(XmlParser.parseAttribute(authnStatementNode,
                        AuthenticationStatement.AUTHENTICATION_INSTANT)));
        authnStatement.setAuthenticationMethod(AuthenticationMethod
                .parse(XmlParser.parseAttribute(authnStatementNode,
                        AuthenticationStatement.AUTHENTICATION_METHOD)));
        NodeList children = authnStatementNode.getChildNodes();
        boolean hasSubject = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (Subject.SUBJECT.equals(child.getLocalName())) {
                if (hasSubject) {
                    throw new IllegalStateException(
                            "Duplicate <Subject> occurence inside <AuthenticationStatement>");
                }
                authnStatement.setSubject(Subject.unmarshall(child));
                hasSubject = true;
            }
        }
        authnStatement.validate();
        return authnStatement;
    }

}
