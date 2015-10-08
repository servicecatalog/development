/* 
 *  Copyright FUJITSU LIMITED 2015 
 **
 * 
 */
package org.oscm.saml.api;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author barzu
 * 
 * @see SAML 1.1 Core: 2.4.2.1
 */
public class Subject {

    public static final String SUBJECT = "Subject";

    private static final String TAG_BEGIN = "         <saml:Subject>\n";
    private static final String TAG_END = "         </saml:Subject>\n";

    private NameIdentifier nameIdentifier;
    private SubjectConfirmation subjectConfirmation;

    public NameIdentifier getNameIdentifier() {
        return nameIdentifier;
    }

    public void setNameIdentifier(NameIdentifier nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public SubjectConfirmation getSubjectConfirmation() {
        return subjectConfirmation;
    }

    public void setSubjectConfirmation(SubjectConfirmation subjectConfirmation) {
        this.subjectConfirmation = subjectConfirmation;
    }

    public StringBuffer toXML() {
        validate();
        StringBuffer xml = new StringBuffer();
        xml.append(TAG_BEGIN);
        if (getNameIdentifier() != null) {
            xml.append(getNameIdentifier().toXML());
        }
        if (getSubjectConfirmation() != null) {
            xml.append(getSubjectConfirmation().toXML());
        }
        xml.append(TAG_END);
        return xml;
    }

    public void validate() {
        if (getNameIdentifier() == null && getSubjectConfirmation() == null) {
            throw new IllegalStateException(
                    "At least one of the following elements is mandatory: <NameIdentifier>, <SubjectConfirmation>");
        }
    }

    public static Subject unmarshall(Node subjectNode) {
        Subject subject = new Subject();
        NodeList children = subjectNode.getChildNodes();
        boolean hasNameIdentifier = false;
        boolean hasSubjectConfirmation = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (NameIdentifier.NAME_IDENTIFIER.equals(child.getLocalName())) {
                if (hasNameIdentifier) {
                    throw new IllegalStateException(
                            "Duplicate <NameIdentifier> occurence inside <Subject>");
                }
                subject.setNameIdentifier(NameIdentifier.unmarshall(child));
                hasNameIdentifier = true;
            } else if (SubjectConfirmation.SUBJECT_CONFIRMATION.equals(child
                    .getLocalName())) {
                if (hasSubjectConfirmation) {
                    throw new IllegalStateException(
                            "Duplicate <SubjectConfirmation> occurence inside <Subject>");
                }
                subject.setSubjectConfirmation(SubjectConfirmation
                        .unmarshall(child));
                hasSubjectConfirmation = true;
            }
        }
        subject.validate();
        return subject;
    }

}
