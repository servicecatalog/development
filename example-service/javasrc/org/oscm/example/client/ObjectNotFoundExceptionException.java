
/**
 * ObjectNotFoundExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */

package org.oscm.example.client;

public class ObjectNotFoundExceptionException extends java.lang.Exception{
    
    private org.oscm.example.client.SessionServiceStub.ObjectNotFoundException faultMessage;

    
        public ObjectNotFoundExceptionException() {
            super("ObjectNotFoundExceptionException");
        }

        public ObjectNotFoundExceptionException(java.lang.String s) {
           super(s);
        }

        public ObjectNotFoundExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public ObjectNotFoundExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.oscm.example.client.SessionServiceStub.ObjectNotFoundException msg){
       faultMessage = msg;
    }
    
    public org.oscm.example.client.SessionServiceStub.ObjectNotFoundException getFaultMessage(){
       return faultMessage;
    }
}
    