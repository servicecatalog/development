/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: tokoda                                                      
 *                                                                              
 *  Creation Date: Nov 3, 2011                                                      
 *                                                                              
 *  Completion Time: Nov 3, 2011                                        
 *                                                                              
 *******************************************************************************/

package org.oscm.taskhandling.bean;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.PlatformUser;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.taskhandling.local.TaskMessage;
import org.oscm.taskhandling.local.TaskQueueServiceLocal;
import org.oscm.types.enumtypes.LogMessageIdentifier;

/**
 * The bean implementation for the task queue service.
 * 
 * @author tokoda
 * 
 */
@Stateless
@Local(TaskQueueServiceLocal.class)
public class TaskQueueServiceBean implements TaskQueueServiceLocal {

    protected static Log4jLogger logger = LoggerFactory
            .getLogger(TaskQueueServiceBean.class);

    @Resource(name = "connFactory", mappedName = "jms/bss/taskQueueFactory")
    protected ConnectionFactory qFactory;

    @Resource(name = "jmsQueue", mappedName = "jms/bss/taskQueue")
    protected Queue queue;

    @EJB(beanInterface = DataService.class)
    DataService dm;

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void sendAllMessages(List<TaskMessage> messages) {
        validateJMSResources();
        validateMessages(messages);
        double msgSize = Math.ceil(messages.size()/1000.0);
        int counter = 0;
        while(counter < msgSize) {
            int fromIndex = counter * 1000;
            int toIndex = Math.min(fromIndex + 1000, messages.size());
            sendObjectMessage(messages.subList(fromIndex, toIndex));
            counter++;
        }
    }

    /**
     * Checks if the injected JMS resources are not <code>null</code>. If they
     * are, a system exception will be thrown.
     */
    private void validateJMSResources() {
        if (queue == null || qFactory == null) {
            SaaSSystemException sse = new SaaSSystemException(
                    "JMS resources are not initialized!");
            logger.logError(Log4jLogger.SYSTEM_LOG, sse,
                    LogMessageIdentifier.ERROR_JMS_RESOURCE_NOT_INITIALIZED);

            throw sse;
        }
    }

    /**
     * Checks if the sent list of Messages are not <code>null</code>, and if the
     * payload of the Messages are not <code>null</code>.
     */
    private void validateMessages(List<TaskMessage> messages) {
        if (messages == null) {
            SaaSSystemException sse = new SaaSSystemException(
                    "The list of task messages are not initialized!");
            logger.logWarn(Log4jLogger.SYSTEM_LOG, sse,
                    LogMessageIdentifier.ERROR_LIST_OF_MESSAGES_NOT_INITIALIZED);

            throw sse;
        }

        for (TaskMessage message : messages) {
            if (message.getHandlerClass() == null) {
                SaaSSystemException sse = new SaaSSystemException(
                        "Handler class of the message is empty!");
                logger.logError(Log4jLogger.SYSTEM_LOG, sse,
                        LogMessageIdentifier.ERROR_HANDLER_OF_MESSAGE_EMPTY);

                throw sse;
            }
            if (message.getPayload() == null) {
                SaaSSystemException sse = new SaaSSystemException(
                        "Payload of the message is empty!");
                logger.logError(Log4jLogger.SYSTEM_LOG, sse,
                        LogMessageIdentifier.ERROR_PAYLOAD_OF_MESSAGE_EMPTY);

                throw sse;
            }
        }
    }

    /**
     * Sends the message objects to the JMS queue, each of them in a single
     * message.
     * 
     * @param messages
     *            The message objects to be sent.
     * @throws TaskHandlingException
     *             Thrown in case the send operation fails.
     */
    private void sendObjectMessage(List<TaskMessage> messages) {
        Session session = null;
        Connection conn = null;
        int sentMsgCount = 0;
        try {
            if (messages.size() > 0) {
                conn = qFactory.createConnection();
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(queue);
                for (TaskMessage objectToSend : messages) {
                    PlatformUser user = dm.getCurrentUserIfPresent();
                    if (user != null) {
                        objectToSend.setCurrentUserKey(user.getKey());
                    }
                    ObjectMessage msg = session.createObjectMessage();
                    msg.setObject(objectToSend);
                    producer.send(msg);
                    sentMsgCount++;
                }

            }
        } catch (JMSException e) {
            SaaSSystemException sse = new SaaSSystemException(e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    sse,
                    LogMessageIdentifier.ERROR_SEND_MESSAGE_TO_JMS_QUEUE_FAILED,
                    getMessagesStatues(messages, sentMsgCount));
            String status = getMessagesStatues(messages, sentMsgCount);
            logger.logError(
                    LogMessageIdentifier.ERROR_SEND_MESSAGE_TO_JMS_QUEUE_FAILED_DETAILS,
                    Integer.toString(sentMsgCount),
                    Integer.toString(messages.size()), status);
            throw sse;
        } finally {
            closeSession(session);
            closeConnection(conn);
        }
    }

    void closeSession(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                logger.logError(Log4jLogger.SYSTEM_LOG, e,
                        LogMessageIdentifier.ERROR_CLOSE_RESOURCE_FAILED);
            }
        }
    }

    void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                logger.logError(Log4jLogger.SYSTEM_LOG, e,
                        LogMessageIdentifier.ERROR_CLOSE_RESOURCE_FAILED);
            }
        }
    }

    private String getMessagesStatues(List<TaskMessage> messages, int sentCount) {
        String status = "";

        for (int i = 0; i < messages.size(); i++) {
            TaskMessage msg = messages.get(i);
            String sta = "Sent Message #" + i
                    + ((i < sentCount) ? " Successful " : " Failed ") + "::"
                    + getMessageInfo(msg, ", ");
            if (i < sentCount) {
                sta += ", " + getMessageStatus(msg);
            }
            status += sta + ";";
        }
        return status;
    }

    private String getMessageInfo(TaskMessage msg, String seperator) {
        String info = "";
        info += "Handler: " + msg.getHandlerClass().getCanonicalName()
                + seperator + "Payload: "
                + msg.getPayload().getClass().getCanonicalName();
        info += seperator + "Details: " + msg.getPayload().getInfo();

        return info;
    }

    private String getMessageStatus(TaskMessage msg) {
        return "Number Of Attempt: " + msg.getNumberOfAttempt();
    }
}
