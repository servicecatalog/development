/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                        
 *                                                                              
 *  Creation Date: 2013-11-03                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLStreamHandler;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.openstack.proxy.ProxyAuthenticator;
import org.oscm.app.openstack.proxy.ProxySettings;

/**
 * A connection to the OpenStack Heat API.
 */
public class OpenStackConnection {

    private static final Logger logger = LoggerFactory
            .getLogger(OpenStackConnection.class);

    private static URLStreamHandler streamHandler;

    private String keystoneEndpoint = "";
    private String heatEndpoint = "";
    private String novaEndpoint = "";
    private String authToken = null;

    /**
     * Sets the URL stream handler. <b>Should only be used for unit testing!</b>
     * 
     * @param streamHandler
     */
    public static void setURLStreamHandler(URLStreamHandler streamHandler) {
        OpenStackConnection.streamHandler = streamHandler;
    }

    /**
     * 
     * @param keystoneEndpoint
     *            The URL to the Keystone API
     */
    public OpenStackConnection(String keystoneEndpoint) {
        this.keystoneEndpoint = removeTrailingSlash(keystoneEndpoint);
    }

    /**
     * 
     * @param endpoint
     *            The URL to the Heat API
     */
    public void setHeatEndpoint(String endpoint) {
        heatEndpoint = removeTrailingSlash(endpoint);
    }

    /**
     * 
     * @param endpoint
     *            The URL to the Nova API
     */
    public void setNovaEndpoint(String endpoint) {
        novaEndpoint = removeTrailingSlash(endpoint);
    }

    private String removeTrailingSlash(String endpoint) {
        while (endpoint != null && endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint;
    }

    /**
     * 
     * @return The URL to the Heat API
     */
    public String getHeatEndpoint() {
        return heatEndpoint;
    }

    /**
     * 
     * @return The URL to the Nova API
     */
    public String getNovaEndpoint() {
        return novaEndpoint;
    }

    /**
     * 
     * @return The URL to the Keystone API
     */
    public String getKeystoneEndpoint() {
        return keystoneEndpoint;
    }

    public RESTResponse processRequest(String restUri, String method)
            throws HeatException {
        return processRequest(restUri, method, null);
    }

    public RESTResponse processRequest(String restUri, String method,
            String requestBody) throws HeatException {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpsURLConnection.setFollowRedirects(true);
            connection = connectUsingProxy(restUri, connection);
            logger.debug("Sending " + method + " request to " + restUri);

            connection.setRequestMethod(method);
            if (authToken != null) {
                connection.setRequestProperty("X-Auth-Token", authToken);
            }
            connection.setReadTimeout(30000);

            // add payload if present
            if (requestBody != null) {
                if (!requestBody.contains("password")) {
                    logger.debug("   request body:\n" + requestBody);
                }
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setDoOutput(true);
                out = new OutputStreamWriter(connection.getOutputStream());
                out.write(requestBody);
                out.close();
            }
            connection.connect();
            return new RESTResponse(connection);
        } catch (MalformedURLException e) {
            throw new HeatException("Failed to connect to Heat, invalid URL: "
                    + restUri);
        } catch (IOException e) {
            int responseCode = -1;
            try {
                if (connection != null) {
                    responseCode = connection.getResponseCode();
                }
            } catch (IOException e1) {
                responseCode = -1;
            }
            final String code = " (HTTP " + responseCode + ", URI " + restUri
                    + "): " + e.getMessage();
            switch (responseCode) {
            case 400:
                throw new HeatException(
                        "Heat response: either input parameter format error or security key is not correct"
                                + code, responseCode);
            case 401:
                throw new HeatException(
                        "Failed to connect to Heat, unauthorized" + code,
                        responseCode);

            case 404:
                throw new HeatException("Heat response: resource not found"
                        + code, responseCode);

            case 504:
                throw new HeatException(
                        "Failed to connect to Heat: Gateway/proxy timeout."
                                + code, responseCode);

            default:
                throw new HeatException(
                        "Failed to connect to Heat, send failed" + code,
                        responseCode);
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    // best effort pattern
                }
            }
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    // best effort pattern
                }
            }
        }
    }

    private HttpURLConnection connectUsingProxy(String restUri,
            HttpURLConnection connection) throws MalformedURLException,
            IOException, HeatException {

        URL url = new URL(null, restUri, streamHandler);

        try {

            if (ProxySettings.useProxyByPass(restUri)) {
                connection = (HttpURLConnection) url
                        .openConnection(Proxy.NO_PROXY);
            } else {

                String proxyHost = System
                        .getProperty(ProxySettings.HTTPS_PROXY_HOST);
                String proxyPort = System
                        .getProperty(ProxySettings.HTTPS_PROXY_PORT);
                String proxyUser = System
                        .getProperty(ProxySettings.HTTPS_PROXY_USER);
                String proxyPassword = System
                        .getProperty(ProxySettings.HTTPS_PROXY_PASSWORD);

                int proxyPortInt = 0;

                try {
                    proxyPortInt = Integer.parseInt(proxyPort);
                } catch (NumberFormatException e) {
                    // ignore
                }
                if (proxyHost != null && proxyPortInt > 0) {
                    // TODO check proxy type for HTTPS protocol
                    Proxy proxy = new Proxy(Proxy.Type.HTTP,
                            new InetSocketAddress(proxyHost, proxyPortInt));

                    if (proxyUser != null && proxyUser.length() > 0
                            && proxyPassword != null
                            && proxyPassword.length() > 0) {

                        Authenticator.setDefault(new ProxyAuthenticator(
                                proxyUser, proxyPassword));

                    }

                    connection = (HttpURLConnection) url.openConnection(proxy);
                }

                else {
                    connection = (HttpURLConnection) url
                            .openConnection(Proxy.NO_PROXY);
                }

            }

        } catch (ClassCastException e) {
            throw new HeatException(
                    "Connection to Heat could not be created. Expected http(s) connection for URL: "
                            + restUri);
        }
        return connection;
    }

    /**
     * If authToken is not set the connection will be used without
     * authentication. The three parameters are set as HTTP request properties.
     * <ul>
     * <li>authToken: X-Auth-Token</li>
     * </ul>
     * 
     * @param authToken
     *            generated by Keystone
     */
    protected void useAuthentication(String authToken) {
        this.authToken = authToken;
    }

}
