/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                              
 *  Creation Date: Jul 3, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Dirk Bernsau
 * 
 */
public class MockURLStreamHandler extends URLStreamHandler {

    private final HttpURLConnection connection;
    private StringWriter inputWriter;
    private String output;
    private String requestMethod;
    private String error;
    private String input;
    protected boolean connected = false;
    private URL url;
    private Throwable throwException;
    protected String locationHeader;
    protected int responseCode;

    public MockURLStreamHandler() {
        connection = Mockito.mock(HttpURLConnection.class);
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    protected String process(@SuppressWarnings("unused") String input) {
        // might be overridden to actually return something
        return null;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        url = u;
        inputWriter = new StringWriter();
        Mockito.when(connection.getInputStream()).thenAnswer(
                new Answer<InputStream>() {

                    @Override
                    public InputStream answer(InvocationOnMock invocation)
                            throws Throwable {
                        connect();
                        if (output == null) {
                            output = "";
                        }
                        return new ByteArrayInputStream(output.getBytes());
                    }
                });
        Mockito.when(connection.getErrorStream()).thenAnswer(
                new Answer<InputStream>() {

                    @Override
                    public InputStream answer(InvocationOnMock invocation)
                            throws Throwable {
                        if (error == null) {
                            error = "";
                        }
                        return new ByteArrayInputStream(error.getBytes());
                    }
                });
        Mockito.when(connection.getOutputStream()).thenReturn(
                new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        inputWriter.write(b);
                    }
                });
        if (throwException != null) {
            Mockito.doThrow(throwException).when(connection).connect();
        } else {
            Mockito.doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation)
                        throws Throwable {
                    connect();
                    return null;
                }
            }).when(connection).connect();
        }
        Mockito.doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length > 0) {
                    requestMethod = arguments[0].toString();
                }
                return null;
            }
        }).when(connection).setRequestMethod(Matchers.anyString());

        Mockito.when(connection.getHeaderField(Matchers.eq("Location")))
                .thenAnswer(new Answer<String>() {

                    @Override
                    public String answer(InvocationOnMock invocation)
                            throws Throwable {
                        connect();
                        return locationHeader;
                    }
                });
        Mockito.when(Integer.valueOf(connection.getResponseCode())).thenAnswer(
                new Answer<Integer>() {

                    @Override
                    public Integer answer(InvocationOnMock invocation)
                            throws Throwable {
                        connect();
                        return Integer.valueOf(responseCode);
                    }
                });
        return connection;
    }

    private void connect() {
        if (!connected) {
            connected = true;
            output = process(input = inputWriter.toString());
        }
    }

    public String getInput() {
        if (!connected) {
            throw new IllegalStateException("Not connected");
        }
        return input;
    }

    /**
     * @param throwException
     *            the throwException to set
     */
    public void setException(Throwable throwException) {
        this.throwException = throwException;
        if (throwException != null) {
            error = throwException.getMessage();
        }
    }

    /**
     * Returns the request method that was set when calling.
     * 
     * @return the request method
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Returns the request URI that was called.
     * 
     * @return the request URI
     */
    public String getRequestUri() {
        return url.toString();
    }
}
