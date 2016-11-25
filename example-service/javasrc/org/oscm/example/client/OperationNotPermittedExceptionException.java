
/**
 * OperationNotPermittedExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */

package org.oscm.example.client;

public class OperationNotPermittedExceptionException extends java.lang.Exception{
    
    private org.oscm.example.client.SessionServiceStub.OperationNotPermittedException faultMessage;

    
        public OperationNotPermittedExceptionException() {
            super("OperationNotPermittedExceptionException");
        }

        public OperationNotPermittedExceptionException(java.lang.String s) {
           super(s);
        }

        public OperationNotPermittedExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OperationNotPermittedExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.oscm.example.client.SessionServiceStub.OperationNotPermittedException msg){
       faultMessage = msg;
    }
    
    public org.oscm.example.client.SessionServiceStub.OperationNotPermittedException getFaultMessage(){
       return faultMessage;
    }
}
    