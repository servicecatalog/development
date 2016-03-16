/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2014-6-4                                       
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.dao;

import java.math.BigInteger;
import java.util.*;

import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.*;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.domobjects.enums.ModifiedEntityType;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.internal.tables.Pagination;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UnitRoleType;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.paginator.Filter;
import org.oscm.paginator.Sorting;
import org.oscm.paginator.TableColumns;

/**
 * @author Mao
 *
 */
@Interceptors({ ExceptionMapper.class })
public class SubscriptionDao {

    private final DataService dataManager;
    private Log4jLogger logger = LoggerFactory.getLogger(SubscriptionDao.class);

    public SubscriptionDao(DataService ds) {
        this.dataManager = ds;
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> getActiveSubscriptions() {
        Query query = dataManager.createNamedQuery("Subscription.getByStatus");
        query.setParameter("status", SubscriptionStatus.ACTIVE);
        return ParameterizedTypes.list(query.getResultList(),
                Subscription.class);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getSubscriptionsWithRoles(Organization owner,
            Set<SubscriptionStatus> states) {
        Query q = dataManager
                .createNamedQuery("Subscription.getForOrgFetchRoles");
        q.setParameter("orgKey", Long.valueOf(owner.getKey()));
        q.setParameter("status", states);
        return ParameterizedTypes.list(q.getResultList(),
                Object[].class);
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> getSubscriptionsForOwner(PlatformUser owner) {
        Query q = dataManager.createNamedQuery("Subscription.getForOwner");
        q.setParameter("ownerKey", Long.valueOf(owner.getKey()));
        return ParameterizedTypes.list(q.getResultList(),
                Subscription.class);
    }

    public Long findSubscriptionForAsyncCallBack(String subscriptionId,
            String organizationId) {
        Query query = dataManager
                .createNamedQuery(
                        "ModifiedEntity.findSubscriptionKeyByOrgIdAndSubId");
        query.setParameter("organizationId", organizationId);
        query.setParameter("subOrgIdType",
                ModifiedEntityType.SUBSCRIPTION_ORGANIZATIONID);
        query.setParameter("subscriptionId", subscriptionId);
        query.setParameter("subIdType",
                ModifiedEntityType.SUBSCRIPTION_SUBSCRIPTIONID);
        return (Long) query.getSingleResult();
    }

    public Long getNumberOfVisibleSubscriptions(
            TechnicalProduct technicalProduct, Organization organization) {
        Query query = dataManager
                .createNamedQuery("Subscription.numberOfVisibleSubscriptions");
        query.setParameter("productKey",
                Long.valueOf(technicalProduct.getKey()));
        query.setParameter("orgKey", Long.valueOf(organization.getKey()));
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> getSubscriptionsForUser(PlatformUser user) {
        Query query = dataManager
                .createNamedQuery("Subscription.getCurrentUserSubscriptions");
        query.setParameter("userKey", Long.valueOf(user.getKey()));
        query.setParameter("status", Subscription.VISIBLE_SUBSCRIPTION_STATUS);
        return ParameterizedTypes.list(
                query.getResultList(), Subscription.class);
    }

    public Long hasSubscriptionsBasedOnOnBehalfServicesForTp(
            Subscription subscription) {
        Query query = dataManager
                .createNamedQuery("Subscription.hasSubscriptionsBasedOnOnBehalfServicesForTp");
        query.setParameter(
                "tpOrgKey",
                Long.valueOf(subscription.getProduct().getTechnicalProduct()
                        .getOrganization().getKey()));
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<String> getSubscriptionIdsForMyCustomers(Organization org,
            Set<SubscriptionStatus> states) {
        Query query = dataManager
                .createNamedQuery("Subscription.getSubscriptionIdsForMyCustomers");
        query.setParameter("offerer", org);
        query.setParameter("states", states);
        return ParameterizedTypes.list(query.getResultList(),
                String.class);
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> getSubscriptionsForMyCustomers(Organization org,
            Set<SubscriptionStatus> states) {
        Query query = dataManager
                .createNamedQuery("Subscription.getSubscriptionsForMyCustomers");
        query.setParameter("offerer", org);
        query.setParameter("states", states);

        return ParameterizedTypes.list(
                query.getResultList(), Subscription.class);
    }

    public List<Subscription> getSubscriptionsForMyCustomers(PlatformUser user,
            Set<SubscriptionStatus> states, Pagination pagination) {
        String queryString = getQuerySubscriptionsForMyCustomers(pagination);
        return getSubscriptionsForVendor(user, states, pagination, queryString);
    }

    @SuppressWarnings("unchecked")
    List<Subscription> getSubscriptionsForVendor(Organization org,
            Set<SubscriptionStatus> states, Pagination pagination,
            String queryString) {

        Set<String> statesAsString = getSubscriptionStatesAsString(states);
        Query query = dataManager.createNativeQuery(queryString, Subscription.class);
        query.setParameter("offerer", Long.valueOf(org.getKey()));
        query.setParameter("states", statesAsString);

        setPaginationParameters(pagination, query);

        return query.getResultList();
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    List<Subscription> getSubscriptionsForVendor(PlatformUser user,
            Set<SubscriptionStatus> states, Pagination pagination,
            String queryString) {

        Set<String> statesAsString = getSubscriptionStatesAsString(states);
        Query query = dataManager.createNativeQuery(queryString, Subscription.class);
        try {
            query.setParameter("locale", user.getLocale());
            query.setParameter("objecttype", LocalizedObjectTypes.PRODUCT_MARKETING_NAME.name());
        } catch (IllegalArgumentException exc) {
            logger.logDebug("Parameters are not found in the query. Not an error, just sorting is not applied.");
        }
        query.setParameter("offerer", Long.valueOf(user.getOrganization().getKey()));
        query.setParameter("states", statesAsString);

        setPaginationParameters(pagination, query);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    List<Subscription> getSubscriptionsForVendor(PlatformUser user,
            Set<SubscriptionStatus> states, org.oscm.paginator.Pagination pagination,
            String queryString, Long... keys) {

        Set<String> statesAsString = getSubscriptionStatesAsString(states);
        Query query = dataManager.createNativeQuery(queryString, Subscription.class);
        try {
            query.setParameter("locale", user.getLocale());
            query.setParameter("objecttype", LocalizedObjectTypes.PRODUCT_MARKETING_NAME.name());
        } catch (IllegalArgumentException exc) {
            logger.logDebug("Parameters are not found in the query. Not an error, just sorting is not applied.");
        }
        query.setParameter("offerer", Long.valueOf(user.getOrganization().getKey()));
        query.setParameter("states", statesAsString);

        setPaginationParameters(pagination, query);
        setSubscriptionKeysParameter(query, keys);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    List<Subscription> getSubscriptionsForOwner(PlatformUser owner, Set<SubscriptionStatus> states,
            Pagination pagination, String queryString) {

        Set<String> statesAsString = getSubscriptionStatesAsString(states);
        Query query = dataManager.createNativeQuery(queryString, Subscription.class);
        try {
            query.setParameter("locale", owner.getLocale());
            query.setParameter("objecttype", LocalizedObjectTypes.PRODUCT_MARKETING_NAME.name());
        } catch (IllegalArgumentException exc) {
            logger.logDebug("Parameters are not found in the query. Not an error, just sorting is not applied.");
        }
        query.setParameter("ownerKey", Long.valueOf(owner.getKey()));
        query.setParameter("states", statesAsString);

        setPaginationParameters(pagination, query);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    List<Subscription> getSubscriptionsForOwner(PlatformUser owner, Pagination pagination, String queryString) {

        Query query = dataManager.createNativeQuery(queryString,
                Subscription.class);
        query.setParameter("ownerKey", Long.valueOf(owner.getKey()));

        setPaginationParameters(pagination, query);

        return query.getResultList();
    }

    /**
     * @param pagination
     * @param query
     */
    private void setPaginationParameters(org.oscm.paginator.Pagination pagination, Query query) {
        setSortingParameter(query, pagination);
        setFilterParameters(query, pagination);

        query.setFirstResult(pagination.getOffset());
        query.setMaxResults(pagination.getLimit());
    }

    private void setSortingParameter(Query query, org.oscm.paginator.Pagination pagination) {
        if (pagination.getSorting() != null) {
            query.setParameter("sortColumn", pagination.getSorting().getColumn().name());
        }
    }

    private void setFilterParameters(Query query, org.oscm.paginator.Pagination pagination) {
        if (pagination.getFilterSet() != null) {
            for (Filter filter : pagination.getFilterSet()) {
                setFilterParameter(query, filter);
            }
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
            query.setParameter("sortColumn", pagination.getSorting().getColumn().name());
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
        case CUSTOMER_NAME:
            query.setParameter("filterExpressionCustomerName",
                    filter.getExpression() + "%");
            break;
        case CUSTOMER_ID:
            query.setParameter("filterExpressionCustomerId",
                    filter.getExpression() + "%");
            break;
        case ACTIVATION_TIME:
            query.setParameter("filterExpressionActivation",
                    filter.getExpression() + "%");
            break;
        case SERVICE_ID:
            query.setParameter("filterExpressionServiceId",
                    filter.getExpression() + "%");
            break;
        case SERVICE_NAME:
            query.setParameter("filterExpressionServiceName",
                    filter.getExpression() + "%");
            break;
        case PURCHASE_ORDER_NUMBER:
            query.setParameter("filterExpressionReferenceNumber",
                    filter.getExpression() + "%");
            break;
        case STATUS:
            query.setParameter("filterExpressionStatus", filter.getExpression()
                    + "%");
            break;
        case UNIT:
            query.setParameter("filterExpressionUnit", filter.getExpression()
                    + "%");
            break;
        }
    }

    String getQuerySubscriptionsForMyCustomers(Pagination pagination) {

        String querySelect = "SELECT s.*"
        		+ " FROM subscription s JOIN product p ON s.product_tkey = p.tkey JOIN organization o ON p.vendorkey = o.tkey JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey ";
        String queryWhere = "WHERE o.tkey = :offerer AND s.status IN (:states) ";

        return paginatedQueryForMyCustomers(querySelect + queryWhere, pagination);

    }

    private String paginatedQueryForMyCustomers(String selectWhereQuery, Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createQueryOrderByStringForMyCustomers(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createQueryFilterStringForMyCustomers(pagination,
                    queryFilter, false);
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    private String paginatedQuery(String selectWhereQuery, Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createQueryOrderByString(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createQueryFilterString(pagination, queryFilter, true);
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    private String createQueryOrderByString(Pagination pagination) {
        String queryOrderBy;
        String whenSubscriptionId = "WHEN '"
                + TableColumns.SUBSCRIPTION_ID.name()
                + "' THEN s.subscriptionid ";
        String whenActivation = "WHEN '" + TableColumns.ACTIVATION_TIME.name()
                + "' THEN to_char(s.activationdate, '999999999999999') ";
        String whenCustomerId = "WHEN '" + TableColumns.CUSTOMER_ID.name()
                + "' THEN s.organizationid ";
        String whenCustomerName = "WHEN '" + TableColumns.CUSTOMER_NAME.name()
                + "' THEN s.name ";
        String whenServiceId = "WHEN '" + TableColumns.SERVICE_ID.name()
                + "' THEN s.productid ";
        String whenServiceName = "WHEN '" + TableColumns.SERVICE_NAME.name() + "' THEN  ("+ createLocalizedServiceNameSubQuery("s") + ") ";
        String whenReferenceNumber = "WHEN '" + TableColumns.PURCHASE_ORDER_NUMBER.name()
                + "' THEN s.purchaseordernumber ";
        String whenStatus = "WHEN '" + TableColumns.STATUS.name() + "' THEN s.status ";
        queryOrderBy = "ORDER BY (CASE :sortColumn " + whenSubscriptionId
                + whenActivation + whenCustomerId + whenCustomerName
 + whenServiceId + whenServiceName + whenReferenceNumber + whenStatus + " END) "
                + pagination.getSorting().getOrder().name();
        return queryOrderBy;
    }

    private String createQueryOrderByStringForMyCustomers(Pagination pagination) {
        String queryOrderBy;
        String whenSubscriptionId = "WHEN '"
                + TableColumns.SUBSCRIPTION_ID.name()
                + "' THEN s.subscriptionid ";
        String whenActivation = "WHEN '" + TableColumns.ACTIVATION_TIME.name()
                + "' THEN to_char(s.activationdate, '999999999999999') ";
        String whenCustomerId = "WHEN '" + TableColumns.CUSTOMER_ID.name()
                + "' THEN oCustomer.organizationid ";
        String whenCustomerName = "WHEN '" + TableColumns.CUSTOMER_NAME.name()
                + "' THEN oCustomer.name ";
        String whenServiceId = "WHEN '" + TableColumns.SERVICE_ID.name()
                + "' THEN p.productid ";
        String whenServiceName = "WHEN '" + TableColumns.SERVICE_NAME.name() + "' THEN  ("+ createLocalizedServiceNameSubQuery("p") + ") ";
        String whenReferenceNumber = "WHEN '" + TableColumns.PURCHASE_ORDER_NUMBER.name()
                + "' THEN s.purchaseordernumber ";
        String whenStatus = "WHEN '" + TableColumns.STATUS.name() + "' THEN s.status ";
        queryOrderBy = "ORDER BY (CASE :sortColumn " + whenSubscriptionId
                + whenActivation + whenCustomerId + whenCustomerName
 + whenServiceId + whenServiceName + whenReferenceNumber + whenStatus + " END) "
                + pagination.getSorting().getOrder().name();
        return queryOrderBy;
    }

    private String createQueryFilterString(Pagination pagination,
            String queryFilterParam, boolean isWhere) {
        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        StringBuilder queryFilterBuilder = new StringBuilder(queryFilterParam);
        boolean isWhereClause = isWhere;
        while (filterIterator.hasNext()) {
            if (isWhereClause) {
                queryFilterBuilder.append("WHERE ");
                isWhereClause = false;
            } else {
                queryFilterBuilder.append("AND ");
            }
            Filter filter = filterIterator.next();
            addFilterColumn(pagination, queryFilterBuilder, filter);
        }
        return queryFilterBuilder.toString();
    }

    private void addFilterColumn(Pagination pagination,
            StringBuilder queryFilterBuilder, Filter filter) {
        switch (filter.getColumn()) {
        case SUBSCRIPTION_ID:
            queryFilterBuilder
                    .append("s.subscriptionid ILIKE :filterExpressionSubscriptionId ");
            break;
        case CUSTOMER_NAME:
            queryFilterBuilder
                    .append("s.name ILIKE :filterExpressionCustomerName ");
            break;
        case CUSTOMER_ID:
            queryFilterBuilder
                    .append("s.organizationid ILIKE :filterExpressionCustomerId ");
            break;
        case ACTIVATION_TIME:
            queryFilterBuilder
                    .append("trim(to_char(to_timestamp(s.activationdate / 1000), '")
                    .append(pagination.getDateFormat())
                    .append("')) ILIKE :filterExpressionActivation ");
            break;
        case SERVICE_ID:
            queryFilterBuilder
                    .append("s.productid ILIKE :filterExpressionServiceId ");
            break;
        case SERVICE_NAME:
            queryFilterBuilder.append(" (")
                    .append(createLocalizedServiceNameSubQuery("s"))
                    .append(") ILIKE :filterExpressionServiceName ");
            break;
        case PURCHASE_ORDER_NUMBER:
            queryFilterBuilder
                    .append("s.purchaseordernumber ILIKE :filterExpressionReferenceNumber ");
            break;
        case STATUS:
            queryFilterBuilder
                    .append("s.status ILIKE :filterExpressionStatus ");
            break;
        case UNIT:
            queryFilterBuilder.append("ug.name ILIKE :filterExpressionUnit ");
            break;
        }
    }

    private String createQueryFilterStringForMyCustomers(Pagination pagination,
                                           String queryFilter, boolean isWhere) {
        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        boolean isWhereClause = isWhere;
        while (filterIterator.hasNext()) {
        	if (isWhereClause) {
        		queryFilter += "WHERE ";
        		isWhereClause = false;
        	} else {
        		queryFilter += "AND ";
        	}
            Filter filter = filterIterator.next();
            queryFilter = addFilterColumn4MyCustomers(pagination, queryFilter, filter);

        }
        return queryFilter;
    }

    private String addFilterColumn4MyCustomers(Pagination pagination, String queryFilter, Filter filter) {
        switch (filter.getColumn()) {
        case SUBSCRIPTION_ID:
            queryFilter += "s.subscriptionid ILIKE :filterExpressionSubscriptionId ";
            break;
        case CUSTOMER_NAME:
            queryFilter += "oCustomer.name ILIKE :filterExpressionCustomerName ";
            break;
        case CUSTOMER_ID:
            queryFilter += "oCustomer.organizationid ILIKE :filterExpressionCustomerId ";
            break;
        case ACTIVATION_TIME:
            queryFilter += "trim(to_char(to_timestamp(s.activationdate / 1000), '"
                + pagination.getDateFormat()
                    + "')) ILIKE :filterExpressionActivation ";
            break;
        case SERVICE_ID:
            queryFilter += "p.productid ILIKE :filterExpressionServiceId ";
            break;
        case SERVICE_NAME:
            queryFilter += " ("+ createLocalizedServiceNameSubQuery("p") +") ILIKE :filterExpressionServiceName ";
            break;
        case PURCHASE_ORDER_NUMBER:
            queryFilter += "s.purchaseordernumber ILIKE :filterExpressionReferenceNumber ";
            break;
        case STATUS:
            queryFilter += "s.status ILIKE :filterExpressionStatus ";
            break;
        }
        return queryFilter;
    }

    private String marketplacePaginatedQuery(String selectWhereQuery, Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createMarketplaceQueryOrderByString(pagination.getSorting().getOrder().name(), pagination.getLocalizedStatusesMap());
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createMarketplaceQueryFilterString(
                    queryFilter, pagination.getFilterSet(), pagination.getDateFormat(), pagination.getLocalizedStatusesMap());
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    private String marketplacePaginatedQuery(String selectWhereQuery, org.oscm.paginator.Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createMarketplaceQueryOrderByString(pagination.getSorting().getOrder().name(), pagination.getLocalizedStatusesMap());
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createMarketplaceQueryFilterString(
                    queryFilter, pagination.getFilterSet(), pagination.getDateFormat(), pagination.getLocalizedStatusesMap());
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    @Deprecated
    private String marketplacePaginatedQueryWithUnits(String selectWhereQuery, Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createMarketplaceQueryWithUnitsOrderByString(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createMarketplaceQueryWithUnitsFilterString(pagination, queryFilter);
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    private String marketplacePaginatedQueryWithUnits(String selectWhereQuery, org.oscm.paginator.Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createMarketplaceQueryWithUnitsOrderByString(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createMarketplaceQueryWithUnitsFilterString(pagination, queryFilter);
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    private String marketplacePaginatedQueryWithUnitsWithFiltering(String selectWhereQuery, org.oscm.paginator.Pagination pagination) {
        String queryOrderBy = "ORDER BY to_char(s.activationdate, '999999999999999') DESC ";
        if (pagination.getSorting() != null) {
            queryOrderBy = createMarketplaceQueryWithUnitsOrderByStringWithFiltering(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createMarketplaceQueryWithUnitsFilterStringWithFiltering(pagination, queryFilter);
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }

    @Deprecated
    private String createMarketplaceQueryWithUnitsOrderByString(Pagination pagination) {
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        String orderByName = pagination.getSorting().getOrder().name();

        String queryOrderBy = buildQueryOrderBy(localizedStatusesMap, orderByName);
        return queryOrderBy;
    }

    private String createMarketplaceQueryWithUnitsOrderByString(org.oscm.paginator.Pagination pagination) {
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        String orderByName = pagination.getSorting().getOrder().name();

        String queryOrderBy = buildQueryOrderBy(localizedStatusesMap, orderByName);
        return queryOrderBy;
    }

    private String buildQueryOrderBy(Map<SubscriptionStatus, String> localizedStatusesMap, String orderByName) {
        String queryOrderBy;
        String whenSubscriptionId = "WHEN '"
                + TableColumns.SUBSCRIPTION_ID.name()
                + "' THEN s.subscriptionid ";
        String whenActivation = "WHEN '" + TableColumns.ACTIVATION_TIME.name()
                + "' THEN to_char(s.activationdate, '999999999999999') ";
        String whenCustomerId = "WHEN '" + TableColumns.CUSTOMER_ID.name()
                + "' THEN oCustomer.organizationid ";
        String whenCustomerName = "WHEN '" + TableColumns.CUSTOMER_NAME.name()
                + "' THEN oCustomer.name ";
        String whenServiceId = "WHEN '" + TableColumns.SERVICE_ID.name()
                + "' THEN p.productid ";
        String whenServiceName = "WHEN '" + TableColumns.SERVICE_NAME.name()
                + "' THEN  ("
                + createLocalizedServiceNameSubQuery("p") + ") ";
        String whenReferenceNumber = "WHEN '"
                + TableColumns.PURCHASE_ORDER_NUMBER.name()
                + "' THEN s.purchaseordernumber ";
        String whenStatus = "WHEN '" + TableColumns.STATUS.name() + "' THEN "
                + createStatusesAndQueryPart(localizedStatusesMap) + " ";
        String whenUnit = "WHEN '" + TableColumns.UNIT.name()
                + "' THEN ug.name ";
        queryOrderBy = "ORDER BY (CASE :sortColumn " + whenSubscriptionId
                + whenActivation + whenCustomerId + whenCustomerName
                + whenServiceId + whenServiceName + whenReferenceNumber
                + whenStatus + whenUnit + " END) "
                + orderByName;
        return queryOrderBy;
    }

    private String createMarketplaceQueryWithUnitsOrderByStringWithFiltering(org.oscm.paginator.Pagination pagination) {
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        String orderByName = pagination.getSorting().getOrder().name();

        String queryOrderBy = buildQueryOrderBy(localizedStatusesMap, orderByName);
        return queryOrderBy;
    }

    private String createMarketplaceQueryOrderByString(String orderByColumnName, Map<SubscriptionStatus, String> localizedStatusesMap) {
        String queryOrderBy;
        String whenSubscriptionId = "WHEN '"
                + TableColumns.SUBSCRIPTION_ID.name()
                + "' THEN s.subscriptionid ";
        String whenActivation = "WHEN '" + TableColumns.ACTIVATION_TIME.name()
                + "' THEN to_char(s.activationdate, '999999999999999') ";
        String whenCustomerId = "WHEN '" + TableColumns.CUSTOMER_ID.name()
                + "' THEN oCustomer.organizationid ";
        String whenCustomerName = "WHEN '" + TableColumns.CUSTOMER_NAME.name()
                + "' THEN oCustomer.name ";
        String whenServiceId = "WHEN '" + TableColumns.SERVICE_ID.name()
                + "' THEN p.productid ";
        String whenServiceName = "WHEN '" + TableColumns.SERVICE_NAME.name() + "' THEN  ("+ createLocalizedServiceNameSubQuery(
                "p") + ") ";
        String whenReferenceNumber = "WHEN '" + TableColumns.PURCHASE_ORDER_NUMBER.name()
                + "' THEN s.purchaseordernumber ";
        String whenStatus = "WHEN '" + TableColumns.STATUS.name() + "' THEN "
                + createStatusesAndQueryPart(localizedStatusesMap) + " ";
        queryOrderBy = "ORDER BY (CASE :sortColumn " + whenSubscriptionId
                + whenActivation + whenCustomerId + whenCustomerName
                + whenServiceId + whenServiceName + whenReferenceNumber
                + whenStatus + " END) "
                + orderByColumnName;
        return queryOrderBy;
    }

    @Deprecated
    private String createMarketplaceQueryWithUnitsFilterString(
            Pagination pagination, String queryFilter) {
        String dateFormat = pagination.getDateFormat();
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        queryFilter = buildFilteredQueryWithUnits(queryFilter, dateFormat, localizedStatusesMap, filterIterator);
        return queryFilter;
    }

    private String createMarketplaceQueryWithUnitsFilterString(
            org.oscm.paginator.Pagination pagination, String queryFilter) {
        String dateFormat = pagination.getDateFormat();
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        queryFilter = buildFilteredQueryWithUnits(queryFilter, dateFormat, localizedStatusesMap, filterIterator);
        return queryFilter;
    }

    private String buildFilteredQueryWithUnits(String queryFilter, String dateFormat, Map<SubscriptionStatus, String> localizedStatusesMap, Iterator<Filter> filterIterator) {
        while (filterIterator.hasNext()) {
            Filter filter = filterIterator.next();
            switch (filter.getColumn()) {
            case SUBSCRIPTION_ID:
                queryFilter += "AND s.subscriptionid ILIKE :filterExpressionSubscriptionId ";
                break;
            case CUSTOMER_NAME:
                queryFilter += "AND oCustomer.name ILIKE :filterExpressionCustomerName ";
                break;
            case CUSTOMER_ID:
                queryFilter += "AND oCustomer.organizationid ILIKE :filterExpressionCustomerId ";
                break;
            case ACTIVATION_TIME:
                queryFilter += "AND trim(to_char(to_timestamp(s.activationdate / 1000), '"
                        + dateFormat
                        + "')) ILIKE :filterExpressionActivation ";
                break;
            case SERVICE_ID:
                queryFilter += "AND p.productid ILIKE :filterExpressionServiceId ";
                break;
            case SERVICE_NAME:
                queryFilter += "AND ("+ createLocalizedServiceNameSubQuery("p") +") ILIKE :filterExpressionServiceName ";
                break;
            case PURCHASE_ORDER_NUMBER:
                queryFilter += "AND s.purchaseordernumber ILIKE :filterExpressionReferenceNumber ";
                break;
            case STATUS:
                queryFilter += "AND " + createStatusesAndQueryPart(localizedStatusesMap)
                        + " ILIKE :filterExpressionStatus ";
                break;
            case UNIT:
                queryFilter += "AND ug.name ILIKE :filterExpressionUnit ";
            }

        }
        return queryFilter;
    }

    private String createMarketplaceQueryWithUnitsFilterStringWithFiltering(
            org.oscm.paginator.Pagination pagination, String queryFilter) {
        String dateFormat = pagination.getDateFormat();
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        buildFilteredQueryWithUnits(queryFilter, dateFormat, localizedStatusesMap, filterIterator);
        return queryFilter;
    }

    private String createMarketplaceQueryFilterString(String queryFilter, Set<Filter> filterSet, String dateFormat,
                                                      Map<SubscriptionStatus, String> localizedStatusesMap) {
        Iterator<Filter> filterIterator = filterSet.iterator();
        while (filterIterator.hasNext()) {
            Filter filter = filterIterator.next();
            switch (filter.getColumn()) {
                case SUBSCRIPTION_ID:
                    queryFilter += "AND s.subscriptionid ILIKE :filterExpressionSubscriptionId ";
                    break;
                case CUSTOMER_NAME:
                    queryFilter += "AND oCustomer.name ILIKE :filterExpressionCustomerName ";
                    break;
                case CUSTOMER_ID:
                    queryFilter += "AND oCustomer.organizationid ILIKE :filterExpressionCustomerId ";
                    break;
                case ACTIVATION_TIME:
                    queryFilter += "AND trim(to_char(to_timestamp(s.activationdate / 1000), '"
                            + dateFormat
                            + "')) ILIKE :filterExpressionActivation ";
                    break;
                case SERVICE_ID:
                    queryFilter += "AND p.productid ILIKE :filterExpressionServiceId ";
                    break;
                case SERVICE_NAME:
                    queryFilter += "AND ("
                            + createLocalizedServiceNameSubQuery("p")
                            + ") ILIKE :filterExpressionServiceName ";
                    break;
                case PURCHASE_ORDER_NUMBER:
                    queryFilter += "AND s.purchaseordernumber ILIKE :filterExpressionReferenceNumber ";
                    break;
                case STATUS:
                    queryFilter += "AND " + createStatusesAndQueryPart(localizedStatusesMap)
                            + " ILIKE :filterExpressionStatus ";
                    break;
            }
        }
        return queryFilter;
    }

    private String createLocalizedServiceNameSubQuery(String tableAlias) {
        return " SELECT localize.value FROM localizedresource localize WHERE localize.objectkey="+tableAlias+".template_tkey AND localize.locale=:locale AND localize.objecttype=:objecttype ";
    }

    private String createStatusesAndQueryPart(Map<SubscriptionStatus, String> localizedStatusesMap) {
        Set<Map.Entry<SubscriptionStatus, String>> statusesEntry = localizedStatusesMap.entrySet();
        String query = "case ";
        for(Map.Entry<SubscriptionStatus, String> entry : statusesEntry) {
            query += " when s.status='" + entry.getKey().name()
                    + "' then '" + entry.getValue() + "'";
        }
        query += " end";
        return query;
    }

    Set<String> getSubscriptionStatesAsString(Set<SubscriptionStatus> states) {
        Set<String> statesAsString = new HashSet<String>();
        for (SubscriptionStatus s : states) {
            statesAsString.add(s.name());
        }
        return statesAsString;
    }

    public List<Subscription> getSubscriptionsForMyBrokerCustomers(
            Organization org) {
        Query query = dataManager
                .createNamedQuery(
                        "Subscription.getSubscriptionsForMyBrokerCustomers");
        query.setParameter("offerer", org);
        return ParameterizedTypes.list(query.getResultList(),
                Subscription.class);
    }

    public List<Subscription> getSubscriptionsForMyBrokerCustomers(
            PlatformUser user, Set<SubscriptionStatus> states,
            Pagination pagination) {
        String queryString = getQuerySubscriptionsForMyBrokerCustomers(
                pagination);
        return getSubscriptionsForVendor(user, states, pagination, queryString);
    }

    String getQuerySubscriptionsForMyBrokerCustomers(Pagination pagination) {

        String querySelectSupplier = "SELECT * FROM (SELECT s.*, oCustomer.organizationid, oCustomer.name, p.productid, p.template_tkey FROM subscription s JOIN product p ON s.product_tkey = p.tkey JOIN organization o ON p.vendorkey = o.tkey JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey ";
        String queryWhereSupplier = "WHERE o.tkey = :offerer AND s.status IN (:states) UNION ";
        String querySupplier = querySelectSupplier + queryWhereSupplier;
        String querySelect = "SELECT  s.*, oCustomer.organizationid, oCustomer.name, p.productid, p.template_tkey FROM subscription s, product p, product pTemplate, product resaleCopyTemplate, organization o, organizationtorole otr, organizationrole oRole, organization oCustomer ";
        String queryWhere = "WHERE s.status IN (:states) AND s.product_tkey =  p.tkey "
                + "AND p.type = 'PARTNER_SUBSCRIPTION' "
                + "AND s.organizationkey = oCustomer.tkey "
                + "AND p.template_tkey = resaleCopyTemplate.tkey "
                + "AND resaleCopyTemplate.template_tkey = pTemplate.tkey "
                + "AND pTemplate.vendorkey = :offerer "
                + "AND p.vendorkey= o.tkey "
                + "AND otr.organization_tkey = o.tkey "
                + "AND otr.organizationrole_tkey = oRole.tkey "
                + "AND oRole.rolename= 'BROKER') AS s ";

        return paginatedQuery(querySupplier + querySelect + queryWhere,
                pagination);

    }


    public List<Subscription> getSubscriptionsByStatus(SubscriptionStatus status) {
        Query query = dataManager.createNamedQuery("Subscription.getByStatus");
        query.setParameter("status", status);
        return ParameterizedTypes.list(query.getResultList(),
                Subscription.class);
    }

    public Long hasCurrentUserSubscriptions(Long userKeyLong,
            List<SubscriptionStatus> status) {
        Query query = dataManager
                .createNamedQuery("Subscription.hasCurrentUserSubscriptions");
        query.setParameter("userKey", userKeyLong);
        query.setParameter("status", status);
        return (Long) query.getSingleResult();

    }

    public boolean checkIfProductInstanceIdExists(String productInstanceId,
            TechnicalProduct techProduct) {
        Query query = dataManager
                .createNamedQuery("Subscription.getByInstanceIdOfTechProd");
        query.setParameter("productInstanceId", productInstanceId);
        query.setParameter("technicalProduct", techProduct);
        return !query.getResultList().isEmpty();
    }

    public boolean isUsableSubscriptionsExistForTemplate(PlatformUser user,
            Set<SubscriptionStatus> states, Product template) {
        Query q = dataManager
                .createNamedQuery("Subscription.numberOfUsableSubscriptionsForUser");
        q.setParameter("userKey", Long.valueOf(user.getKey()));
        q.setParameter("status", states);
        q.setParameter("prodTemplate", template);
        long result = ((Long) q.getSingleResult()).longValue();
        return result > 0;
    }

    @Deprecated
    public List<Subscription> getSubscriptionsForOrg(PlatformUser user, Pagination pagination,
            Set<SubscriptionStatus> states) {
        String queryString = getQuerySubscriptionsForOrg(pagination);
        return getSubscriptionsForVendor(user, states, pagination, queryString);
    }

    public List<Subscription> getSubscriptionsForOrg(PlatformUser user, org.oscm.paginator.Pagination pagination,
            Set<SubscriptionStatus> states) {
        String queryString = getQuerySubscriptionsForOrg(pagination);
        return getSubscriptionsForVendor(user, states, pagination, queryString);
    }

    public List<Subscription> getSubscriptionsForOrgWithFiltering(PlatformUser user, org.oscm.paginator.Pagination pagination,
                                                                  Set<SubscriptionStatus> states, Collection<Long> subscriptionKeys) {
        String queryString = getQuerySubscriptionsForOrgWithFiltering(pagination);
        return getSubscriptionsForVendor(user, states, pagination, queryString, subscriptionKeys.toArray(new Long[subscriptionKeys.size()]));
    }


    public List<Subscription> getSubscriptionsForOwner(PlatformUser owner, Pagination pagination,
            Set<SubscriptionStatus> states) {

        String queryString = getQuerySubscriptionsForOwnerWithStates(pagination);
        return getSubscriptionsForOwner(owner, states, pagination, queryString);
    }

    @Deprecated
    public List<Subscription> getSubscriptionsForUserWithRoles(
            Set<UserRoleType> userRoleTypes, PlatformUser user,
            Pagination pagination, Set<SubscriptionStatus> states) {
        if (userRoleTypes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Query query = dataManager
                .createNativeQuery(
                        getSubscriptionsForUserWithRolesQuery(userRoleTypes,
                                pagination), Subscription.class);
        setSubscriptionsForUserWithRolesQueryParams(user, states, query);
        setPaginationParameters(pagination, query);
        return query.getResultList();
    }

    public List<Subscription> getSubscriptionsForUserWithRoles(
            Set<UserRoleType> userRoleTypes, PlatformUser user,
            org.oscm.paginator.Pagination pagination, Set<SubscriptionStatus> states) {
        if (userRoleTypes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Query query = dataManager
                .createNativeQuery(
                        getSubscriptionsForUserWithRolesQuery(userRoleTypes,
                                pagination), Subscription.class);
        setSubscriptionsForUserWithRolesQueryParams(user, states, query);
        setPaginationParameters(pagination, query);
        return query.getResultList();
    }

    private void setSubscriptionsForUserWithRolesQueryParams(PlatformUser user, Set<SubscriptionStatus> states, Query query) {
        setQueryParameter(query, "locale", user.getLocale());
        setQueryParameter(query, "objecttype",
                LocalizedObjectTypes.PRODUCT_MARKETING_NAME.name());
        setQueryParameter(query, "userKey", Long.valueOf(user.getKey()));
        setQueryParameter(query, "locale", user.getLocale());
        setQueryParameter(query, "states",
                getSubscriptionStatesAsString(states));
        setQueryParameter(query, "ownerKey", Long.valueOf(user.getKey()));
        setQueryParameter(query, "orgKey",
                Long.valueOf(user.getOrganization().getKey()));
    }

    public List<Subscription> getSubscriptionsForUserWithRolesWithFiltering(
            Set<UserRoleType> userRoleTypes, PlatformUser user,
            org.oscm.paginator.Pagination pagination, Set<SubscriptionStatus> states, Collection<Long> subscriptionKeys) {
        if (userRoleTypes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Query query = dataManager
                .createNativeQuery(
                        getSubscriptionsForUserWithRolesQueryWithFiltering(userRoleTypes,
                                pagination), Subscription.class);
        setSubscriptionsForUserWithRolesQueryParams(user, states, query);
        setPaginationParameters(pagination, query);
        setSubscriptionKeysParameter(query, subscriptionKeys.toArray(new Long[subscriptionKeys.size()]));
        return query.getResultList();
    }

    @Deprecated
    private String getSubscriptionsForUserWithRolesQuery(
            Set<UserRoleType> userRoleTypes, Pagination pagination) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ((");
        boolean isAdded;
        for (Iterator<UserRoleType> i = userRoleTypes.iterator(); i.hasNext();) {
            UserRoleType userRoleType = i.next();
            isAdded = false;
            if (UserRoleType.UNIT_ADMINISTRATOR.equals(userRoleType)) {
                queryBuilder.append(getQueryForUnitAdmin());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterString(
                                pagination, " "));

                // Bug 11958 allow subscr owner to manage his subscription
                queryBuilder.append(" ) UNION ( ");
                queryBuilder.append(getQueryForSubOwner());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterString(
                                pagination, " "));
                isAdded = true;
            }
            if (UserRoleType.SUBSCRIPTION_MANAGER.equals(userRoleType)) {
                queryBuilder.append(getQueryForSubOwner());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterString(
                                pagination, " "));
                isAdded = true;
            }
            if (isAdded && i.hasNext()) {
                queryBuilder.append(" ) UNION ( ");
            }
        }

        queryBuilder.append(" )) AS s ");
        queryBuilder.append(getOrderBy(pagination));
        return queryBuilder.toString();
    }

    private String getSubscriptionsForUserWithRolesQuery(
            Set<UserRoleType> userRoleTypes, org.oscm.paginator.Pagination pagination) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ((");
        boolean isAdded;
        for (Iterator<UserRoleType> i = userRoleTypes.iterator(); i.hasNext();) {
            UserRoleType userRoleType = i.next();
            isAdded = false;
            if (UserRoleType.UNIT_ADMINISTRATOR.equals(userRoleType)) {
                queryBuilder.append(getQueryForUnitAdmin());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterString(
                                pagination, " "));

                // Bug 11958 allow subscr owner to manage his subscription
                queryBuilder.append(" ) UNION ( ");
                queryBuilder.append(getQueryForSubOwner());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterString(
                                pagination, " "));
                isAdded = true;
            }
            if (UserRoleType.SUBSCRIPTION_MANAGER.equals(userRoleType)) {
                queryBuilder.append(getQueryForSubOwner());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterString(
                                pagination, " "));
                isAdded = true;
            }
            if (isAdded && i.hasNext()) {
                queryBuilder.append(" ) UNION ( ");
            }
        }

        queryBuilder.append(" )) AS s ");
        queryBuilder.append(getOrderBy(pagination));
        return queryBuilder.toString();
    }

    private String getSubscriptionsForUserWithRolesQueryWithFiltering(
            Set<UserRoleType> userRoleTypes, org.oscm.paginator.Pagination pagination) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ((");
        boolean isAdded;
        for (Iterator<UserRoleType> i = userRoleTypes.iterator(); i.hasNext();) {
            UserRoleType userRoleType = i.next();
            isAdded = false;
            if (UserRoleType.UNIT_ADMINISTRATOR.equals(userRoleType)) {
                queryBuilder.append(getQueryForUnitAdmin());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterStringWithFiltering(
                                pagination, " "));

                // Bug 11958 allow subscr owner to manage his subscription
                queryBuilder.append(" ) UNION ( ");
                queryBuilder.append(getQueryForSubOwner());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterStringWithFiltering(
                                pagination, " "));
                isAdded = true;
            }
            if (UserRoleType.SUBSCRIPTION_MANAGER.equals(userRoleType)) {
                queryBuilder.append(getQueryForSubOwner());
                queryBuilder
                        .append(createMarketplaceQueryWithUnitsFilterStringWithFiltering(
                                pagination, " "));
                isAdded = true;
            }
            if (isAdded && i.hasNext()) {
                queryBuilder.append(" ) UNION ( ");
            }
        }

        queryBuilder.append(" )) AS s ");
        queryBuilder.append(getOrderBy(pagination));
        return queryBuilder.toString();
    }

    @Deprecated
    private String getOrderBy(Pagination pagination) {
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        StringBuilder orderByBuilder = new StringBuilder();
        getOrderByQueryWithUnit(pagination.getSorting(), localizedStatusesMap, orderByBuilder);
        return orderByBuilder.toString();
    }

    private String getOrderBy(org.oscm.paginator.Pagination pagination) {
        Map<SubscriptionStatus, String> localizedStatusesMap = pagination.getLocalizedStatusesMap();
        StringBuilder orderByBuilder = new StringBuilder();
        getOrderByQueryWithUnit(pagination.getSorting(), localizedStatusesMap, orderByBuilder);
        return orderByBuilder.toString();
    }

    private void getOrderByQueryWithUnit(Sorting sorting, Map<SubscriptionStatus, String> localizedStatusesMap, StringBuilder orderByBuilder) {
        if (sorting != null) {
            orderByBuilder.append("ORDER BY (CASE :sortColumn ");
            orderByBuilder.append("WHEN '")
                    .append(TableColumns.SUBSCRIPTION_ID.name())
                    .append("' THEN s.subscriptionid ");
            orderByBuilder
                    .append("WHEN '")
                    .append(TableColumns.ACTIVATION_TIME.name())
                    .append("' THEN to_char(s.activationdate, '999999999999999') ");
            orderByBuilder.append("WHEN '")
                    .append(TableColumns.CUSTOMER_ID.name())
                    .append("' THEN s.customer_org ");
            orderByBuilder.append("WHEN '")
                    .append(TableColumns.CUSTOMER_NAME.name())
                    .append("' THEN s.customer_name ");
            orderByBuilder.append("WHEN '")
                    .append(TableColumns.SERVICE_ID.name())
                    .append("' THEN s.productid ");
            orderByBuilder.append("WHEN '")
                    .append(TableColumns.SERVICE_NAME.name())
                    .append("' THEN  (")
                    .append(createLocalizedServiceNameSubQuery("s"))
                    .append(") ");
            orderByBuilder.append("WHEN '")
                    .append(TableColumns.PURCHASE_ORDER_NUMBER.name())
                    .append("' THEN s.purchaseordernumber ");
            orderByBuilder.append("WHEN '").append(TableColumns.STATUS.name())
                    .append("' THEN ")
                    .append(createStatusesAndQueryPart(localizedStatusesMap)).append(" ");
            orderByBuilder.append("WHEN '").append(TableColumns.UNIT.name())
                    .append("' THEN s.unit_name ");
            orderByBuilder.append(" END) ").append(
                    sorting.getOrder().name());
        }
    }

    private void setQueryParameter(Query query, String parameter, Object value) {
        try {
            query.setParameter(parameter, value);
        } catch (IllegalArgumentException exc) {
            logger.logDebug("Parameter " + parameter
                    + "is not found in the query");
        }
    }

    private String getQueryForUnitAdmin() {
        return "SELECT s.*, oCustomer.organizationid as customer_org, oCustomer.name as customer_name, p.productid, p.template_tkey, ug.name as unit_name "
                + "FROM Subscription s "
                + "LEFT JOIN product p ON (s.product_tkey = p.tkey) "
                + "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey "
                + "LEFT JOIN usergroup ug ON ug.tkey = s.usergroup_tkey "
                + "LEFT JOIN usergrouptouser ugtu ON ugtu.usergroup_tkey = ug.tkey "
                + "LEFT JOIN unitroleassignment ura ON ura.usergrouptouser_tkey = ugtu.tkey "
                + "WHERE ura.unituserrole_tkey = '"
                + UnitRoleType.ADMINISTRATOR.getKey()
                + "' AND ugtu.platformuser_tkey = :userKey AND s.status IN (:states) ";
    }

    private String getQueryForSubOwner() {
        return "SELECT s.*, oCustomer.organizationid as customer_org, oCustomer.name as customer_name, p.productid, p.template_tkey, ug.name as unit_name "
                + "FROM Subscription s "
                + "LEFT JOIN product p ON (s.product_tkey = p.tkey) "
                + "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey "
                + "LEFT JOIN usergroup ug ON ug.tkey = s.usergroup_tkey "
                + "WHERE s.status IN (:states) AND s.owner_tkey=:ownerKey ";
    }

    public List<Subscription> getSubscriptionsForOwner(PlatformUser owner, Pagination pagination) {
        String queryString = getQuerySubscriptionsForOwner(pagination);
        return getSubscriptionsForOwner(owner, pagination, queryString);
    }

    @Deprecated
    private String getQuerySubscriptionsForOrg(Pagination pagination) {
        return marketplacePaginatedQueryWithUnits(
                "SELECT s.* "
                        + "FROM Subscription s "
                        + "LEFT JOIN product p ON (s.product_tkey = p.tkey) "
                        + "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey "
                        + "LEFT JOIN usergroup ug ON ug.tkey = s.usergroup_tkey "
                        + "WHERE s.status IN (:states) AND s.organizationkey=:offerer ",
                pagination);
    }

    private String getQuerySubscriptionsForOrg(org.oscm.paginator.Pagination pagination) {
        return marketplacePaginatedQueryWithUnits(
                "SELECT s.* "
                        + "FROM Subscription s "
                        + "LEFT JOIN product p ON (s.product_tkey = p.tkey) "
                        + "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey "
                        + "LEFT JOIN usergroup ug ON ug.tkey = s.usergroup_tkey "
                        + "WHERE s.status IN (:states) AND s.organizationkey=:offerer ",
                pagination);
    }

    private String getQuerySubscriptionsForOrgWithFiltering(org.oscm.paginator.Pagination pagination) {
        return marketplacePaginatedQueryWithUnitsWithFiltering(
                "SELECT s.* "
                        + "FROM Subscription s "
                        + "LEFT JOIN product p ON (s.product_tkey = p.tkey) "
                        + "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey "
                        + "LEFT JOIN usergroup ug ON ug.tkey = s.usergroup_tkey "
                        + "WHERE s.status IN (:states) AND s.organizationkey=:offerer " +
                        "AND s.tkey IN (:keys) ",
                pagination);
    }

    private String getQuerySubscriptionsForOwnerWithStates(Pagination pagination) {
        return marketplacePaginatedQueryWithUnits(
                "SELECT s.* "
                        + "FROM Subscription s "
                        + "LEFT JOIN product p ON (s.product_tkey = p.tkey) "
                        + "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey "
                        + "LEFT JOIN usergroup ug ON ug.tkey = s.usergroup_tkey "
                        + "WHERE s.status IN (:states) AND s.owner_tkey=:ownerKey ",
                pagination);
    }

    private String getQuerySubscriptionsForOwner(Pagination pagination) {
        return marketplacePaginatedQuery(
                "SELECT s.*"
                + " FROM Subscription s LEFT JOIN product p ON (s.product_tkey = p.tkey) LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey WHERE s.owner_tkey=:ownerKey ",
                pagination);
    }

    @Deprecated
    public List<Subscription> getSubscriptionsForUser(PlatformUser user, Pagination pagination) {
        String queryString = getQuerySubscriptionsForUser(pagination);
        return getSubscriptionsForUser(user, pagination, queryString);
    }

    public List<Subscription> getSubscriptionsForUser(PlatformUser user, org.oscm.paginator.Pagination pagination) {
        String queryString = getQuerySubscriptionsForUser(pagination);
        return getSubscriptionsForUser(user, pagination, queryString);
    }

    public List<Subscription> getSubscriptionsForUserWithSubscriptionKeys(PlatformUser user, org.oscm.paginator.Pagination pagination,
                                                      Collection<Long> subscriptionKeys) {
        String queryString = getQuerySubscriptionsForUserWithKeys(pagination);
        return getSubscriptionsForUser(user, pagination, queryString, subscriptionKeys.toArray(new Long[subscriptionKeys.size()]));
    }

    private String getQuerySubscriptionsForUser(Pagination pagination) {
        return marketplacePaginatedQuery(
                "SELECT s.*"
                        + " FROM Subscription s LEFT JOIN product p ON (s.product_tkey = p.tkey) LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey WHERE s.status IN (:status) AND EXISTS (SELECT 1 FROM UsageLicense lic WHERE lic.user_tkey = :userKey AND lic.subscription_tkey = s.tkey) ",
                pagination);
    }

    private String getQuerySubscriptionsForUser(org.oscm.paginator.Pagination pagination) {
        return marketplacePaginatedQuery(
                "SELECT s.*"
                        + " FROM Subscription s LEFT JOIN product p ON (s.product_tkey = p.tkey) LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey WHERE s.status IN (:status) AND EXISTS (SELECT 1 FROM UsageLicense lic WHERE lic.user_tkey = :userKey AND lic.subscription_tkey = s.tkey) ",
                pagination);
    }

    private String getQuerySubscriptionsForUserWithKeys(org.oscm.paginator.Pagination pagination) {
        return marketplacePaginatedQuery(
                "SELECT s.* "
                        + "FROM Subscription s " +
                        "LEFT JOIN product p ON (s.product_tkey = p.tkey) " +
                        "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey " +
                        "WHERE s.tkey IN (:keys) " +
                        "AND s.status IN (:status) " +
                        "AND EXISTS (SELECT 1 FROM UsageLicense lic WHERE lic.user_tkey = :userKey " +
                        "AND lic.subscription_tkey = s.tkey) ",
                pagination);
    }

    private List<Subscription> getSubscriptionsForUser(PlatformUser user, Pagination pagination, String queryString) {

        Query query = getSubscriptionsForUserNativeQuery(user, queryString);

        setPaginationParameters(pagination, query);

        return ParameterizedTypes.list(query.getResultList(), Subscription.class);
    }

    private Query getSubscriptionsForUserNativeQuery(PlatformUser user, String queryString) {
        Query query = dataManager.createNativeQuery(queryString, Subscription.class);
        try {
            query.setParameter("locale", user.getLocale());
            query.setParameter("objecttype", LocalizedObjectTypes.PRODUCT_MARKETING_NAME.name());
        } catch (IllegalArgumentException exc) {
            logger.logDebug("Parameters are not found in the query. Not an error, just sorting is not applied.");
        }
        query.setParameter("userKey", Long.valueOf(user.getKey()));
        query.setParameter("status", getSubscriptionStatesAsString(new HashSet<>(Subscription.VISIBLE_SUBSCRIPTION_STATUS)));
        return query;
    }

    private List<Subscription> getSubscriptionsForUser(PlatformUser user, org.oscm.paginator.Pagination pagination,
                                                       String queryString, Long... subscriptionKeys) {

        Query query = getSubscriptionsForUserNativeQuery(user, queryString);

        setPaginationParameters(pagination, query);
        setSubscriptionKeysParameter(query, subscriptionKeys);
        return ParameterizedTypes.list(query.getResultList(), Subscription.class);
    }

    private void setSubscriptionKeysParameter(Query query, Long... subscriptionKeys) {
        if (subscriptionKeys != null && subscriptionKeys.length > 0) {
            Set<BigInteger> subscriptionKeysStrings = new HashSet<>();
            for (Long subscriptionKey : subscriptionKeys) {
                subscriptionKeysStrings.add(BigInteger.valueOf(subscriptionKey));
            }
            query.setParameter("keys", subscriptionKeysStrings);
        }
    }

    public UsageLicense getUserLicense(PlatformUser user, long subKey) {

    	Query query = dataManager.createNamedQuery("Subscription.findUsageLicense");

        query.setParameter("userId", user.getUserId());
        query.setParameter("subscriptionKey", Long.valueOf(subKey));

        List<UsageLicense> result = ParameterizedTypes.list(query.getResultList(), UsageLicense.class);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<RoleDefinition> getSubscriptionRoles(Organization owner, String subId) {
        Query q = dataManager
                .createNamedQuery("Subscription.getSubRoles");
        q.setParameter("orgKey", Long.valueOf(owner.getKey()));
        q.setParameter("subId", subId);
        return ParameterizedTypes.list(q.getResultList(), RoleDefinition.class);
    }

    public Subscription getMySubscriptionDetails(long key) {
        PlatformUser user = dataManager.getCurrentUser();
        Query query = dataManager.createNativeQuery("SELECT s.*"
                + " FROM Subscription s " +
                "LEFT JOIN product p ON (s.product_tkey = p.tkey) " +
                        "LEFT JOIN organization oCustomer ON s.organizationkey = oCustomer.tkey " +
                "WHERE s.tkey=:subKey and s.status IN (:status) AND EXISTS " +
                "(SELECT 1 FROM UsageLicense lic WHERE lic.user_tkey=:userKey AND lic.subscription_tkey=:subKey)",
                Subscription.class);
        query.setParameter("userKey", Long.valueOf(user.getKey()));
        query.setParameter("subKey", Long.valueOf(key));
        query.setParameter("status", getSubscriptionStatesAsString(new HashSet<>(Subscription.VISIBLE_SUBSCRIPTION_STATUS)));
        return (Subscription) query.getSingleResult();
    }
}
