/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                                                                                                                                                                             
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.dao;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Organization;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.pagination.Filter;
import org.oscm.pagination.Pagination;
import org.oscm.pagination.TableColumns;

@Interceptors({ ExceptionMapper.class })
public class UserSubscriptionDao {

    private final DataService dataManager;
    private Log4jLogger logger = LoggerFactory.getLogger(SubscriptionDao.class);

    public UserSubscriptionDao(DataService ds) {
        this.dataManager = ds;
    }

    private String paginatedQuery(String selectWhereQuery,
            Pagination pagination) {

        String queryOrderBy = " ORDER BY subscription.assigned DESC, subscription.subscriptionid ASC ";

        if (pagination.getSorting() != null) {
            queryOrderBy = createQueryOrderBy(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null
                && pagination.getFilterSet().size() > 0) {
            queryFilter = createQueryFilter(pagination, queryFilter);
        }

        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    private String paginatedQueryForCountSubs(String selectWhereQuery,
            Pagination pagination) {

        String queryFilter = "";
        if (pagination.getFilterSet() != null
                && pagination.getFilterSet().size() > 0) {
            queryFilter = createQueryFilter(pagination, queryFilter);
        }

        return selectWhereQuery + queryFilter;
    }

    private String createQueryOrderBy(Pagination pagination) {
        String queryOrderBy;

        String whenUserId = " WHEN '" + TableColumns.SUBSCRIPTION_ID.name()
                + "' THEN subscription.subscriptionid ";
        String whenFirstName = "WHEN '" + TableColumns.ROLE_IN_SUB.name()
                + "' THEN subscription.displayedrole ";

        queryOrderBy = " ORDER BY (CASE :sortColumn " + whenUserId
                + whenFirstName + " END) "
                + pagination.getSorting().getOrder().name();
        return queryOrderBy;
    }

    private String createQueryFilter(Pagination pagination,
            String queryFilter) {

        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        StringBuilder queryFilterBuilder = new StringBuilder(queryFilter);

        queryFilterBuilder.append(" WHERE ");

        int i = 0;
        while (filterIterator.hasNext()) {
            if (i != 0) {
                queryFilterBuilder.append(" AND ");
            }
            Filter filter = filterIterator.next();
            addFilterColumn(pagination, queryFilterBuilder, filter);
            i++;
        }
        return queryFilterBuilder.toString();
    }

    private void addFilterColumn(Pagination pagination,
            StringBuilder queryFilterBuilder, Filter filter) {
        switch (filter.getColumn()) {
        case SUBSCRIPTION_ID:
            queryFilterBuilder.append(
                    " subscription.subscriptionid ILIKE :filterExpressionSubscriptionId");
            break;
        case ROLE_IN_SUB:
            queryFilterBuilder.append(
                    " subscription.displayedrole ILIKE :filterExpressionRoleInSub ");
        default:
            break;
        }
    }

    private void setPaginationParameters(Pagination pagination, Query query) {
        setSortingParameter(query, pagination);
        setFilterParameters(query, pagination);

        query.setFirstResult(pagination.getOffset());
        query.setMaxResults(pagination.getLimit());
    }

    private void setSortingParameter(Query query, Pagination pagination) {
        if (pagination.getSorting() != null) {
            query.setParameter("sortColumn",
                    pagination.getSorting().getColumn().name());
        }
    }

    private void setFilterParameters(Query query, Pagination pagination) {
        if (pagination.getFilterSet() != null) {
            for (Filter filter : pagination.getFilterSet()) {
                setFilterParameter(query, filter);
            }
        }
    }

    private void setFilterParameter(Query query, Filter filter) {
        switch (filter.getColumn()) {
        case SUBSCRIPTION_ID:
            query.setParameter("filterExpressionSubscriptionId",
                    filter.getExpression() + "%");
            break;
        case ROLE_IN_SUB:
            query.setParameter("filterExpressionRoleInSub",
                    filter.getExpression() + "%");
            break;
        default:
            break;
        }
    }

    public List<Object[]> getUserAssignableSubscriptions(Pagination pagination,
            Organization owner, long userKey, Set<SubscriptionStatus> states) {

        String nativeQuery = getQueryUserAssignableSubscriptions(pagination);
        Query q = dataManager.createNativeQuery(nativeQuery);

        q.setParameter("locale", owner.getLocale());
        q.setParameter("orgKey", Long.valueOf(owner.getKey()));
        q.setParameter("userKey", Long.valueOf(userKey));
        q.setParameter("states", getSubscriptionStatesAsString(states));

        setPaginationParameters(pagination, q);
        return ParameterizedTypes.list(q.getResultList(), Object[].class);
    }

    public Long getCountUserAssignableSubscriptions(Pagination pagination,
            Organization owner, long userKey, Set<SubscriptionStatus> states) {

        String nativeQuery = getQueryCountUserAssignableSubscriptions(
                pagination);
        Query q = dataManager.createNativeQuery(nativeQuery);

        q.setParameter("locale", owner.getLocale());
        q.setParameter("orgKey", Long.valueOf(owner.getKey()));
        q.setParameter("userKey", Long.valueOf(userKey));
        q.setParameter("states", getSubscriptionStatesAsString(states));

        setPaginationParameters(pagination, q);
        BigInteger result = (BigInteger) q.getSingleResult();

        return result.longValue();
    }

    private String getQueryUserAssignableSubscriptions(Pagination pagination) {

        String query = "SELECT * FROM ("
                + "                     SELECT  sub.subscriptionid, "
                + "                             true as assigned, "
                + "                             roles.roleid, "
                + "                             (SELECT loc.value FROM bssuser.localizedresource loc WHERE loc.objecttype = 'ROLE_DEF_NAME' AND loc.objectkey=roles.tkey AND loc.locale =:locale) as displayedrole, "
                + "                             license.tkey as key, "
                + "                             license.version as version"
                + "                     FROM bssuser.subscription sub, bssuser.usagelicense license"
                + "                     LEFT JOIN bssuser.roledefinition roles"
                + "                     ON roles.tkey = license.roledefinition_tkey"
                + "                     WHERE license.subscription_tkey = sub.tkey "
                + "                             AND sub.status IN (:states) "
                + "                             AND sub.organizationKey =:orgKey "
                + "                             AND license.user_tkey =:userKey "
                + "                     UNION "
                + "                     SELECT  sub.subscriptionid, "
                + "                             false as assigned, "
                + "                             null as roleid, "
                + "                             null as displayedrole, "
                + "                             null as key, "
                + "                             null as version"
                + "                     FROM bssuser.subscription sub"
                + "                     WHERE sub.tkey NOT IN (SELECT lic.subscription_tkey FROM bssuser.usagelicense lic WHERE lic.user_tkey=:userKey)"
                + "                             AND sub.status IN (:states)"
                + "                             AND sub.organizationKey =:orgKey) AS subscription ";

        return paginatedQuery(query, pagination);
    }

    private String getQueryCountUserAssignableSubscriptions(
            Pagination pagination) {

        String query = "SELECT COUNT(*) FROM ("
                + "                     SELECT  sub.subscriptionid, "
                + "                             true as assigned, "
                + "                             roles.roleid, "
                + "                             (SELECT loc.value FROM bssuser.localizedresource loc WHERE loc.objecttype = 'ROLE_DEF_NAME' AND loc.objectkey=roles.tkey AND loc.locale =:locale) as displayedrole, "
                + "                             license.tkey as key, "
                + "                             license.version as version"
                + "                     FROM bssuser.subscription sub, bssuser.usagelicense license"
                + "                     LEFT JOIN bssuser.roledefinition roles"
                + "                     ON roles.tkey = license.roledefinition_tkey"
                + "                     WHERE license.subscription_tkey = sub.tkey "
                + "                             AND sub.status IN (:states) "
                + "                             AND sub.organizationKey =:orgKey "
                + "                             AND license.user_tkey =:userKey "
                + "                     UNION "
                + "                     SELECT  sub.subscriptionid, "
                + "                             false as assigned, "
                + "                             null as roleid, "
                + "                             null as displayedrole, "
                + "                             null as key, "
                + "                             null as version"
                + "                     FROM bssuser.subscription sub"
                + "                     WHERE sub.tkey NOT IN (SELECT lic.subscription_tkey FROM bssuser.usagelicense lic WHERE lic.user_tkey=:userKey)"
                + "                             AND sub.status IN (:states)"
                + "                             AND sub.organizationKey =:orgKey) AS subscription ";

        return paginatedQueryForCountSubs(query, pagination);
    }

    Set<String> getSubscriptionStatesAsString(Set<SubscriptionStatus> states) {
        Set<String> statesAsString = new HashSet<String>();
        for (SubscriptionStatus s : states) {
            statesAsString.add(s.name());
        }
        return statesAsString;
    }

}
