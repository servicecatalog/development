/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 11.10.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.common.saml2;

import java.io.Serializable;
import java.net.URL;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * @author roderus
 * 
 */
@ManagedBean
@RequestScoped
public class Saml2Model implements Serializable {

    private static final long serialVersionUID = 1L;

    private String encodedAuthnRequest;
    private String relayState;
    private String acsUrl;
    private String encodedAuthnLogoutRequest;
    private String logoffUrl;
    private String relayStateForLogout;

    public String getEncodedAuthnRequest() {
        return encodedAuthnRequest;
    }

    public void setEncodedAuthnRequest(String encodedAuthnRequest) {
        this.encodedAuthnRequest = encodedAuthnRequest;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public void setEncodedAuthnLogoutRequest(String encodedAuthnLogoutRequest) {
        this.encodedAuthnLogoutRequest = encodedAuthnLogoutRequest;
    }

    public String getEncodedAuthnLogoutRequest() {
        return encodedAuthnLogoutRequest;
    }

    public void setLogoffUrl(String logoffUrl) {
        this.logoffUrl = logoffUrl;
    }

    public String getLogoffUrl() {
        return logoffUrl;
    }

    public String getRelayStateForLogout() {
        return relayStateForLogout;
    }

    public void setRelayStateForLogout(String relayStateForLogout) {
        this.relayStateForLogout = relayStateForLogout;
    }
}
