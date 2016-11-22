/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Factory to create a BSS ServicePortType to call BSS.
 * 
 * @author pock
 */
public class PortFactory {

    public static final String INTERNAL = "INTERNAL";
    public static final String SAML_SP = "SAML_SP";

    private static URL getWsdlLocation(final ConnectionInfo connection,
            final String serviceName) throws IOException {
        StringBuilder s = new StringBuilder();
        s.append(connection.getBaseUrl()).append("oscm/");
        s.append(connection.getVersion()).append("/");
        s.append(serviceName).append("/");
        if (connection.getAuthMode().equals(INTERNAL)) {
            if (connection.isClientCert()) {
                s.append("CLIENTCERT?wsdl");
            } else {
                s.append("BASIC?wsdl");
            }
        } else {
            s.append("STS?wsdl");
            String tenantId = connection.getTenantId();
            
            if(isTenantIdValid(tenantId)){
                s.append("&tenantID="+tenantId);
            }
        }
        return new URL(s.toString());
    }
    
    private static boolean isTenantIdValid(String tenantId) {

        if (tenantId == null) {
            return false;
        }

        if (tenantId.length() != 8) {
            return false;
        }

        return true;
    }

    private static URL getEndpointLocation(final ConnectionInfo connection,
            final String serviceName) throws IOException {
        StringBuilder s = new StringBuilder();
        s.append(connection.getBaseUrl());
        s.append(serviceName).append("/");
        if (INTERNAL.equals(connection.getAuthMode())) {
            if (connection.isClientCert()) {
                s.append("CLIENTCERT?wsdl");
            } else {
                s.append("BASIC?wsdl");
            }
        } else {
            s.append("STS?wsdl");
        }

        return new URL(s.toString());
    }

    public static <T> T getPort(final ConnectionInfo info, final Class<T> type)
            throws IOException {

        final String serviceName = type.getSimpleName();

        URL wsdlLocation = getWsdlLocation(info, serviceName);

        if (!info.isClientCert()) {
            wsdlLocation = withCredentials(wsdlLocation, info.getUsername(),
                    info.getPassword());
        }

        final Service service = Service.create(wsdlLocation, new QName(
                "http://oscm.org/xsd", serviceName));

        final T port = service.getPort(type);

        final Map<String, Object> ctx = ((BindingProvider) port)
                .getRequestContext();
        ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                getEndpointLocation(info, serviceName).toString());
        if (!info.isClientCert()) {
            ctx.put(BindingProvider.USERNAME_PROPERTY, info.getUsername());
            ctx.put(BindingProvider.PASSWORD_PROPERTY, info.getPassword());
        } else if (SAML_SP.equals(info.getAuthMode())) {
            ctx.put("username", info.getUsername());
            ctx.put("password", info.getPassword());
        }

        return port;
    }

    /**
     * Creates a new URL object that retrieves the content specified by the
     * given URL with credentials.. Internally the content is pre-fetched with
     * the given credentials. The returned URL has an custom protocol handler
     * that simply returns the pre-fetched content.
     * 
     * @param url
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    private static URL withCredentials(final URL url, final String username,
            final String password) throws IOException {

        final byte[] content = getUrlContent(url, username, password);

        final URLStreamHandler handler = new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return new URLConnection(url) {
                    @Override
                    public void connect() throws IOException {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new ByteArrayInputStream(content);
                    }
                };
            }
        };
        return new URL(url.getProtocol(), url.getHost(), url.getPort(),
                url.getFile(), handler);
    }

    /**
     * Retrieves the content under the given URL with username and passwort
     * authentication.
     * 
     * @param url
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    private static byte[] getUrlContent(URL url, String username,
            String password) throws IOException {
        final HttpClient client = new HttpClient();

        // Set credentials:
        client.getParams().setAuthenticationPreemptive(true);
        final Credentials credentials = new UsernamePasswordCredentials(
                username, password);
        client.getState()
                .setCredentials(
                        new AuthScope(url.getHost(), url.getPort(),
                                AuthScope.ANY_REALM), credentials);

        // Retrieve content:
        final GetMethod method = new GetMethod(url.toString());
        final int status = client.executeMethod(method);
        if (status != HttpStatus.SC_OK) {
            throw new IOException("Error " + status + " while retrieving "
                    + url);
        }
        return method.getResponseBody();
    }
}
