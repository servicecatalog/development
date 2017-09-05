/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.dataservice.bean;

import java.util.List;

import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.oscm.domobjects.DomainHistoryObject;
import org.oscm.domobjects.DomainObject;
import org.oscm.domobjects.DomainObjectWithVersioning;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;

/**
 * Hibernate specific listener implementation to catch insert, modification and
 * delete events for history object creation and index updates.
 * 
 * @author hoffmann
 */
public class HibernateEventListener implements PostUpdateEventListener,
    PostInsertEventListener, PostDeleteEventListener {

    private static final long serialVersionUID = -843967013822084583L;

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(HibernateEventListener.class);

    private Indexer indexer = new Indexer();

    public HibernateEventListener() {
    }

    public void onPostInsert(PostInsertEvent event) {
        createHistory(event.getPersister(), event.getEntity(),
                ModificationType.ADD);
        indexer.handleIndexing((DomainObject<?>) event.getEntity(), ModificationType.ADD, event.getPersister());
    }

    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof DomainObjectWithVersioning<?>) {
            final int i = getVersionColumn(event);
            if (((Integer) event.getOldState()[i]).intValue() < ((Number) event
                    .getState()[i]).intValue()) {
                createHistory(event.getPersister(), event.getEntity(),
                        ModificationType.MODIFY);
                indexer.handleIndexing((DomainObject<?>) event.getEntity(),
                        ModificationType.MODIFY, event.getPersister());
            }
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    private int getVersionColumn(PostUpdateEvent event) {
        int i = 0;
        for (; i < event.getState().length; i++) {
            if (event.getState()[i] instanceof Integer
                    && ((Integer) event.getState()[i]).intValue() == ((DomainObject<?>) event
                            .getEntity()).getVersion())
                return i;
        }
        throw new SaaSSystemException(
                "Field 'version' not found for DomainObject "
                        + event.getEntity().getClass().getName() + ", key: "
                        + ((DomainObject<?>) event.getEntity()).getKey()
                        + ", version: "
                        + ((DomainObject<?>) event.getEntity()).getVersion());
    }

    public void onPostDelete(PostDeleteEvent event) {
        removeLocalization(event.getPersister(), event.getEntity());
        createHistory(event.getPersister(), event.getEntity(),
                ModificationType.DELETE);
        indexer.handleIndexing((DomainObject<?>) event.getEntity(), ModificationType.DELETE, event.getPersister());
    }

    private void removeLocalization(EntityPersister persister, Object entity) {
        if (entity instanceof DomainObject<?>) {
            DomainObject<?> obj = (DomainObject<?>) entity;
            List<LocalizedObjectTypes> objType = obj.getLocalizedObjectTypes();
            if (objType.size() > 0) {
                long key = obj.getKey();
                final StatelessSession session = persister.getFactory()
                        .openStatelessSession();
                Transaction tx = session.beginTransaction();
                org.hibernate.Query query = session
                        .createQuery("DELETE FROM LocalizedResource WHERE objectKey = :objectKey AND objectType IN (:objectType)");
                query.setParameter("objectKey", key);
                query.setParameterList("objectType", objType);
                query.executeUpdate();
                tx.commit();
                session.close();
            }
        }
    }

    private void createHistory(EntityPersister persister, Object entity,
            ModificationType type) {
        if (entity instanceof DomainObject<?>) {
            DomainObject<?> obj = (DomainObject<?>) entity;
            if (obj.hasHistory()) {
                final DomainHistoryObject<?> hist = HistoryObjectFactory
                        .create(obj, type,
                                DataServiceBean.getCurrentHistoryUser());

                final StatelessSession session = persister.getFactory()
                        .openStatelessSession();
                Transaction tx = session.beginTransaction();
                session.insert(hist);
                tx.commit();
                session.close();

                if (logger.isDebugLoggingEnabled()) {
                    logger.logDebug(String.format("%s %s[%s, v=%s]", type, obj
                            .getClass().getSimpleName(), obj
                            .getKey(), hist.getObjVersion()));
                }
            }
        }
    }
}
