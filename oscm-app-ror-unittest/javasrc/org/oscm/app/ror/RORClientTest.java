/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: 12.03.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.Before;
import org.junit.Test;

import org.oscm.app.iaas.data.ResourceType;
import org.oscm.app.iaas.exceptions.MissingResourceException;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LParameter;

/**
 * @author iversen
 * 
 */
public class RORClientTest {

	private RORClient vdcClient;
	private final String CLIENT_URL = "CLIENT_URL";
	private final String CLIENT_TENANTID = "CLIENT_TENANTID";
	private final String CLIENT_USER = "CLIENT_USER";
	private final String CLIENT_PASSWORD = "CLIENT_PASSWORD";
	private final String CLIENT_LOCALE = "en";
	private final String HTTPS_PROXY_HOST = "https.proxyHost";
	private final String HTTPS_PROXY_HOST_VALUE = "proxy";
	private final String HTTPS_PROXY_PORT = "https.proxyPort";
	private final String HTTPS_PROXY_PORT_VALUE = "8080";
	private final String HTTPS_PROXY_USER = "https.proxyUser";
	private final String HTTPS_PROXY_USER_VALUE = "user";
	private final String HTTPS_PROXY_PASSWORD = "https.proxyPassword";
	private final String HTTPS_PROXY_PASSWORD_VALUE = "password";
	private final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";

	@Before
	public void setup() throws Exception {
		System.clearProperty(HTTPS_PROXY_HOST);
		System.clearProperty(HTTPS_PROXY_PORT);
		System.clearProperty(HTTPS_PROXY_USER);
		System.clearProperty(HTTPS_PROXY_PASSWORD);
		System.clearProperty(HTTP_NON_PROXY_HOSTS);
		vdcClient = new RORClient(CLIENT_URL, CLIENT_TENANTID, CLIENT_USER,
				CLIENT_PASSWORD, CLIENT_LOCALE);
	}

	@Test
	public void createMissingResourceException_VSYS10060() throws Exception {
		// given
		String errormessage = "VSYS10060";
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(LParameter.LSERVER_ID, "SERVER01");

		// when
		try {
			vdcClient.createMissingResourceException(errormessage, parameters);
		}
		// then
		catch (MissingResourceException e) {
			assertEquals(errormessage, e.getMessage());
			assertEquals("SERVER01", e.getResouceId());
			assertEquals(ResourceType.VSERVER, e.getResouceType());
		}
	}

	@Test
	public void createMissingResourceException_VSYS10050() throws Exception {
		// given
		String errormessage = "VSYS10050";
		String platformID = "PLATFORM1";
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(LParameter.LPLATFORM_ID, platformID);

		// when
		try {
			vdcClient.createMissingResourceException(errormessage, parameters);
		}
		// then
		catch (MissingResourceException e) {
			assertEquals(errormessage, e.getMessage());
			assertEquals(platformID, e.getResouceId());
			assertEquals(ResourceType.VSYSTEM, e.getResouceType());
		}
	}

