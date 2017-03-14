/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.dataservice.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.search.Environment;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import org.oscm.domobjects.LocalizedResource;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.index.IndexRequestMessage;
import org.oscm.internal.types.exception.SaaSSystemException;

public class IndexMQSenderTest {

    private IndexMQSender sender;

    @Captor
    ArgumentCaptor<Serializable> caughtMessage;

    private IndexRequestMessage objectMessage;
    private ConnectionFactory factory;

    private Queue queue;

    private Context context;

    @Before
    public void setup() throws Exception {
        System.setProperty(Environment.AUTOREGISTER_LISTENERS, "true");
        sender = spy(new IndexMQSender());

        LocalizedResource entity = new LocalizedResource();
        entity.setObjectType(LocalizedObjectTypes.PRICEMODEL_DESCRIPTION);
        objectMessage = IndexRequestMessage.get(entity, ModificationType.ADD);

        factory = mock(ConnectionFactory.class);
        sender.qFactory = factory;

        queue = mock(Queue.class);
        sender.queue = queue;

        context = mock(InitialContext.class);
        doReturn(context).when(sender).getContext();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        System.setProperty(Environment.AUTOREGISTER_LISTENERS, "false");
    }

    @Test
    public void create_noSetting() throws Exception {
        System.clearProperty(Environment.AUTOREGISTER_LISTENERS);
        sender = new IndexMQSender();
        assertNotNull(sender);
        assertTrue(sender.isNotifyIndexer());
    }

    @Test
    public void create_trueSetting() throws Exception {
        assertNotNull(sender);
        assertTrue(sender.isNotifyIndexer());
    }

    @Test
    public void create_falseSetting() throws Exception {
        System.setProperty(Environment.AUTOREGISTER_LISTENERS, "false");
        sender = new IndexMQSender();
        assertNotNull(sender);
        assertFalse(sender.isNotifyIndexer());
    }

    @Test
    public void notifyIndexer_disabled() throws Exception {
        System.setProperty(Environment.AUTOREGISTER_LISTENERS, "false");
        sender = spy(new IndexMQSender());

        sender.notifyIndexer(new Product(), ModificationType.ADD);
        verify(sender, times(0)).sendMessage(any(Serializable.class));
    }

    @Test
    public void notifyIndexer_enabled() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(sender).sendMessage(caughtMessage.capture());
        LocalizedResource entity = new LocalizedResource();
        entity.setObjectType(LocalizedObjectTypes.PRICEMODEL_DESCRIPTION);

        sender.notifyIndexer(entity, ModificationType.ADD);
        verify(sender, times(1)).sendMessage(any(Serializable.class));
        Serializable createdMessage = caughtMessage.getValue();
        assertNotNull(createdMessage);
        assertTrue(createdMessage instanceof IndexRequestMessage);
    }

    @Test
    public void notifyIndexer_enabledWithException() throws Exception {
        MockitoAnnotations.initMocks(this);
        doThrow(new JMSException("test caused")).when(sender).sendMessage(
                caughtMessage.capture());
        LocalizedResource entity = new LocalizedResource();
        entity.setObjectType(LocalizedObjectTypes.PRICEMODEL_DESCRIPTION);
        try {
            sender.notifyIndexer(entity, ModificationType.ADD);
            fail();
        } catch (SaaSSystemException e) {

        }
    }

    @Test
    public void sendMessage_noJMSResources() throws Exception {
        doReturn(Boolean.FALSE).when(sender).checkJMSResources();
        sender.sendMessage(objectMessage);
        verify(factory, times(0)).createConnection();
    }

    @Test
    public void sendMessage_withJMSResources() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(Boolean.TRUE).when(sender).checkJMSResources();
        Connection conn = mock(Connection.class);
        doReturn(conn).when(factory).createConnection();
        Session session = mock(Session.class);
        doReturn(session).when(conn).createSession(eq(false),
                eq(Session.AUTO_ACKNOWLEDGE));
        MessageProducer producer = mock(MessageProducer.class);
        doReturn(producer).when(session).createProducer(eq(queue));
        ObjectMessage om = mock(ObjectMessage.class);
        doReturn(om).when(session).createObjectMessage();
        doNothing().when(om).setObject(caughtMessage.capture());

        sender.sendMessage(objectMessage);
        verify(factory, times(1)).createConnection();
        verify(session, times(1)).close();
        verify(conn, times(1)).close();
        verify(producer, times(1)).send(eq(om));
        assertEquals(objectMessage, caughtMessage.getValue());
    }

    @Test
    public void sendMessage_checkCloseOnException() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(Boolean.TRUE).when(sender).checkJMSResources();
        Connection conn = mock(Connection.class);
        doReturn(conn).when(factory).createConnection();
        Session session = mock(Session.class);
        doReturn(session).when(conn).createSession(eq(false),
                eq(Session.AUTO_ACKNOWLEDGE));
        MessageProducer producer = mock(MessageProducer.class);
        doReturn(producer).when(session).createProducer(eq(queue));
        ObjectMessage om = mock(ObjectMessage.class);
        doReturn(om).when(session).createObjectMessage();
        doNothing().when(om).setObject(caughtMessage.capture());
        doThrow(new JMSException("test caused")).when(producer).send(
                any(Message.class));

        try {
            sender.sendMessage(objectMessage);
            fail();
        } catch (JMSException e) {
            verify(session, times(1)).close();
            verify(conn, times(1)).close();
        }
    }

    @Test
    public void getContext() throws Exception {
        sender = new IndexMQSender();
        assertNotNull(sender.getContext());
    }

    @Test
    public void checkJMSResources_positive() throws Exception {
        assertTrue(sender.checkJMSResources());
    }

    @Test
    public void checkJMSResources_positiveNoQueue() throws Exception {
        sender.queue = null;
        assertTrue(sender.checkJMSResources());
    }

    @Test
    public void checkJMSResources_negativeNamingExceptionOnLookup()
            throws Exception {
        sender.queue = null;
        doThrow(new NamingException()).when(context).lookup(anyString());
        assertFalse(sender.checkJMSResources());
    }

    @Test
    public void checkJMSResources_negativeNamingExceptionOnContextRetrieval()
            throws Exception {
        sender.queue = null;
        doThrow(new NamingException()).when(sender).getContext();
        assertFalse(sender.checkJMSResources());
    }

    @Test
    public void checkJMSResources_negativeNamingExceptionOnContextRetrieval2Calls()
            throws Exception {
        sender.queue = null;
        doThrow(new NamingException()).when(sender).getContext();
        assertFalse(sender.checkJMSResources());
        assertFalse(sender.checkJMSResources());
    }
}
