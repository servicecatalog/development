/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Author: badziakp
 *
 *  Creation Date: 29.08.2016
 *
 *  Completion Time:
 *
 *******************************************************************************/
package org.oscm.domobjects;

import org.oscm.domobjects.annotations.BusinessKey;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "tenantId" }))
@BusinessKey(attributes = { "tenantId" })
public class Tenant extends DomainObjectWithVersioning<TenantData> {

    public Tenant() {
        super();
        dataContainer = new TenantData();
    }

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Collection<Organization> organizations;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Collection<Marketplace> marketplaces;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Collection<PlatformUser> platformUsers;

    public Collection<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Collection<Organization> organizations) {
        this.organizations = organizations;
    }

    public Collection<Marketplace> getMarketplaces() {
        return marketplaces;
    }

    public void setMarketplaces(Collection<Marketplace> marketplaces) {
        this.marketplaces = marketplaces;
    }

    public Collection<PlatformUser> getPlatformUsers() {
        return platformUsers;
    }

    public void setPlatformUsers(Collection<PlatformUser> platformUsers) {
        this.platformUsers = platformUsers;
    }
}
