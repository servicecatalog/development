package org.oscm.rest.internal.builder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.oscm.domobjects.PlatformUser;
import org.oscm.rest.internal.QueryBuilder;

public class OrganizationQueryBuilder implements QueryBuilder {

    @Override
    public <T> CriteriaQuery<T> getListCriteria(PlatformUser u, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root) {
        // TODO Auto-generated method stub
        return null;
    }

}
