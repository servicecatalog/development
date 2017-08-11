/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 20.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

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
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.xml.sax.InputSource;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;

/**
 * WSDL locator implementation to load a WSDL and all related imports using the
 * specified credentials.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class BasicAuthWSDLLocator implements WSDLLocator {

    private final static Log4jLogger logger = LoggerFactory
            .getLogger(WSDLLocator.class);

    private String userName;
    private String password;
    private String wsdlBaseURL;
    private String importLocation;
    private InputStream in;

    public BasicAuthWSDLLocator(String wsdlBaseURL, String userName,
            String password) {
        this.wsdlBaseURL = wsdlBaseURL;
        this.userName = userName;
        this.password = password;
    }

    private InputSource createInputSource(String url) throws IOException {

        URL targetURL = new URL(url);
        HttpHost targetHost = new HttpHost(targetURL.getHost(),
                targetURL.getPort(), targetURL.getProtocol());

        CloseableHttpClient httpclient = HttpClients.createDefault();

        final String proxyHost = System.getProperty("http.proxyHost");
        final String proxyPort = System.getProperty("http.proxyPort", "80");
        
        if (proxyHost != null && proxyHost.trim().length() > 0) {
            
            HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
                    proxy);
            httpclient = HttpClients.custom().setRoutePlanner(routePlanner)
                    .build();
        }

        // Set credentials if specified:
        HttpClientContext context = HttpClientContext.create();

        if (userName != null && userName.length() > 0) {

            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    userName, password);

            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(new AuthScope(targetURL.getHost(),
                    targetURL.getPort(), AuthScope.ANY_REALM), credentials);

            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            context.setCredentialsProvider(provider);
            context.setAuthCache(authCache);
        }

        // Retrieve content:
        // opening a local resource isn't supported by apache
        // instead open stream directly
        if (targetURL.getProtocol().startsWith("file")) {
            in = targetURL.openStream();
            return new InputSource(in);
        }

        HttpGet httpGet = new HttpGet(targetURL.toString());

        CloseableHttpResponse response = httpclient.execute(httpGet, context);
        int status = response.getStatusLine().getStatusCode();

        if (status != HttpStatus.SC_OK) {
            throw new IOException(
                    "Error " + status + " while retrieving " + url);
        }

        HttpEntity entity = response.getEntity();
        return new InputSource(entity.getContent());
    }

    @Override
    public InputSource getBaseInputSource() {
        try {
            return createInputSource(wsdlBaseURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBaseURI() {
        return wsdlBaseURL;
    }

    @Override
    public InputSource getImportInputSource(String parentLocation,
            String importLocation) {
        this.importLocation = importLocation;
        try {
            if (parentLocation == null) {
                return createInputSource(importLocation);
            } else {
                URL parentUrl = new URL(parentLocation);
                return createInputSource(new URL(parentUrl, importLocation)
                        .toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLatestImportURI() {
        return importLocation;
    }

    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // ignore, stream is already closed
            }
        }
    }
}
