/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

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
 * Helper class to create an URL object that retrieves the content specified by
 * the given URL with credentials. Internally the content is pre-fetched with
 * the given credentials. The returned URL has a custom protocol handler that
 * simply returns the pre-fetched content.
 * 
 * @author hoffmann
 * 
 */
public class BasicAuthLoader {

    /**
     * Creates a new URL object that retrieves the content specified by the
     * given URL with credentials. Internally the content is pre-fetched with
     * the given credentials. The returned URL has a custom protocol handler
     * that simply returns the pre-fetched content.
     * 
     * @param url
     *            the URL to read
     * @param username
     * @param password
     * @return an URL with a custom protocol handler
     * @throws IOException
     *             if an I/O exception occurs.
     */
    public static URL load(final URL url, final String username,
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
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), url
                .getFile(), handler);
    }

    /**
     * Retrieves the content under the given URL with username and passwort
     * authentication.
     * 
     * @param url
     *            the URL to read
     * @param username
     * @param password
     * @return the read content.
     * @throws IOException
     *             if an I/O exception occurs.
     */
    private static byte[] getUrlContent(URL url, String username,
            String password) throws IOException {

        HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(),
                url.getProtocol());
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Set credentials:
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
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
