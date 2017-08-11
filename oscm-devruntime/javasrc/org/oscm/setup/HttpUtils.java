/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: Peter Pock                                                   
 *                                                                              
 *  Creation Date: 03.07.2009                                                      
 *                                                                              
 *  Completion Time:                                    
 *                                                                              
 *******************************************************************************/

package org.oscm.setup;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;

/**
 * Provides some basic functionality required to reset the example service
 * 
 * @author Peter Pock
 * 
 */
public class HttpUtils {

    /**
     * parameters: serviceResetUrl
     */
    public static void main(String args[]) {
        if (args.length != 1) {
            throw new RuntimeException(
                    "Usage: java HttpUtils <serviceResetUrl>");
        }
        performHttpGet(args[0]);
    }

    /**
     * Perform an HTTP GET request
     * 
     * @param url
     */
    public static final void performHttpGet(String url) {

        if (url == null || url.length() == 0) {
            return;
        }

        // Provide custom retry handler is necessary
        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(
                3, false);

        // Create an instance of HttpClient.
        HttpClient client = HttpClients.custom().setRetryHandler(retryHandler)
                .build();

        // Create a method instance.
        HttpGet httpGet = new HttpGet(url);

        try {
            // Execute the method.
            HttpResponse httpResponse = client.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + statusLine);

                throw new IOException("Error " + statusLine.getStatusCode()
                        + " while retrieving " + url);
            }

            HttpEntity entity = httpResponse.getEntity();

            // Read the response body.
            InputStream response = entity.getContent();
            try {
                byte[] buf = new byte[1024];
                int i = 0;
                while (i != -1) {
                    i = response.read(buf);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
