/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *******************************************************************************/
package org.oscm.tenant.local;

import javax.ejb.Local;
/**
 * Local interface providing the functionality to retrieve and manipulate
 * configuration settings of tenants.
 *
 * @author PLGrubskiM on 2017-07-03.
 *
 */
@Local
public interface TenantConfigurationServiceLocal {

    /**
     * Retrieves the value of HTTP request method for the specified tenant ID.
     *
     * @param tenantId
     * @return GET or POST
     */
    String getHttpMethodForTenant(String tenantId);

    /**
     * Retrieves the value of the SAML Issuer for the specified tenant ID.
     *
     * @param tenantId
     * @return Issuer for SAML assertion
     */
    String getIssuerForTenant(String tenantId);

    /**
     * Retrieves the value of the IDP URL for the specified tenant ID.
     *
     * @param tenantId
     * @return IDP URL
     */
    String getIdpUrlForTenant(String tenantId);
}
