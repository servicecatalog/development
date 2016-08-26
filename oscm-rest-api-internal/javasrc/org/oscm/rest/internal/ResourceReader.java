package org.oscm.rest.internal;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.oscm.domobjects.DomainObject;
import org.oscm.domobjects.PlatformUser;

@LocalBean
@Stateless
public class ResourceReader {

    static final int DEFAULT_OFFSET = 0;
    static final int DEFAULT_LIMIT = 25;

    @PersistenceContext(name = "persistence/em", unitName = "oscm-domainobjects")
    protected EntityManager em;

    <T extends DomainObject<?>> List<T> getList(PlatformUser u, Class<T> type, int offset, int limit)
            throws InstantiationException, IllegalAccessException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> root = cq.from(type);

        cq = QueryBuilderProvider.getFor(type).getListCriteria(u, cb, cq, root);

        TypedQuery<T> tq = em.createQuery(cq);
        tq.setMaxResults(limit);
        tq.setFirstResult(offset);
        return tq.getResultList();
    }
}
