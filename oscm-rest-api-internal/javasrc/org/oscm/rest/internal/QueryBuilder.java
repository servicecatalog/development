package org.oscm.rest.internal;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.oscm.domobjects.PlatformUser;

public interface QueryBuilder {

    public <T> CriteriaQuery<T> getListCriteria(PlatformUser u, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root);

}
