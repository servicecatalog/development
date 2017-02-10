/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.dataservice.bean;

import java.util.List;

import org.hibernate.StatelessSession;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.domobjects.DomainHistoryObject;
import org.oscm.domobjects.DomainObject;
import org.oscm.domobjects.DomainObjectWithVersioning;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.internal.types.exception.SaaSSystemException;

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

    private IndexMQSender messageSender;

    public HibernateEventListener() {
        this.messageSender = new IndexMQSender();
    }

    public void onPostInsert(PostInsertEvent event) {
        createHistory(event.getPersister(), event.getEntity(),
                ModificationType.ADD);
        messageSender.notifyIndexer(event.getEntity(), ModificationType.ADD);
    }

    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof DomainObjectWithVersioning<?>) {
            final int i = getVersionColumn(event);
            if (((Integer) event.getOldState()[i]).intValue() < ((Number) event
                    .getState()[i]).intValue()) {
                createHistory(event.getPersister(), event.getEntity(),
                        ModificationType.MODIFY);
                messageSender.notifyIndexer(event.getEntity(),
                        ModificationType.MODIFY);
            }
        }
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
        messageSender.notifyIndexer(event.getEntity(), ModificationType.DELETE);
    }

    private void removeLocalization(EntityPersister persister, Object entity) {
        if (entity instanceof DomainObject<?>) {
            DomainObject<?> obj = (DomainObject<?>) entity;
            List<LocalizedObjectTypes> objType = obj.getLocalizedObjectTypes();
            if (objType.size() > 0) {
                long key = obj.getKey();
                final StatelessSession session = persister.getFactory()
                        .openStatelessSession();
                org.hibernate.Query query = session
                        .createQuery("DELETE FROM LocalizedResource WHERE objectKey = :objectKey AND objectType IN (:objectType)");
                query.setParameter("objectKey", Long.valueOf(key));
                query.setParameterList("objectType", objType);
                query.executeUpdate();
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
                session.insert(hist);
                session.close();

                if (logger.isDebugLoggingEnabled()) {
                    logger.logDebug(String.format("%s %s[%s, v=%s]", type, obj
                            .getClass().getSimpleName(), Long.valueOf(obj
                            .getKey()), Long.valueOf(hist.getObjVersion())));
                }
            }
        }
    }
}
