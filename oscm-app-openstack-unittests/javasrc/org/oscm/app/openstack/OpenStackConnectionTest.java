/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 04.08.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Before;
import org.junit.Test;
import org.oscm.app.openstack.exceptions.HeatException;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * @author afschar
 *
 */
public class OpenStackConnectionTest {

    @Before
    public void setUp() {
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler());
    }

    @Test
    public void processRequest_wrongURL() {
        // given
        String url = "more bullshit";

        // when
        try {
            new OpenStackConnection("some bullshit")
                    .processRequest(url, "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf("invalid URL") > -1);
            assertTrue(ex.getMessage().indexOf(url) > -1);
            assertEquals(-1, ex.getResponseCode());
        }
    }

    @Test
    public void processRequest_noHTTPConnection() {
        // given
        String url = "http://bild.de";
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new URLConnection(u) {
                    @Override
                    public void connect() throws IOException {
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit")
                    .processRequest(url, "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf(
                    "Expected http(s) connection for URL") > -1);
            assertTrue(ex.getMessage().indexOf(url) > -1);
        }
    }

    @Test
    public void processRequest_IOException401() {
        // given
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new HttpURLConnection(u, (Proxy) null) {
                    @Override
                    public void connect() throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return 401;
                    }

                    @Override
                    public synchronized InputStream getErrorStream() {
                        return new ByteArrayInputStream("401 error occurred"
                                .getBytes());
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit").processRequest(
                    "http://bild.de", "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf("unauthorized") > -1);
            assertTrue(ex.getMessage().indexOf("HTTP 401") > -1);
            assertTrue(ex.getMessage().indexOf("http://bild.de") > -1);
            assertTrue(ex.getMessage().indexOf("401 error occurred") > -1);
        }
    }

    @Test
    public void processRequest_IOException400() {
        // given
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new HttpURLConnection(u, (Proxy) null) {
                    @Override
                    public void connect() throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return 400;
                    }

                    @Override
                    public synchronized InputStream getErrorStream() {
                        return new ByteArrayInputStream("400 error occurred"
                                .getBytes());
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit").processRequest(
                    "http://bild.de", "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf(
                    "either input parameter format error") > -1);
            assertTrue(ex.getMessage().indexOf("HTTP 400") > -1);
            assertTrue(ex.getMessage().indexOf("http://bild.de") > -1);
            assertTrue(ex.getMessage().indexOf("400 error occurred") > -1);
        }
    }

    @Test
    public void processRequest_IOException404() {
        // given
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new HttpURLConnection(u, (Proxy) null) {
                    @Override
                    public void connect() throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return 404;
                    }

                    @Override
                    public synchronized InputStream getErrorStream() {
                        return new ByteArrayInputStream("404 error occurred"
                                .getBytes());
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit").processRequest(
                    "http://bild.de", "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf("resource not found") > -1);
            assertTrue(ex.getMessage().indexOf("HTTP 404") > -1);
            assertTrue(ex.getMessage().indexOf("http://bild.de") > -1);
            assertTrue(ex.getMessage().indexOf("404 error occurred") > -1);
        }
    }

    @Test
    public void processRequest_IOException406() {
        // given
        final String msg = "more of this bullshit!";
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new HttpURLConnection(u, (Proxy) null) {
                    @Override
                    public void connect() throws IOException {
                        throw new IOException(msg);
                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return 406;
                    }

                    @Override
                    public synchronized InputStream getInputStream()
                            throws IOException {
                        return new ByteArrayInputStream("exception bullshit"
                                .getBytes());
                    }

                    @Override
                    public synchronized InputStream getErrorStream() {
                        return new ByteArrayInputStream("406 error occurred"
                                .getBytes());
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit").processRequest(
                    "http://bild.de", "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf("send failed") > -1);
            assertTrue(ex.getMessage().indexOf("HTTP 406") > -1);
            assertTrue(ex.getMessage().indexOf("http://bild.de") > -1);
            assertTrue(ex.getMessage().indexOf("406 error occurred") > -1);
            assertTrue(ex.getMessage().indexOf(msg) > -1);
        }
    }
    @Test
    public void processRequest_IOException504() {
        // given
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new HttpURLConnection(u, (Proxy) null) {
                    @Override
                    public void connect() throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public int getResponseCode() throws IOException {
                        return 504;
                    }

                    @Override
                    public synchronized InputStream getErrorStream() {
                        return new ByteArrayInputStream("504 error occurred"
                                .getBytes());
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit").processRequest(
                    "http://bild.de", "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf("Gateway/proxy timeout") > -1);
            assertTrue(ex.getMessage().indexOf("HTTP 504") > -1);
            assertTrue(ex.getMessage().indexOf("http://bild.de") > -1);
            assertTrue(ex.getMessage().indexOf("504 error occurred") > -1);
        }
    }

    @Test
    public void processRequest_IOException() {
        // given
        final String msg = "get out of my way!";
        OpenStackConnection.setURLStreamHandler(new MockURLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u, Proxy p)
                    throws IOException {
                return new HttpURLConnection(u, (Proxy) null) {
                    @Override
                    public void connect() throws IOException {
                        throw new IOException(msg);
                    }
                };
            }
        });

        // when
        try {
            new OpenStackConnection("some bullshit").processRequest(
                    "http://bild.de", "POST");
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertTrue(ex.getMessage().indexOf("send failed") > -1);
            assertTrue(ex.getMessage().indexOf(msg) > -1);
        }
    }


}
