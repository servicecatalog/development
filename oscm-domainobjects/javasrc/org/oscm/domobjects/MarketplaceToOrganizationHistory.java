/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: weiser                                                      
 *                                                                              
 *  Creation Date: 12.09.2011                                                      
 *                                                                              
 *  Completion Time: 12.09.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * @author weiser
 * 
 */
@Entity
@NamedQuery(name = "MarketplaceToOrganizationHistory.findByObject", query = "SELECT c FROM MarketplaceToOrganizationHistory c WHERE c.objKey=:objKey ORDER BY objversion")
public class MarketplaceToOrganizationHistory extends
        DomainHistoryObject<MarketplaceToOrganizationData> {

    private static final long serialVersionUID = -2113190826293830873L;

    private long marketplaceObjKey;

    private long organizationObjKey;

    public MarketplaceToOrganizationHistory() {
        dataContainer = new MarketplaceToOrganizationData();
    }

    public MarketplaceToOrganizationHistory(MarketplaceToOrganization c) {
        super(c);
        if (c.getMarketplace() != null) {
            setMarketplaceObjKey(c.getMarketplace().getKey());
        }
        if (c.getOrganization() != null) {
            setOrganizationObjKey(c.getOrganization().getKey());
        }
    }

    public void setMarketplaceObjKey(long marketplaceObjKey) {
        this.marketplaceObjKey = marketplaceObjKey;
    }

    public long getMarketplaceObjKey() {
        return marketplaceObjKey;
    }

    public void setOrganizationObjKey(long organizationObjKey) {
        this.organizationObjKey = organizationObjKey;
    }

    public long getOrganizationObjKey() {
        return organizationObjKey;
    }
}
