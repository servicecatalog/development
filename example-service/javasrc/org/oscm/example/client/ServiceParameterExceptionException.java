
/**
 * ServiceParameterExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */

package org.oscm.example.client;

public class ServiceParameterExceptionException extends java.lang.Exception{
    
    private org.oscm.example.client.SessionServiceStub.ServiceParameterException faultMessage;

    
        public ServiceParameterExceptionException() {
            super("ServiceParameterExceptionException");
        }

        public ServiceParameterExceptionException(java.lang.String s) {
           super(s);
        }

        public ServiceParameterExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public ServiceParameterExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.oscm.example.client.SessionServiceStub.ServiceParameterException msg){
       faultMessage = msg;
    }
    
    public org.oscm.example.client.SessionServiceStub.ServiceParameterException getFaultMessage(){
       return faultMessage;
    }
}
    