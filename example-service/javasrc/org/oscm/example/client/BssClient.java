/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Author: weiser                                                      
 *                                                                              
 *  Creation Date: 22.09.2010                                                      
 *                                                                              
 *  Completion Time: 23.09.2010                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.example.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;

import org.oscm.example.client.SessionServiceStub.DeleteServiceSessionResponseE;
import org.oscm.example.client.SessionServiceStub.ResolveUserTokenResponseE;
import org.oscm.example.common.InetLookup;

/**
 * @author weiser
 * 
 */
public class BssClient {

    private boolean searchPort = true;
    private String host;
    private int port = 8181;
    private String userKey;
    private String password;

    private static volatile Properties configProperties;

    public static Properties getProperties() throws ServletException {
        if (configProperties == null) {
            synchronized (BssClient.class) {
                configProperties = loadProperties();
            }
        }
        return configProperties;
    }

    private static Properties loadProperties() throws ServletException {
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("example.properties");
        Properties props = new Properties();
        try {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new ServletException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore, wanted to close anyway
                }
            }
        }
        return props;
    }

    public BssClient(String host) {
        this.host = InetLookup.resolveHost(host);
    }

    public void recordEvent(String saasId, String eventId, String actor,
            long multiplier) throws ServletException, RemoteException,
            OrganizationAuthoritiesExceptionException,
            ValidationExceptionException, ObjectNotFoundExceptionException {
        EventServiceStub stub = new EventServiceStub(getEndPoint(true));
        EventServiceStub.RecordEventForSubscriptionE refs = new EventServiceStub.RecordEventForSubscriptionE();
        EventServiceStub.RecordEventForSubscription param = new EventServiceStub.RecordEventForSubscription();
        EventServiceStub.VoGatheredEvent event = new EventServiceStub.VoGatheredEvent();
        event.setActor(actor);
        event.setEventId(eventId);
        event.setMultiplier(multiplier);
        event.setOccurrenceTime(System.currentTimeMillis());
        param.setEvent(event);
        param.setSubscriptionKey(getSubscriptionKey(saasId));
        refs.setRecordEventForSubscription(param);
        try {
            stub.recordEventForSubscription(refs);
        } catch (AxisFault e) {
            if (port < 8185 && searchPort
                    && e.getDetail() instanceof ConnectException) {
                // try again with another port
                port++;
                recordEvent(saasId, eventId, actor, multiplier);
            }
            throw new ServletException(e);
        } catch (DuplicateEventExceptionException e) {
            // should not happen as unique id is not used
            e.printStackTrace();
        }
    }

    public String resolveUsertoken(String usertoken, String saasId)
            throws ServletException, RemoteException {
        SessionServiceStub stub = new SessionServiceStub(getEndPoint(false));
        setBasicAuth(stub);
        SessionServiceStub.ResolveUserTokenE rut = new SessionServiceStub.ResolveUserTokenE();
        SessionServiceStub.ResolveUserToken param = new SessionServiceStub.ResolveUserToken();
        param.setSessionId(getSessionId(saasId));
        param.setUserToken(usertoken);
        param.setSubscriptionKey(getSubscriptionKey(saasId));
        rut.setResolveUserToken(param);
        try {
            ResolveUserTokenResponseE response = stub.resolveUserToken(rut);
            return response.getResolveUserTokenResponse().get_return();
        } catch (AxisFault e) {
            if (port < 8185 && searchPort
                    && e.getDetail() instanceof ConnectException) {
                // try again with another port
                port++;
                return resolveUsertoken(usertoken, saasId);
            }
            throw new ServletException(e);
        }
    }

    public String logoutUser(String saasId) throws ServletException,
            RemoteException {
        SessionServiceStub stub = new SessionServiceStub(getEndPoint(false));
        setBasicAuth(stub);
        SessionServiceStub.DeleteServiceSessionE dss = new SessionServiceStub.DeleteServiceSessionE();
        SessionServiceStub.DeleteServiceSession param = new SessionServiceStub.DeleteServiceSession();
        param.setSessionId(getSessionId(saasId));
        param.setSubscriptionKey(getSubscriptionKey(saasId));
        dss.setDeleteServiceSession(param);
        try {
            DeleteServiceSessionResponseE response = stub
                    .deleteServiceSession(dss);
            return response.getDeleteServiceSessionResponse().get_return();
        } catch (AxisFault e) {
            if (port < 8185 && searchPort
                    && e.getDetail() instanceof ConnectException) {
                // try again with another port
                port++;
                return logoutUser(saasId);
            }
            throw new ServletException(e);
        }
    }

    private String getEndPoint(boolean event) throws ServletException {
        boolean usehttps = true;
        Properties props = getProperties();

        String value = props.getProperty("bssPort");
        if (value != null && value.length() > 0) {
            port = Integer.parseInt(value);
            searchPort = false;
        }
        // check if the port should be overwritten by a special host
        // port
        value = props.getProperty(host);
        if (value != null) {
            port = Integer.parseInt(value);
            searchPort = false;
        } else { // try to read the host
            value = props.getProperty("bssHost");
            if (value != null && value.length() > 0) {
                host = value;
            }
        }

        value = props.getProperty("useHttps");
        if (value != null) {
            usehttps = "true".equals(value);
        }
        userKey = props.getProperty("userKey");
        password = props.getProperty("password");

        String serviceName;
        if (event) {
            serviceName = "Event";
        } else {
            serviceName = "Session";
        }
        final String httpSstr = "https://%s:%s/%sService/BASIC";
        final String httpstr = "http://%s:%s/%sService/BASIC";
        String rtv = null;
        if (usehttps) {
            rtv = String.format(httpSstr, host, String.valueOf(port),
                    serviceName);
        } else {
            rtv = String.format(httpstr, host, String.valueOf(port),
                    serviceName);
        }

        return rtv;
    }

    /**
     * Ugly hack which only works due to the fact that the session server
     * doesn't verifiy the user's organization role
     * 
     * @param stub
     */
    private void setBasicAuth(Stub stub) {
        HttpTransportProperties.Authenticator basicAuth = new HttpTransportProperties.Authenticator();
        basicAuth.setUsername(userKey);
        basicAuth.setPassword(password);
        basicAuth.setPreemptiveAuthentication(true);
        final Options clientOptions = stub._getServiceClient().getOptions();
        clientOptions.setProperty(HTTPConstants.AUTHENTICATE, basicAuth);
    }

    private String getSessionId(String saasId) {
        // determine the subscription key and the session id
        int idx;
        idx = saasId.indexOf('_');
        if (idx < 0) {
            // the saasId must contain the subscription key
            return null;
        }
        return saasId.substring(idx + 1);
    }

    private long getSubscriptionKey(String saasId) {
        // determine the subscription key and the session id
        int idx;
        idx = saasId.indexOf('_');
        if (idx < 0) {
            // the saasId must contain the subscription key
            return 0;
        }
        return parseUnsignedLong(saasId.substring(0, idx));
    }

    private static long parseUnsignedLong(String str)
            throws NumberFormatException {
        if (str.length() > 16) {
            throw new NumberFormatException();
        }
        int lowstart = str.length() - 8;
        if (lowstart <= 0)
            return Long.parseLong(str, 16);
        else
            return Long.parseLong(str.substring(0, lowstart), 16) << 32
                    | Long.parseLong(str.substring(lowstart), 16);
    }

}
