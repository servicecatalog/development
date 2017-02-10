/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 03.08.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.adapter.bean;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.oscm.billing.external.exception.BillingException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.WebResource;

/**
 * Manages REST communication with the file billing application.
 */
public class RestDAO {

    /**
     * Create a web resource for the given URL
     */
    public WebResource createWebResource(String serviceURL) {

        Client client = Client.create();
        WebResource webResource = client.resource(serviceURL);
        return webResource;
    }

    /**
     * Create a web resource with the given parameters for the given URL
     */
    public WebResource createWebResource(String serviceURL,
            Map<String, String> queryParams) {
        Client client = Client.create();
        WebResource webResource = client.resource(serviceURL);
        for (String key : queryParams.keySet()) {
            webResource = webResource.queryParam(key, queryParams.get(key));
        }
        return webResource;
    }

    /**
     * Create a web resource with the given multi-valued parameters for the
     * given URL
     */
    public WebResource createMultiValueWebResource(String serviceURL,
            QueryParamMultiValuedMap queryParams) {
        Client client = Client.create();
        WebResource webResource = client.resource(serviceURL);
        webResource = webResource.queryParams(queryParams.getMap());
        return webResource;
    }

    /**
     * Send a GET request and get the plain text in the response
     * 
     * @param webResource
     *            a web resource
     * @return the text from the response
     * @throws BillingException
     *             if the GET request fails
     */
    public String getTextResponse(WebResource webResource)
            throws BillingException {

        ClientResponse response = sendGetRequest(webResource
                .accept(MediaType.TEXT_PLAIN_TYPE));
        try {
            return response.getEntity(String.class);
        } catch (RuntimeException e) {
            throw new BillingException(
                    "Error when processing response from File Billing Application",
                    e);
        }
    }

    /**
     * Send a GET request and get the file content, which is contained in the
     * response as octet stream
     * 
     * @param webResource
     *            a web resource
     * @return the file content
     * @throws BillingException
     *             if the GET request fails
     */
    public byte[] getFileResponse(WebResource webResource)
            throws BillingException {

        ClientResponse response = sendGetRequest(webResource
                .accept(MediaType.APPLICATION_OCTET_STREAM));

        try {
            InputStream is = response.getEntityInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int next = is.read();
            while (next > -1) {
                bos.write(next);
                next = is.read();
            }
            bos.flush();

            byte[] result = bos.toByteArray();
            if (result.length > 0) {
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new BillingException(
                    "Error when processing file response from File Billing Application",
                    e);
        }
    }

    /**
     * Send a GET request
     * 
     * @param httpInterface
     *            a http interface
     * @return the GET response
     * @throws BillingException
     *             if the GET request fails
     */
    ClientResponse sendGetRequest(UniformInterface httpInterface)
            throws BillingException {

        ClientResponse response;
        try {
            response = httpInterface.get(ClientResponse.class);
        } catch (Exception e) {
            throw new BillingException(
                    "Call to File Billing Application failed",
                    new RuntimeException("HTTP call failed"));
        }

        if (response.getStatus() != 200) {
            throw new BillingException(
                    "Call to File Billing Application failed",
                    new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus()));
        }

        return response;
    }

    /**
     * Send a GET request and decode the price model data, which are contained
     * as XML String in the GET response
     * 
     * @param webResource
     *            a web resource
     * @return the price model data
     * @throws BillingException
     *             if the GET request fails
     */
    @SuppressWarnings("unchecked")
    public List<String> getPriceModelData(WebResource webResource)
            throws BillingException {

        String xml = getTextResponse(webResource);
        return (List<String>) decodeXml(xml);
    }

    /**
     * Decode a Java object, which is contained in the given XML
     * 
     * @param xml
     *            a XML string
     * @return a Java object
     * @throws BillingException
     *             if the XML decoding fails
     */
    Object decodeXml(String xml) throws BillingException {

        if (xml != null) {
            try (XMLDecoder xmlDecoder = new XMLDecoder(
                    new ByteArrayInputStream(
                            xml.getBytes(StandardCharsets.UTF_8)))) {
                return xmlDecoder.readObject();
            } catch (Exception e) {
                throw new BillingException(
                        "File application response decoding error.");
            }
        } else {
            return null;
        }
    }

}