	@Test
	public void createClient() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_HOST_VALUE);
		System.setProperty(HTTPS_PROXY_PORT, HTTPS_PROXY_PORT_VALUE);
		System.setProperty(HTTPS_PROXY_USER, HTTPS_PROXY_USER_VALUE);
		System.setProperty(HTTPS_PROXY_PASSWORD, HTTPS_PROXY_PASSWORD_VALUE);
		Credentials proxyCredentials = new UsernamePasswordCredentials(
				HTTPS_PROXY_USER_VALUE, HTTPS_PROXY_PASSWORD_VALUE);
		AuthScope authScope = new AuthScope(HTTPS_PROXY_HOST_VALUE,
				Integer.parseInt(HTTPS_PROXY_PORT_VALUE));

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertEquals(HTTPS_PROXY_HOST_VALUE, client.getHostConfiguration()
				.getProxyHost());
		assertEquals(HTTPS_PROXY_PORT_VALUE,
				String.valueOf(client.getHostConfiguration().getProxyPort()));
		assertEquals(proxyCredentials,
				client.getState().getProxyCredentials(authScope));

	}

	@Test
	public void createClient_WithEmptyCredentials() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_HOST_VALUE);
		System.setProperty(HTTPS_PROXY_PORT, HTTPS_PROXY_PORT_VALUE);
		System.setProperty(HTTPS_PROXY_USER, "");
		System.setProperty(HTTPS_PROXY_PASSWORD, "");

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertEquals(HTTPS_PROXY_HOST_VALUE, client.getHostConfiguration()
				.getProxyHost());
		assertEquals(HTTPS_PROXY_PORT_VALUE,
				String.valueOf(client.getHostConfiguration().getProxyPort()));
		assertNull(client.getState().getProxyCredentials(AuthScope.ANY));

	}

	@Test
	public void createClient_WithoutCredentials() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_HOST_VALUE);
		System.setProperty(HTTPS_PROXY_PORT, HTTPS_PROXY_PORT_VALUE);

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertEquals(HTTPS_PROXY_HOST_VALUE, client.getHostConfiguration()
				.getProxyHost());
		assertEquals(HTTPS_PROXY_PORT_VALUE,
				String.valueOf(client.getHostConfiguration().getProxyPort()));
		assertNull(client.getState().getProxyCredentials(AuthScope.ANY));

	}

	@Test
	public void createClient_MissingPassword() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_HOST_VALUE);
		System.setProperty(HTTPS_PROXY_PORT, HTTPS_PROXY_PORT_VALUE);
		System.setProperty(HTTPS_PROXY_USER, HTTPS_PROXY_USER_VALUE);

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertEquals(HTTPS_PROXY_HOST_VALUE, client.getHostConfiguration()
				.getProxyHost());
		assertEquals(HTTPS_PROXY_PORT_VALUE,
				String.valueOf(client.getHostConfiguration().getProxyPort()));
		assertNull(client.getState().getProxyCredentials(AuthScope.ANY));
	}

	@Test
	public void createClient_MissingUser() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_HOST_VALUE);
		System.setProperty(HTTPS_PROXY_PORT, HTTPS_PROXY_PORT_VALUE);
		System.setProperty(HTTPS_PROXY_USER, HTTPS_PROXY_USER_VALUE);

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertEquals(HTTPS_PROXY_HOST_VALUE, client.getHostConfiguration()
				.getProxyHost());
		assertEquals(HTTPS_PROXY_PORT_VALUE,
				String.valueOf(client.getHostConfiguration().getProxyPort()));
		assertNull(client.getState().getProxyCredentials(AuthScope.ANY));
	}

	@Test
	public void createClient_MissingHost() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_PORT, HTTPS_PROXY_PORT_VALUE);
		System.setProperty(HTTPS_PROXY_USER, HTTPS_PROXY_PASSWORD_VALUE);
		System.setProperty(HTTPS_PROXY_PASSWORD, HTTPS_PROXY_PASSWORD_VALUE);

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertNull(client.getHostConfiguration().getProxyHost());
		assertEquals(-1, client.getHostConfiguration().getProxyPort());
		assertNull(client.getState().getProxyCredentials(AuthScope.ANY));
	}

	@Test
	public void createClient_NFE() throws Exception {
		// given
		System.setProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_HOST_VALUE);
		System.setProperty(HTTPS_PROXY_PORT, "");
		System.setProperty(HTTPS_PROXY_USER, HTTPS_PROXY_PASSWORD_VALUE);
		System.setProperty(HTTPS_PROXY_PASSWORD, HTTPS_PROXY_PASSWORD_VALUE);

		// when
		HttpClient client = vdcClient.createHttpClient();

		// then
		assertNull(client.getHostConfiguration().getProxyHost());
		assertEquals(-1, client.getHostConfiguration().getProxyPort());
		assertNull(client.getState().getProxyCredentials(AuthScope.ANY));
	}

	@Test
	public void testNonProxyMatcher() throws Exception {

		String endpoint = "https://ror-demo.fujitsu.com:8014/cfmgapi/endpoint";
		testNonProxy(endpoint, null, false);
		testNonProxy(endpoint, "", false);
		testNonProxy(endpoint, "localhost", false);
		testNonProxy(endpoint, "localhost|127.0.0.1", false);
		testNonProxy(endpoint, endpoint, true);
		testNonProxy(endpoint, "https://ror-demo*", true);
		testNonProxy(endpoint, "https://ror-demo2*", false);
		testNonProxy(endpoint, "*.fujitsu.com:8014/cfmgapi/endpoint", true);
		testNonProxy(endpoint, "*.fujitsu.com:8014| https://ror-demo*", true);
		testNonProxy(endpoint, "*.fujitsu.com:8014*", true);
		testNonProxy(endpoint, "*.fujitsu.com", false);
		testNonProxy(endpoint, "*.fujitsu.jp.*", false);
	}

	private void testNonProxy(String endpoint, String setting,
			boolean expectation) {

		// given
		setNonProxy(setting);

		// when
		boolean result = vdcClient.useProxyByPass(endpoint);

		// then
		if (expectation) {
			assertTrue(
					"Expected, that " + setting + " would match " + endpoint,
					result);
		} else {
			assertFalse("Expected, that " + setting + " would not match "
					+ endpoint, result);
		}
	}

	private void setNonProxy(String value) {
		if (value != null) {
			System.setProperty("http.nonProxyHosts", value);
		} else {
			System.clearProperty("http.nonProxyHosts");
		}
	}
}
