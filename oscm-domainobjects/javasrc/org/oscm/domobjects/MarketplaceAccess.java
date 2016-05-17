/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: KowalczykA                                                     
 *                                                                              
 *  Creation Date: 17.05.2016                                                                                                                                                                                
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Defines marketplaces with restricted access and organizations which has
 * access to them.
 * 
 * @author KowalczykA
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "marketplace_tkey",
        "organization_tkey" }) )
public class MarketplaceAccess extends DomainObjectWithEmptyDataContainer {

    private static final long serialVersionUID = -3270780943325084256L;

    @Column(name = "marketplace_tkey")
    private long marketplace_tkey;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_tkey")
    private Marketplace marketplace;

    @Column(name = "organization_tkey")
    private long organization_tkey;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_tkey")
    private Organization organization;

    public long getMarketplace_tkey() {
        return marketplace_tkey;
    }

    public void setMarketplace_tkey(long marketplace_tkey) {
        this.marketplace_tkey = marketplace_tkey;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
        if (marketplace != null) {
            setMarketplace_tkey(marketplace.getKey());
        }
    }

    public long getOrganization_tkey() {
        return organization_tkey;
    }

    public void setOrganization_tkey(long organization_tkey) {
        this.organization_tkey = organization_tkey;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        if (organization != null) {
            setOrganization_tkey(organization.getKey());
        }
    }

}
