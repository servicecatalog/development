/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.oscm.saml.api.ResponseParser.XmlParser;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 3.4.3
 */
public class Status {

    public static final String STATUS = "Status";
    public static final String STATUS_CODE = "StatusCode";
    public static final String VALUE = "Value";

    public static final String NAMESPACE = "samlp";

    private static final String STATUS_BEGIN = "   <samlp:Status>\n";
    private static final String STATUS_END = "   </samlp:Status>\n";
    private static final String STATUS_CODE_SIMPLE = "      <samlp:StatusCode Value=\"samlp:[STATUS_CODE]\"/>\n";
    private static final String STATUS_CODE_NESTING_BEGIN = "      <samlp:StatusCode Value=\"samlp:[STATUS_CODE]\">\n";
    private static final String STATUS_CODE_NESTING_END = "      </samlp:StatusCode>\n";

    /**
     * @see SAML 1.1 Core: 3.4.3.1
     */
    public enum FirstLevelStatusCode {
        SUCCESS("Success"), VERSION_MISSMATCH("VersionMismatch"), REQUESTER(
                "Requester"), RESPONDER("Responder");

        private String value;
        private static Map<String, FirstLevelStatusCode> values2Enums;

        private FirstLevelStatusCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static FirstLevelStatusCode parse(String value) {
            if (value == null) {
                return null;
            }
            return getValues2Enums().get(value);
        }

        private static Map<String, FirstLevelStatusCode> getValues2Enums() {
            if (values2Enums == null) {
                values2Enums = new HashMap<String, FirstLevelStatusCode>();
                for (FirstLevelStatusCode element : FirstLevelStatusCode
                        .values()) {
                    values2Enums.put(element.getValue(), element);
                }
            }
            return values2Enums;
        }
    }

    /**
     * @see SAML 1.1 Core: 3.4.3.1
     */
    public enum SecondLevelStatusCode {
        REQUEST_VERSION_TOO_HIGH("RequestVersionTooHigh"), REQUEST_VERSION_TOO_LOW(
                "RequestVersionTooLow"), REQUEST_VERSION_DEPRECATED(
                "RequestVersionDeprecated"), TOO_MANY_RESPONSES(
                "TooManyResponses"), REQUEST_DENIED("RequestDenied"), RESOURCE_NOT_RECOGNIZED(
                "ResourceNotRecognized");

        private String value;
        private static Map<String, SecondLevelStatusCode> values2Enums;

        private SecondLevelStatusCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static SecondLevelStatusCode parse(String value) {
            if (value == null) {
                return null;
            }
            return getValues2Enums().get(value);
        }

        private static Map<String, SecondLevelStatusCode> getValues2Enums() {
            if (values2Enums == null) {
                values2Enums = new HashMap<String, SecondLevelStatusCode>();
                for (SecondLevelStatusCode element : SecondLevelStatusCode
                        .values()) {
                    values2Enums.put(element.getValue(), element);
                }
            }
            return values2Enums;
        }
    }

    private FirstLevelStatusCode statusCode;
    private SecondLevelStatusCode secondLevelStatusCode;

    public FirstLevelStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(FirstLevelStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public SecondLevelStatusCode getSecondLevelStatusCode() {
        return secondLevelStatusCode;
    }

    public void setSecondLevelStatusCode(
            SecondLevelStatusCode secondLevelStatusCode) {
        this.secondLevelStatusCode = secondLevelStatusCode;
    }

    public StringBuffer toXML() {
        validate();
        StringBuffer xml = new StringBuffer();
        xml.append(STATUS_BEGIN);
        if (getSecondLevelStatusCode() != null) {
            // second-level status code (optional)
            xml.append(STATUS_CODE_NESTING_BEGIN.replaceAll(
                    Pattern.quote("[STATUS_CODE]"), getStatusCode().getValue()));
            xml.append("   ").append(
                    STATUS_CODE_SIMPLE.replaceAll(
                            Pattern.quote("[STATUS_CODE]"),
                            getSecondLevelStatusCode().getValue()));
            xml.append(STATUS_CODE_NESTING_END);
        } else {
            xml.append(STATUS_CODE_SIMPLE.replaceAll(
                    Pattern.quote("[STATUS_CODE]"), getStatusCode().getValue()));
        }
        xml.append(STATUS_END);
        return xml;
    }

    public void validate() {
        if (getStatusCode() == null) {
            throw new IllegalStateException(
                    "First level <StatusCode> is mandatory");
        }
    }

    public static Status unmarshall(Node statusNode) {
        Status status = new Status();
        NodeList children = statusNode.getChildNodes();
        boolean hasStatusCode = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (Status.STATUS_CODE.equals(child.getLocalName())) {
                if (hasStatusCode) {
                    throw new IllegalStateException(
                            "Duplicate <StatusCode> occurence inside <Status>");
                }
                hasStatusCode = true;
                status.setStatusCode(FirstLevelStatusCode.parse(XmlParser
                        .parseLocalAttribute(child, Status.VALUE)));
                NodeList secondLevelChildren = child.getChildNodes();
                boolean hasSecondLevelStatusCode = false;
                for (int j = 0; j < secondLevelChildren.getLength(); j++) {
                    Node secondLevelChild = children.item(j);
                    if (Status.STATUS_CODE.equals(child.getLocalName())) {
                        if (hasSecondLevelStatusCode) {
                            throw new IllegalStateException(
                                    "Duplicate nested <StatusCode> occurence inside <StatusCode>");
                        }
                        hasSecondLevelStatusCode = true;
                        status.setSecondLevelStatusCode(SecondLevelStatusCode
                                .parse(XmlParser.parseLocalAttribute(
                                        secondLevelChild, Status.VALUE)));
                    }
                }
            }
        }
        status.validate();
        return status;
    }

}
