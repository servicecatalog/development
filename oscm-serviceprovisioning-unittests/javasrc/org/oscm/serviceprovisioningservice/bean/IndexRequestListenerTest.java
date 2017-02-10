/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.bean;

import static org.junit.Assert.assertTrue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;

import org.junit.Test;

import org.oscm.test.stubs.ObjectMessageStub;
import org.oscm.triggerservice.stubs.ConnectionStub;
import org.oscm.triggerservice.stubs.QueueStub;

/**
 * 
 * @author Mani
 * 
 */
public class IndexRequestListenerTest {
    private final ConnectionStub conn = new ConnectionStub();

    private final IndexRequestListener listener = new IndexRequestListener() {
        javax.naming.Context getContext() throws javax.naming.NamingException {
            return new InitialContext() {
                public Object lookup(String name)
                        throws javax.naming.NamingException {
                    if (name.indexOf("Factory") > -1) {
                        return new ConnectionFactory() {
                            @Override
                            public Connection createConnection(String arg0,
                                    String arg1) throws JMSException {
                                return null;
                            }

                            @Override
                            public Connection createConnection()
                                    throws JMSException {
                                return conn;
                            }
                        };
                    }
                    return new QueueStub();
                };
            };
        };
    };

    @Test
    public void testResetToNonChargeable_PrimitiveFields() throws Exception {
        // given

        // when
        listener.onMessage(new ObjectMessageStub());

        // then
        assertTrue(conn.getSession().getMessageProducerStub().isMessageSent());
    }
}
