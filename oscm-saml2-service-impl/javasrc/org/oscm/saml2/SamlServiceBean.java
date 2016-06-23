/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 23.06.16 13:57
 *
 ******************************************************************************/

package org.oscm.saml2;

import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.SamlService;
import org.oscm.internal.types.exception.UnsupportedOperationException;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Authored by dawidch
 */
@Stateless(name = "saml2.0Bean")
@Remote(SamlService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SamlServiceBean implements SamlService {

    @Override
    public String createSamlResponse(String requestId) {
        throw new UnsupportedOperationException("Not supported, as OSCM does not act as Idp anymore.");
    }

    @Override
    public String generateLogoutRequest(String idpSessionIndex) {
        return "";
    }
}
