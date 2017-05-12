/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 26.04.17 09:00
 *
 ******************************************************************************/

package org.oscm.operatorservice.queries;

import org.oscm.dataservice.local.DataService;
import org.oscm.internal.vo.VOSubscriptionUsageEntry;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Authored by dawidch
 */
public class SubscriptionUsageQuery {
    public static Collection<VOSubscriptionUsageEntry> execute(DataService dm) {
        String query = "SELECT \n" +
                "  organization.name AS \"customerOrgName\", \n" +
                "  organization.organizationid AS \"customerOrgId\", \n" +
                "  product.productid AS \"productId\", \n" +
                "  technicalproduct.technicalproductid AS \"technicalProductId\", \n" +
                "  supplier.name AS \"supplierOrgName\", \n" +
                "  supplier.organizationid AS \"supplierOrgId\", \n" +
                "  subscription.subscriptionid, " +
                "  (select count(*) from bssuser.usagelicense where subscription_tkey=subscription.tkey) as numberOfUsers, subscription.vmsNumber as vmsNumber\n" +
                "FROM \n" +
                "  bssuser.subscription, \n" +
                "  bssuser.product, \n" +
                "  bssuser.organization, \n" +
                "  bssuser.technicalproduct, \n" +
                "  bssuser.organization supplier\n" +
                "WHERE \n" +
                "  subscription.product_tkey = product.tkey AND\n" +
                "  subscription.organizationkey = organization.tkey AND\n" +
                "  product.technicalproduct_tkey = technicalproduct.tkey AND\n" +
                "  technicalproduct.organizationkey = supplier.tkey;\n";


        List resultList = dm.createNativeQuery(query).getResultList();
        Collection<VOSubscriptionUsageEntry> result = new ArrayList<>();
        for (Object o : resultList) {
            Object[] columns = (Object[]) o;
            result.add(new VOSubscriptionUsageEntry((String) columns[1], (String) columns[0], (String) columns[6],
                    (String) columns[2],(String) columns[3], (String) columns[4],
                    (String) columns[5], columns[7].toString(), columns[8].toString()));
        }
        return result;
    }
}
