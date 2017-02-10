/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 16.06.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.triggerservice.stubs;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class ConnectionFactoryStub implements ConnectionFactory {

    ConnectionStub connStub = new ConnectionStub();
    boolean throwsJMSException;

    public Connection createConnection() throws JMSException {
        if (throwsJMSException) {
            throw new JMSException("test caused exception");
        }
        return connStub;
    }

    public Connection createConnection(String arg0, String arg1)
            throws JMSException {
        throw new UnsupportedOperationException();
    }

    public void setThrowsJMSException(boolean throwsJMSException) {
        this.throwsJMSException = throwsJMSException;
    }

}
