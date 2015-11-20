package org.oscm.usergroupservice.dao;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.PlatformUser;
import org.oscm.internal.types.enumtypes.UnitRoleType;
import org.oscm.pagination.Filter;
import org.oscm.pagination.Pagination;
import org.oscm.pagination.TableColumns;

@Stateless
@LocalBean
public class UserGroupUsersDao {

    @EJB(beanInterface = DataService.class)
    DataService dm;

    public List<PlatformUser> executeQueryGroupUsers(
            Pagination pagination, String userGroupKey) {
        String nativeQuery = getQueryGroupUsers(pagination);
        Query query = dm.createNativeQuery(nativeQuery, PlatformUser.class);
        query.setParameter("organization_key",
                Long.valueOf(dm.getCurrentUser().getOrganization().getKey()));
        if (userGroupKey == null || userGroupKey.trim().equals("")) {
            userGroupKey = "0";
        }
        query.setParameter("userGroup_key", Long.valueOf(userGroupKey));

        setPaginationParameters(pagination, query);
        return query.getResultList();
    }
    
    public Long executeQueryCountGroupUsers(
            Pagination pagination, String userGroupKey) {
        String nativeQuery = getQueryCountGroupUsers(pagination);
        Query query = dm.createNativeQuery(nativeQuery);
        query.setParameter("organization_key",
                Long.valueOf(dm.getCurrentUser().getOrganization().getKey()));
        if (userGroupKey == null || userGroupKey.trim().equals("")) {
            userGroupKey = "0";
        }
        query.setParameter("userGroup_key", Long.valueOf(userGroupKey));

        setPaginationParameters(pagination, query);
        BigInteger bi = (BigInteger) query.getSingleResult();
        return bi.longValue();
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
        case USER_ID:
            query.setParameter("filterExpressionUserId",
                    filter.getExpression() + "%");
            break;
        case FIRST_NAME:
            query.setParameter("filterExpressionFirstName",
                    filter.getExpression() + "%");
            break;
        case LAST_NAME:
            query.setParameter("filterExpressionLastName",
                    filter.getExpression() + "%");
            break;
        case ROLE_IN_UNIT:
            query.setParameter("filterExpressionRoleInUnit",
                    filter.getExpression() + "%");
            break;
        default:
            break;
        }
    }

    private String getQueryGroupUsers(Pagination pagination) {
        String querySelect = " SELECT * FROM (SELECT pu.*,"
                           + "                       uur.rolename, "
                           + "                       1 as assigned "
                           + "                FROM platformuser pu, "
                           + "                     usergrouptouser ugtu, "
                           + "                     unitroleassignment ura, "
                           + "                     unituserrole uur "
                           + "                WHERE NOT EXISTS (SELECT 1 "
                           + "                                  FROM onbehalfuserreference ref "
                           + "                                  WHERE ref.slaveUser_tkey = pu.tkey) "
                           + "                AND ugtu.platformuser_tkey = pu.tkey "
                           + "                AND ura.usergrouptouser_tkey = ugtu.tkey "
                           + "                AND uur.tkey = ura.unituserrole_tkey "
                           + "                AND ugtu.usergroup_tkey = :userGroup_key "
                           + "                UNION "
                           + "                SELECT pu.*, "
                           + "                       '', "
                           + "                       0 as assigned "
                           + "                FROM platformuser pu "
                           + "                WHERE NOT EXISTS (SELECT 1 "
                           + "                                  FROM usergrouptouser ugtu1 "
                           + "                                  WHERE ugtu1.platformuser_tkey = pu.tkey "
                           + "                                  AND ugtu1.usergroup_tkey = :userGroup_key)) AS unituser "
                           + " WHERE unituser.organizationkey = :organization_key ";
        return paginatedQueryForUsers(querySelect, pagination);
    }

    private String getQueryCountGroupUsers(Pagination pagination) {
        String querySelect = " SELECT count(*) FROM (SELECT pu.*,"
                           + "                       uur.rolename, "
                           + "                       1 as assigned "
                           + "                FROM platformuser pu, "
                           + "                     usergrouptouser ugtu, "
                           + "                     unitroleassignment ura, "
                           + "                     unituserrole uur "
                           + "                WHERE NOT EXISTS (SELECT 1 "
                           + "                                  FROM onbehalfuserreference ref "
                           + "                                  WHERE ref.slaveUser_tkey = pu.tkey) "
                           + "                AND ugtu.platformuser_tkey = pu.tkey "
                           + "                AND ura.usergrouptouser_tkey = ugtu.tkey "
                           + "                AND uur.tkey = ura.unituserrole_tkey "
                           + "                AND ugtu.usergroup_tkey = :userGroup_key "
                           + "                UNION "
                           + "                SELECT pu.*, "
                           + "                       '', "
                           + "                       0 as assigned "
                           + "                FROM platformuser pu "
                           + "                WHERE NOT EXISTS (SELECT 1 "
                           + "                                  FROM usergrouptouser ugtu1 "
                           + "                                  WHERE ugtu1.platformuser_tkey = pu.tkey "
                           + "                                  AND ugtu1.usergroup_tkey = :userGroup_key)) AS unituser "
                           + " WHERE unituser.organizationkey = :organization_key ";
        return paginatedQueryForCountUsers(querySelect, pagination);
    }
    
    private String paginatedQueryForUsers(String selectWhereQuery, Pagination pagination) {
        String queryOrderBy = " ORDER BY unituser.assigned DESC, unituser.userid ASC";
        if (pagination.getSorting() != null) {
            queryOrderBy = createQueryOrderBy(pagination);
        }

        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createQueryFilter(pagination, queryFilter);
        }
        return selectWhereQuery + queryFilter + queryOrderBy;
    }
    
    private String paginatedQueryForCountUsers(String selectWhereQuery, Pagination pagination) {
        String queryFilter = "";
        if (pagination.getFilterSet() != null) {
            queryFilter = createQueryFilter(pagination, queryFilter);
        }
        return selectWhereQuery + queryFilter;
    }

    private String createQueryFilter(Pagination pagination,
            String queryFilter) {
        Iterator<Filter> filterIterator = pagination.getFilterSet().iterator();
        StringBuilder queryFilterBuilder = new StringBuilder(queryFilter);
        while (filterIterator.hasNext()) {
            queryFilterBuilder.append(" AND ");
            Filter filter = filterIterator.next();
            addFilterColumn(pagination, queryFilterBuilder, filter);
        }
        return queryFilterBuilder.toString();
    }
    
    private void addFilterColumn(Pagination pagination,
            StringBuilder queryFilterBuilder, Filter filter) {
        switch (filter.getColumn()) {
        case USER_ID:
            queryFilterBuilder
                    .append(" unituser.userid ILIKE :filterExpressionUserId");
            break;
        case FIRST_NAME:
            queryFilterBuilder
                    .append(" unituser.firstname ILIKE :filterExpressionFirstName");
            break;
        case LAST_NAME:
            queryFilterBuilder
                    .append(" unituser.lastname ILIKE :filterExpressionLastName");
            break;
        case ROLE_IN_UNIT:
            queryFilterBuilder.append(createRolesAndQueryPart(pagination)
                    + " ILIKE :filterExpressionRoleInUnit ");
        default:
            break;
        }
    }

    private String createQueryOrderBy(Pagination pagination) {
        String queryOrderBy;
        String whenUserId = "WHEN '" + TableColumns.USER_ID.name()
                + "' THEN unituser.userid ";
        String whenFirstName = "WHEN '" + TableColumns.FIRST_NAME.name()
                + "' THEN unituser.firstname ";
        String whenLastName = "WHEN '" + TableColumns.LAST_NAME.name()
                + "' THEN unituser.lastname ";
        String whenRoleInUnit = "WHEN '" + TableColumns.ROLE_IN_UNIT.name()
                + "' THEN " + createRolesAndQueryPart(pagination);

        queryOrderBy = " ORDER BY (CASE :sortColumn " + whenUserId
                + whenFirstName + whenLastName + whenRoleInUnit + " END) "
                + pagination.getSorting().getOrder().name();
        return queryOrderBy;
    }
    
    private String createRolesAndQueryPart(Pagination pagination) {
        Set<Map.Entry<UnitRoleType, String>> statusesEntry = pagination.getLocalizedRolesMap().entrySet();
        String query = "case ";
        for(Map.Entry<UnitRoleType, String> entry : statusesEntry) {
            query += " when unituser.rolename='" + entry.getKey().name()
                    + "' then '" + entry.getValue() + "'";
        }
        query += " end";
        return query;
    }
    
}
