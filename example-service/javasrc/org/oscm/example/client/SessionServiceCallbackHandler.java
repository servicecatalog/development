
/**
 * SessionServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */

    package org.oscm.example.client;

    /**
     *  SessionServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class SessionServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public SessionServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public SessionServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for createPlatformSession method
            * override this method for handling normal response from createPlatformSession operation
            */
           public void receiveResultcreatePlatformSession(
                    org.oscm.example.client.SessionServiceStub.CreatePlatformSessionResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createPlatformSession operation
           */
            public void receiveErrorcreatePlatformSession(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for resolveUserToken method
            * override this method for handling normal response from resolveUserToken operation
            */
           public void receiveResultresolveUserToken(
                    org.oscm.example.client.SessionServiceStub.ResolveUserTokenResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from resolveUserToken operation
           */
            public void receiveErrorresolveUserToken(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createServiceSession method
            * override this method for handling normal response from createServiceSession operation
            */
           public void receiveResultcreateServiceSession(
                    org.oscm.example.client.SessionServiceStub.CreateServiceSessionResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createServiceSession operation
           */
            public void receiveErrorcreateServiceSession(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteSessionsForSessionId method
            * override this method for handling normal response from deleteSessionsForSessionId operation
            */
           public void receiveResultdeleteSessionsForSessionId(
                    org.oscm.example.client.SessionServiceStub.DeleteSessionsForSessionIdResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteSessionsForSessionId operation
           */
            public void receiveErrordeleteSessionsForSessionId(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteServiceSession method
            * override this method for handling normal response from deleteServiceSession operation
            */
           public void receiveResultdeleteServiceSession(
                    org.oscm.example.client.SessionServiceStub.DeleteServiceSessionResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteServiceSession operation
           */
            public void receiveErrordeleteServiceSession(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteServiceSessionsForSubscription method
            * override this method for handling normal response from deleteServiceSessionsForSubscription operation
            */
           public void receiveResultdeleteServiceSessionsForSubscription(
                    org.oscm.example.client.SessionServiceStub.DeleteServiceSessionsForSubscriptionResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteServiceSessionsForSubscription operation
           */
            public void receiveErrordeleteServiceSessionsForSubscription(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getNumberOfServiceSessions method
            * override this method for handling normal response from getNumberOfServiceSessions operation
            */
           public void receiveResultgetNumberOfServiceSessions(
                    org.oscm.example.client.SessionServiceStub.GetNumberOfServiceSessionsResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getNumberOfServiceSessions operation
           */
            public void receiveErrorgetNumberOfServiceSessions(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deletePlatformSession method
            * override this method for handling normal response from deletePlatformSession operation
            */
           public void receiveResultdeletePlatformSession(
                    org.oscm.example.client.SessionServiceStub.DeletePlatformSessionResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deletePlatformSession operation
           */
            public void receiveErrordeletePlatformSession(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSubscriptionKeysForSessionId method
            * override this method for handling normal response from getSubscriptionKeysForSessionId operation
            */
           public void receiveResultgetSubscriptionKeysForSessionId(
                    org.oscm.example.client.SessionServiceStub.GetSubscriptionKeysForSessionIdResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSubscriptionKeysForSessionId operation
           */
            public void receiveErrorgetSubscriptionKeysForSessionId(java.lang.Exception e) {
            }
                


    }
    