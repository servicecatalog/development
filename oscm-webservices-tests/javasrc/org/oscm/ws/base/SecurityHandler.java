/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 25.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws.base;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


/**
 * @author stavreva
 *
 */
public final class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
    
    private String userId;
    private String password;

    public SecurityHandler(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext msgCtx) {

        // Indicator telling us which direction this message is going in
        final Boolean outInd = (Boolean) msgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // Handler must only add security headers to outbound messages
        if (outInd.booleanValue()) {
            try {
                // Get the SOAP Envelope
                final SOAPEnvelope envelope = msgCtx.getMessage().getSOAPPart().getEnvelope();

                // Header may or may not exist yet
                SOAPHeader header = envelope.getHeader();
                if (header == null)
                    header = envelope.addHeader();

                // Add WSS Usertoken Element Tree 
                final SOAPElement security = header.addChildElement("Security", "wsse",
                        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                final SOAPElement userToken = security.addChildElement("UsernameToken", "wsse");
                userToken.addChildElement("Username", "wsse").addTextNode(userId);
                userToken.addChildElement("Password", "wsse").addAttribute(new QName("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText").addTextNode(password);

            } catch (final Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /* (non-Javadoc)
     * @see javax.xml.ws.handler.Handler#close(javax.xml.ws.handler.MessageContext)
     */
    @Override
    public void close(MessageContext arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.xml.ws.handler.Handler#handleFault(javax.xml.ws.handler.MessageContext)
     */
    @Override
    public boolean handleFault(SOAPMessageContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.xml.ws.handler.soap.SOAPHandler#getHeaders()
     */
    @Override
    public Set<QName> getHeaders() {
        // TODO Auto-generated method stub
        return null;
    }

}