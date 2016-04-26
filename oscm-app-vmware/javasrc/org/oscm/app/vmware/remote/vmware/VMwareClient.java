/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2013 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: Jan 18, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.remote.vmware;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.ws.BindingProvider;

import org.oscm.app.vmware.persistence.VMwareCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwareClient implements AutoCloseable {

    private static final Logger logger = LoggerFactory
            .getLogger(VMwareClient.class);

    private String url;
    private String user;
    private String password;
    private ServiceConnection connection;

    public VMwareClient() {

    }

    public VMwareClient(VMwareCredentials credentials) {
        this.url = credentials.getURL();
        this.user = credentials.getUserId();
        this.password = credentials.getPassword();
        logger.debug(
                "Created VMware client for url " + url + " and user " + user);
    }

    /**
     * Establish a connection to the vCenter.
     */
    public void connect() throws Exception {

        // FIXME what to do?
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };

        int numFailedLogins = 0;
        boolean repeatLogin = true;

        while (repeatLogin) {
            try {

                HttpsURLConnection.setDefaultHostnameVerifier(hv);

                VimService vimService = new VimService();
                VimPortType vimPort = vimService.getVimPort();
                Map<String, Object> ctxt = ((BindingProvider) vimPort)
                        .getRequestContext();

                ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
                ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY,
                        Boolean.TRUE);

                ManagedObjectReference morSvcInstance = new ManagedObjectReference();
                morSvcInstance.setType("ServiceInstance");
                morSvcInstance.setValue("ServiceInstance");
                ServiceContent serviceContent = vimPort
                        .retrieveServiceContent(morSvcInstance);
                vimPort.login(serviceContent.getSessionManager(), user,
                        password, null);
                connection = new ServiceConnection(vimPort, serviceContent);
                repeatLogin = false;
            } catch (Exception e) {
                logger.error(
                        "Failed to login. URL: " + url + " UserId: " + user, e);
                if (numFailedLogins > 2) {
                    throw e;
                }
                numFailedLogins++;
                repeatLogin = true;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Returns whether the connection has been established
     * 
     * @return true if we are connected
     */
    public boolean isConnected() {
        if (connection == null) {
            return false;
        }

        try {
            ManagedObjectReference rootFolder = connection.getServiceContent()
                    .getRootFolder();
            getServiceUtil().getDynamicProperty(rootFolder, "name");
            return true;
        } catch (@SuppressWarnings("unused") Exception e) {
            return false;
        }
    }

    /**
     * Returns the {@link ManagedObjectAccessor} based on the current client
     * connection.
     * 
     * @return the managed object accessor
     * @throws IllegalStateException
     *             when client is not connected
     */
    public ManagedObjectAccessor getServiceUtil() {
        return new ManagedObjectAccessor(getConnection());
    }

    /**
     * Returns the {@link ServiceConnection} when client is in connect state.
     * 
     * @return the service connection
     * @throws IllegalStateException
     *             when client is not connected
     */
    public ServiceConnection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Not connected");
        }
        return connection;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.disconnect();
        }
        connection = null;
    }

}
