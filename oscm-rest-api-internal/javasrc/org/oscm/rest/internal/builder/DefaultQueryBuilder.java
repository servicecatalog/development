package org.oscm.rest.internal.builder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.oscm.domobjects.PlatformUser;
import org.oscm.rest.internal.QueryBuilder;

public class DefaultQueryBuilder implements QueryBuilder {

    @Override
    public <T> CriteriaQuery<T> getListCriteria(PlatformUser u, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root) {
        // the default behavior is to read nothing thus checking the key for
        // being null
        return cq.where(root.get("key").isNull());
    }

}
