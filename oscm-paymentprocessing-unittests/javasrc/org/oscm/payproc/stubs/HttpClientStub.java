/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 18.12.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.payproc.stubs;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * @author Mike J&auml;ger
 * 
 */
public class HttpClientStub{

    public static boolean throwIOException = false;
    public static boolean throwHTTPException = false;

    public int executeMethod(HttpUriRequest request) throws IOException,
            HttpException {
        if (throwHTTPException) {
            throw new HttpException("exception caused by test");
        }
        if (throwIOException) {
            throw new IOException("exception caused by test");
        }
        return 0;
    }

    /**
     * Resets the test related members to default.
     */
    public static void reset() {
        throwIOException = false;
        throwHTTPException = false;
    }

}
