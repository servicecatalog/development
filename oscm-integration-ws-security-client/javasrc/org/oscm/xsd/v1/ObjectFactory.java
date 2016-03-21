/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.xsd.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.oscm.xsd.v1 package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InitMessageListResponse_QNAME = new QName(
            "http://oscm.org/xsd", "initMessageListResponse");
    private final static QName _GetCollectedMessagesResponse_QNAME = new QName(
            "http://oscm.org/xsd", "getCollectedMessagesResponse");
    private final static QName _GetCollectedMessages_QNAME = new QName(
            "http://oscm.org/xsd", "getCollectedMessages");
    private final static QName _InitMessageList_QNAME = new QName(
            "http://oscm.org/xsd", "initMessageList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: org.oscm.xsd.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InitMessageListResponse }
     * 
     */
    public InitMessageListResponse createInitMessageListResponse() {
        return new InitMessageListResponse();
    }

    /**
     * Create an instance of {@link GetCollectedMessagesResponse }
     * 
     */
    public GetCollectedMessagesResponse createGetCollectedMessagesResponse() {
        return new GetCollectedMessagesResponse();
    }

    /**
     * Create an instance of {@link GetCollectedMessages }
     * 
     */
    public GetCollectedMessages createGetCollectedMessages() {
        return new GetCollectedMessages();
    }

    /**
     * Create an instance of {@link InitMessageList }
     * 
     */
    public InitMessageList createInitMessageList() {
        return new InitMessageList();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link InitMessageListResponse }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://oscm.org/xsd", name = "initMessageListResponse")
    public JAXBElement<InitMessageListResponse> createInitMessageListResponse(
            InitMessageListResponse value) {
        return new JAXBElement<InitMessageListResponse>(
                _InitMessageListResponse_QNAME, InitMessageListResponse.class,
                null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link GetCollectedMessagesResponse }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://oscm.org/xsd", name = "getCollectedMessagesResponse")
    public JAXBElement<GetCollectedMessagesResponse> createGetCollectedMessagesResponse(
            GetCollectedMessagesResponse value) {
        return new JAXBElement<GetCollectedMessagesResponse>(
                _GetCollectedMessagesResponse_QNAME,
                GetCollectedMessagesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link GetCollectedMessages }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://oscm.org/xsd", name = "getCollectedMessages")
    public JAXBElement<GetCollectedMessages> createGetCollectedMessages(
            GetCollectedMessages value) {
        return new JAXBElement<GetCollectedMessages>(
                _GetCollectedMessages_QNAME, GetCollectedMessages.class, null,
                value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitMessageList }
     * {@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://oscm.org/xsd", name = "initMessageList")
    public JAXBElement<InitMessageList> createInitMessageList(
            InitMessageList value) {
        return new JAXBElement<InitMessageList>(_InitMessageList_QNAME,
                InitMessageList.class, null, value);
    }

}
