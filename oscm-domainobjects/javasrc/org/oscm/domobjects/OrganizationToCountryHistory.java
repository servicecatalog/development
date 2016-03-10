/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * History object for the domain object <code>OrganizationToCountry</code>
 * 
 * @author cheld, pock
 * 
 */
@Entity
@NamedQuery(name = "OrganizationToCountryHistory.findByObject", query = "select c from OrganizationToCountryHistory c where c.objKey=:objKey order by objversion")
public class OrganizationToCountryHistory extends
        DomainHistoryObjectWithEmptyDataContainer {

    private static final long serialVersionUID = 240061340382244025L;

    /**
     * Organization key.
     */
    private long organizationObjKey;

    /**
     * SupportedCountry key.
     */
    private long supportedCountryObjKey;

    /**
     * Default constructor.
     */
    public OrganizationToCountryHistory() {
        super();
    }

    /**
     * Constructs OrganizationToCountryHistory from a OrganizationToCountry
     * domain object
     * 
     * @param country
     *            - the organization
     */
    public OrganizationToCountryHistory(OrganizationToCountry country) {
        super(country);
        if (country.getOrganization() != null) {
            setOrganizationObjKey(country.getOrganization().getKey());
        }
        if (country.getSupportedCountry() != null) {
            setSupportedCountryObjKey(country.getSupportedCountry().getKey());
        }
    }

    /**
     * Setter for organization.
     * 
     * @param organizationObjKey
     */
    public void setOrganizationObjKey(long organizationObjKey) {
        this.organizationObjKey = organizationObjKey;
    }

    /**
     * Getter for organization.
     * 
     * @return Organization key.
     */
    public long getOrganizationObjKey() {
        return organizationObjKey;
    }

    /**
     * Setter for supported country
     */
    public void setSupportedCountryObjKey(long supportedCountryObjKey) {
        this.supportedCountryObjKey = supportedCountryObjKey;
    }

    /**
     * Getter for supported country
     */
    public long getSupportedCountryObjKey() {
        return supportedCountryObjKey;
    }

}
