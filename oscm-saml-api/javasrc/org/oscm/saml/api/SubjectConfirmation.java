/* 
 *  Copyright FUJITSU LIMITED 2016 
 **
 * 
 */
package org.oscm.saml.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 2.4.2.3
 */
public class SubjectConfirmation {

    public static final String SUBJECT_CONFIRMATION = "SubjectConfirmation";
    public static final String CONFIRMATION_METHOD = "ConfirmationMethod";

    private static final String TAG_BEGIN = "            <saml:SubjectConfirmation>";
    private static final String TAG_END = " \n            </saml:SubjectConfirmation>\n";
    private static final String TAG_CONFIRMATION_METHOD_BEGIN = " \n               <saml:ConfirmationMethod>";
    private static final String TAG_CONFIRMATION_METHOD_END = "</saml:ConfirmationMethod>";

    /**
     * @see SAML 1.1 Bind: 4.1.1.6, 4.1.2.5
     */
    public enum ConfirmationMethod {
        BEARER("urn:oasis:names:tc:SAML:1.0:cm:bearer"), ARTIFACT(
                "urn:oasis:names:tc:SAML:1.0:cm:artifact");

        private String value;
        private static Map<String, ConfirmationMethod> values2Enums;

        private ConfirmationMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ConfirmationMethod parse(String value) {
            if (value == null) {
                return null;
            }
            return getValues2Enums().get(value);
        }

        private static Map<String, ConfirmationMethod> getValues2Enums() {
            if (values2Enums == null) {
                values2Enums = new HashMap<String, ConfirmationMethod>();
                for (ConfirmationMethod format : ConfirmationMethod.values()) {
                    values2Enums.put(format.getValue(), format);
                }
            }
            return values2Enums;
        }

    }

    private List<ConfirmationMethod> confirmationMethods = new ArrayList<ConfirmationMethod>();

    public List<ConfirmationMethod> getConfirmationMethods() {
        return confirmationMethods;
    }

    public StringBuffer toXML() {
        validate();
        StringBuffer xml = new StringBuffer();
        xml.append(TAG_BEGIN);
        for (ConfirmationMethod confirmationMethod : getConfirmationMethods()) {
            xml.append(TAG_CONFIRMATION_METHOD_BEGIN)
                    .append(confirmationMethod.getValue())
                    .append(TAG_CONFIRMATION_METHOD_END);
        }
        xml.append(TAG_END);
        return xml;
    }

    public void validate() {
        if (getConfirmationMethods().isEmpty()) {
            throw new IllegalStateException(
                    "At least one <ConfirmationMethod> is mandatory");
        }
    }

    public static SubjectConfirmation unmarshall(Node assertionNode) {
        SubjectConfirmation subjectConfirmation = new SubjectConfirmation();
        NodeList children = assertionNode.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (CONFIRMATION_METHOD.equals(child.getLocalName())) {
                subjectConfirmation.getConfirmationMethods().add(
                        ConfirmationMethod.parse(child.getTextContent()));
            }
        }
        subjectConfirmation.validate();
        return subjectConfirmation;
    }

}
