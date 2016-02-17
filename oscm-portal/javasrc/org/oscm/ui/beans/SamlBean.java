/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.ui.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Backing bean for SAML based authentication and authorization.
 * 
 * @author barzu
 */
@ViewScoped
@ManagedBean(name="samlBean")
public class SamlBean extends BaseBean {

    public static String TARGET_KEY = "TARGET";
    public static String ACS_KEY = "ACS";
    public static String REQUEST_ID = "authID";

    public String getTarget() {
        return getRequestParameter(TARGET_KEY);
    }

    public String getSamlResponse() {
        String samlResponse = getSamlService().createSamlResponse(
                getRequestParameter(REQUEST_ID));
        return samlResponse;
    }

    public String getAcsUrl() {
        return getRequestParameter(ACS_KEY);
    }

}
