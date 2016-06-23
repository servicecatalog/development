/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 03.06.2013
 *
 *******************************************************************************/

package org.oscm.saml2.api;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.oscm.internal.intf.SamlService;

/**
 * @author mgrubski
 */
@RequestScoped
@ManagedBean
public class LogoutRequestGenerator {

    @EJB(beanName = "saml2.0Bean")
    private SamlService samlService;

    public String generateLogoutRequest(String samlSessionId) {
        return samlService.generateLogoutRequest(samlSessionId);
    }
}
