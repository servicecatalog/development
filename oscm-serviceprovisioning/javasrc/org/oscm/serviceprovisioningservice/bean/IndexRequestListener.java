/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: July 26, 2011                                                      
 *                                                                              
 *  Completion Time: July 26, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.bean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
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

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.types.constants.Configuration;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * Message driven bean to handle the index request objects sent by the business
 * logic.
 * 
 * @author Dirk Bernsau
 * 
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "UserName", propertyValue = "jmsuser"),
        @ActivationConfigProperty(propertyName = "Password", propertyValue = "jmsuser") }, name = "jmsQueue", mappedName = "jms/bss/indexerQueue")
public class IndexRequestListener {

    private final static Log4jLogger logger = LoggerFactory
            .getLogger(IndexRequestListener.class);

    @EJB(beanInterface = ConfigurationServiceLocal.class)
    private ConfigurationServiceLocal confSvc;

    private static final long DELAY_TIME = readDelayTime();
    private static final String DELAY_PROPERTY = "ON_MESSAGE_DELAY";

    /**
     * Read the time (in milliseconds) to delay the message execution from the
     * property "DELAY_PROPERTY"; If the property is not set use the default
     * setting.
     */
    protected static long readDelayTime() {
        long delayTime = 50;// milliseconds

        String propertyString = System.getProperty(DELAY_PROPERTY);
        if (propertyString != null && propertyString.length() != 0) {
            try {
                long propertyValue = Long.parseLong(propertyString);
                if (propertyValue >= 0) {
                    delayTime = propertyValue;
                }
            } catch (NumberFormatException ex) {
                logger.logWarn(Log4jLogger.SYSTEM_LOG,
                        LogMessageIdentifier.WARN_INVALID_PROPERTY_VALUE,
                        propertyString, DELAY_PROPERTY,
                        Long.toString(delayTime));
            }
        }
        logger.logDebug("onMessage delay time is set to " + delayTime
                + " milli seconds", Log4jLogger.SYSTEM_LOG);

        return delayTime;
    }

    /**
     * Delay the message execution because there is small gap in the two phase
     * commit. For details see Bug 9061.
     */
    private void delayMessage() {
        try {
            Thread.sleep(DELAY_TIME);
        } catch (InterruptedException ex) {
            logger.logDebug("InterruptedException caught " + ex.getMessage(),
                    Log4jLogger.SYSTEM_LOG);
        }
    }

    public void onMessage(Message message) {
        logger.logDebug("Received object message from indexing queue",
                Log4jLogger.SYSTEM_LOG);
        delayMessage();

        // lookup jms resource and forward message
        Session session = null;
        Connection conn = null;
        try {
            Context ctx = getContext();
            ConnectionFactory qFactory = (ConnectionFactory) ctx
                    .lookup("jms/bss/masterIndexerQueueFactory");
            Queue targetQueue = (Queue) ctx
                    .lookup("jms/bss/masterIndexerQueue");
            conn = qFactory.createConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(targetQueue);
            producer.send(message);
        } catch (Throwable e) {
            logger.logInfo(Log4jLogger.SYSTEM_LOG,
                    LogMessageIdentifier.ERROR_EVALUATE_MESSAGE_FAILED);
            if (putBackMessageOnIndexerQueue(message)) {
                logger.logError(Log4jLogger.SYSTEM_LOG, e,
                        LogMessageIdentifier.ERROR_EVALUATE_MESSAGE_FAILED);
            }
        } finally {
            closeSession(session);
            closeConnection(conn);
        }
    }

    Context getContext() throws NamingException {
        return new InitialContext();
    }

    private boolean putBackMessageOnIndexerQueue(Message message) {
        if (message instanceof ObjectMessage) {
            Session session = null;
            Connection conn = null;
            try {
                Context jndiContext = getContext();
                ConnectionFactory qFactory = (ConnectionFactory) jndiContext
                        .lookup("jms/bss/indexerQueueFactory");
                conn = qFactory.createConnection();
                Queue queue = (Queue) jndiContext
                        .lookup("jms/bss/indexerQueue");
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(queue);
                ObjectMessage msg = session.createObjectMessage();
                msg.setObject(((ObjectMessage) message).getObject());
                producer.send(msg);
                return true;
            } catch (Throwable e) {
                // This should not happen because the indexer queue is in the
                // local server. If it happens, than there's something terribly
                // wrong.
                throw new SaaSSystemException(e);
            } finally {
                closeSession(session);
                closeConnection(conn);
            }
        } else {
            return false;
        }
    }

    private String getConfigurationSetting(ConfigurationKey key) {
        ConfigurationSetting setting = confSvc.getConfigurationSetting(key,
                Configuration.GLOBAL_CONTEXT);
        if (setting != null) {
            return setting.getValue();
        }
        SaaSSystemException sse = new SaaSSystemException(
                "Configuration missing");
        logger.logError(Log4jLogger.SYSTEM_LOG, sse,
                LogMessageIdentifier.WARN_NO_CONFIGRATION_SETTING_VALUE_FOUND,
                key.name());
        throw sse;
    }

    private void closeSession(Session session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_CLOSE_RESOURCE_FAILED);
        }
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (JMSException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_CLOSE_RESOURCE_FAILED);
        }
    }
}
