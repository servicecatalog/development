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
import org.oscm.internal.types.exception.SaaSApplicationException;

/**
 * @author mgrubski
 */
@RequestScoped
@ManagedBean
public class LogoutRequestGenerator {

    @EJB(beanName = "saml2.0Bean")
    private SamlService samlService;


    public String generateLogoutRequest(String samlSessionId, String nameID) throws SaaSApplicationException {
        return samlService.generateLogoutRequest(samlSessionId, nameID);
    }
}
