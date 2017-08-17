/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 22.12.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.payproc.stubs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.NameValuePair;

/**
 * Test stub for apache http client post method.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class PostMethodStub {

    /**
     * The content to be returned by the string.
     */
    private static String stubReturnValue = "";

    /**
     * The detail data for the method's request body.
     */
    private static NameValuePair[] requestBody;

    /**
     * Only purpose is to reset the members.
     */
    public PostMethodStub() {
        super();
    }

    /**
     * Resets the static members to null.
     */
    public static void reset() {
        requestBody = null;
        stubReturnValue = null;
    }

    public InputStream getResponseBodyAsStream() throws IOException {
        return new ByteArrayInputStream(stubReturnValue.getBytes("utf-8"));
    }

    /**
     * Sets the value to be returned by the stub.
     * 
     * @param value
     */
    public static void setStubReturnValue(String value) {
        stubReturnValue = value;
    }

    public void setRequestBody(NameValuePair[] parametersBody)
            throws IllegalArgumentException {
        requestBody = parametersBody;
    }


    public String getResponseBodyAsString() throws IOException {
        return stubReturnValue;
    }

    public static NameValuePair[] getRequestBodyDetails() {
        return requestBody;
    }

}
