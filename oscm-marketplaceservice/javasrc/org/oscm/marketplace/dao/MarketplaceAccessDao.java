/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-23
 *
 *******************************************************************************/
package org.oscm.marketplace.dao;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.MarketplaceAccess;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by BadziakP on 2016-05-23.
 */
@Stateless
@LocalBean
public class MarketplaceAccessDao {

    @EJB(beanInterface = DataService.class)
    DataService dataService;

    public List<MarketplaceAccess> getForMarketplaceKey(long marketplaceKey) {
        Query query = dataService
                .createNamedQuery("MarketplaceAccess.findByMarketplace");
        query.setParameter("marketplace_tkey", marketplaceKey);
        return ParameterizedTypes.list(query.getResultList(),
                MarketplaceAccess.class);
    }

    public void removeAccessForMarketplace(long marketplaceKey) {
        Query query = dataService
                .createNamedQuery("MarketplaceAccess.removeAllForMarketplace");
        query.setParameter("marketplace_tkey", marketplaceKey);
        query.executeUpdate();
    }

    public List<Object[]> getOrganizationsWithMplAndSubscriptions(long marketplaceKey) {

        String querySelect = "SELECT o.tkey as orgKey, "
                + "o.organizationid as orgId, "
                + "o.name as name, "
                + "(SELECT true FROM marketplaceaccess ma where ma.organization_tkey=o.tkey and ma.marketplace_tkey=:marketplaceKey) as hasAccess, "
                + "(SELECT count(s.tkey) FROM subscription s WHERE s.organizationkey=o.tkey AND s.marketplace_tkey=:marketplaceKey AND s.status<>'SUSPENDED') as subscriptions "
                + "FROM organization o";

        Query query = dataService.createNativeQuery(querySelect);

        query.setParameter("marketplaceKey", Long.valueOf(marketplaceKey));

        return ParameterizedTypes.list(query.getResultList(), Object[].class);
    }

}
