/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

        HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(),
                url.getProtocol());
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Set credentials:
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                username, password);

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(url.getHost(), url.getPort(),
                AuthScope.ANY_REALM), credentials);

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(provider);
        context.setAuthCache(authCache);

        // Retrieve content:
        HttpGet httpGet = new HttpGet(url.toString());

        CloseableHttpResponse response = httpclient.execute(httpGet, context);
        int status = response.getStatusLine().getStatusCode();

        if (status != HttpStatus.SC_OK) {
            throw new IOException(
                    "Error " + status + " while retrieving " + url);
        }
        
        HttpEntity entity = response.getEntity();
        return EntityUtils.toByteArray(entity);
    }
}
