/* 
 *  Copyright FUJITSU LIMITED 2015 
 **
 * 
 */
package org.oscm.saml.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import org.oscm.saml.api.ResponseParser.XmlParser;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 2.4.2.2
 */
public class NameIdentifier {

    public static final String NAME_IDENTIFIER = "NameIdentifier";
    public static final String NAME_QUALIFIER = "NameQualifier";
    public static final String FORMAT = "Format";

    private static final String TAG_BEGIN_1 = "            <saml:NameIdentifier";
    private static final String OPT_NAME_QUALIFIER = " NameQualifier=\"[NAME_QUALIFIER]\"";
    private static final String OPT_FORMAT = " Format=\"[NAME_FORMAT]\"";
    private static final String TAG_BEGIN_2 = ">";
    private static final String TAG_END = "</saml:NameIdentifier>\n";

    /**
     * @see SAML 1.1 Core: 7.3.3
     */
    public enum Format {
        UNSPECIFIED("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"), EMAIL(
                "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"), X509_SUBJECT(
                "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName"), WIN_DOMAIN_QUALIFIED(
                "urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName");

        private String value;
        private static Map<String, Format> values2Enums;

        private Format(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Format parse(String value) {
            if (value == null) {
                return null;
            }
            return getValues2Enums().get(value);
        }

        private static Map<String, Format> getValues2Enums() {
            if (values2Enums == null) {
                values2Enums = new HashMap<String, Format>();
                for (Format format : Format.values()) {
                    values2Enums.put(format.getValue(), format);
                }
            }
            return values2Enums;
        }

    }

    private String nameIdentifier;
    private Format format;
    private String nameQualifier;

    public String getNameIdentifier() {
        return nameIdentifier;
    }

    public void setNameIdentifier(String nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getNameQualifier() {
        return nameQualifier;
    }

    public void setNameQualifier(String nameQualifier) {
        this.nameQualifier = nameQualifier;
    }

    public StringBuffer toXML() {
        validate();
        StringBuffer xml = new StringBuffer();
        xml.append(TAG_BEGIN_1);
        // NameQualifier (optional)
        if (getNameQualifier() != null
                && getNameQualifier().trim().length() > 0) {
            xml.append(OPT_NAME_QUALIFIER.replaceAll(
                    Pattern.quote("[NAME_QUALIFIER]"), getNameQualifier()));
        }
        // Format (optional)
        if (getFormat() != null) {
            xml.append(OPT_FORMAT.replaceAll(Pattern.quote("[NAME_FORMAT]"),
                    getFormat().getValue()));
        }
        xml.append(TAG_BEGIN_2);
        xml.append(getNameIdentifier());
        xml.append(TAG_END);
        return xml;
    }

    public void validate() {
        if (getNameIdentifier() == null
                || getNameIdentifier().trim().length() <= 0) {
            throw new IllegalStateException("NameIdentifier is mandatory");
        }
    }

    public static NameIdentifier unmarshall(Node nameIdentifierNode) {
        NameIdentifier nameIdentifier = new NameIdentifier();
        nameIdentifier.setNameIdentifier(nameIdentifierNode.getTextContent());
        nameIdentifier.setNameQualifier(XmlParser.parseAttribute(
                nameIdentifierNode, NameIdentifier.NAME_QUALIFIER));
        nameIdentifier.setFormat(NameIdentifier.Format.parse(XmlParser
                .parseAttribute(nameIdentifierNode, NameIdentifier.FORMAT)));
        nameIdentifier.validate();
        return nameIdentifier;
    }

}
