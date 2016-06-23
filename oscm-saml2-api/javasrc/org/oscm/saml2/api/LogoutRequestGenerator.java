/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 03.06.2013
 *
 *******************************************************************************/

package org.oscm.saml2.api;

import org.oscm.internal.intf.SamlService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * @author mgrubski
 */
@RequestScoped
@ManagedBean
public class LogoutRequestGenerator {



    public String generateLogoutRequest(String samlSessionId) {
        return "http://www.wp.pl";
    }
}
