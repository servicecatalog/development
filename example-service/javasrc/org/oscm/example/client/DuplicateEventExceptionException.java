
/**
 * DuplicateEventExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */

package org.oscm.example.client;

public class DuplicateEventExceptionException extends java.lang.Exception{
    
    private org.oscm.example.client.EventServiceStub.DuplicateEventException faultMessage;

    
        public DuplicateEventExceptionException() {
            super("DuplicateEventExceptionException");
        }

        public DuplicateEventExceptionException(java.lang.String s) {
           super(s);
        }

        public DuplicateEventExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public DuplicateEventExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.oscm.example.client.EventServiceStub.DuplicateEventException msg){
       faultMessage = msg;
    }
    
    public org.oscm.example.client.EventServiceStub.DuplicateEventException getFaultMessage(){
       return faultMessage;
    }
}
    