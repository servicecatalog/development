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

}
