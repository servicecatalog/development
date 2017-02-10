/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-11-08                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.iaas.data.DiskImage;
import org.oscm.app.iaas.data.ResourceType;
import org.oscm.app.iaas.exceptions.CommunicationException;
import org.oscm.app.iaas.exceptions.IaasException;
import org.oscm.app.iaas.exceptions.MissingResourceException;
import org.oscm.app.ror.data.LOperation;
import org.oscm.app.ror.data.LParameter;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.data.LPlatformDescriptor;
import org.oscm.app.ror.data.LPlatformDescriptorConfiguration;
import org.oscm.app.ror.data.RORDiskImage;
import org.oscm.app.ror.exceptions.RORException;

public class RORClient {

	private static final Logger logger = LoggerFactory
			.getLogger(RORClient.class);

	private HashMap<String, String> values = new HashMap<String, String>();
	private String auth;
	private String apiUrl;
	private String locale;
	private static final String HTTPS_PROXY_HOST = "https.proxyHost";
	private static final String HTTPS_PROXY_PORT = "https.proxyPort";
	private static final String HTTPS_PROXY_USER = "https.proxyUser";
	private static final String HTTPS_PROXY_PASSWORD = "https.proxyPassword";
	private static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";

	public RORClient(String url, String tenantId, String user, String password,
			String locale) {
		String basicAuth = user + ":" + password;
		auth = new String(Base64.encodeBase64(basicAuth.getBytes()));
		values.put("userId", user);
		values.put("orgId", tenantId);
		apiUrl = url;
		this.locale = locale;
	}

	/**
	 * @return a map containing basic parameters
	 */
	public HashMap<String, String> getBasicParameters() {
		return new HashMap<String, String>(values);
	}

	public NameValuePair[] createParameters(HashMap<String, String> values) {
		NameValuePair[] parameters = new NameValuePair[values.size() + 2];
		int i = 2;
		parameters[0] = new NameValuePair(LParameter.VERSION, "2.0");
		parameters[1] = new NameValuePair(LParameter.LOCALE, locale);
		for (String key : values.keySet()) {
			parameters[i++] = new NameValuePair(key, values.get(key));
		}
		return parameters;
	}

	public XMLConfiguration execute(HashMap<String, String> values)
			throws IaasException {
		try {
			traceAPI(values);
			XMLConfiguration result = new XMLConfiguration();
			GetMethod get = new GetMethod(apiUrl);
			HttpClient client = createHttpClient();
			get.setRequestHeader("Authorization", "Basic " + auth);
			get.setQueryString(createParameters(values));
			client.executeMethod(get);
			getRespStream(result, get);
			String status = result.getString("responseStatus");
			if (!"SUCCESS".equals(status)) {
				if ("RESOURCE_NOT_FOUND".equals(status)) {
					createMissingResourceException(
							result.getString("responseMessage"), values);
				}
				throw new RORException("Command failed: "
						+ values.get(LParameter.ACTION) + " Status: " + status
						+ " Message: " + result.getString("responseMessage"));
			}
			return result;
		} catch (UnknownHostException e) {
			throw new CommunicationException(e.getMessage(), e.getMessage());
		} catch (RuntimeException | IOException | ConfigurationException e) {
			throw new RORException("Command failed: "
					+ values.get(LParameter.ACTION) + " Message: "
					+ e.getMessage());
		}
	}

	public HttpClient createHttpClient() {
		HttpClient client = new HttpClient();

		String proxyHost = System.getProperty(HTTPS_PROXY_HOST);
		String proxyPort = System.getProperty(HTTPS_PROXY_PORT);
		String proxyUser = System.getProperty(HTTPS_PROXY_USER);
		String proxyPassword = System.getProperty(HTTPS_PROXY_PASSWORD);
		int proxyPortInt = 0;

		try {
			proxyPortInt = Integer.parseInt(proxyPort);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (!useProxyByPass(this.apiUrl)) {
			if (proxyHost != null && proxyPortInt > 0) {
				client.getHostConfiguration().setProxy(proxyHost, proxyPortInt);

				if (proxyUser != null && proxyUser.length() > 0
						&& proxyPassword != null && proxyPassword.length() > 0) {
					HttpState state = new HttpState();
					Credentials proxyCredentials = new UsernamePasswordCredentials(
							proxyUser, proxyPassword);
					state.setProxyCredentials(new AuthScope(proxyHost,
							proxyPortInt), proxyCredentials);
					client.setState(state);
				}
			}
		}
		return client;
	}

	public boolean useProxyByPass(String url) {
		String nonProxy = System.getProperty(HTTP_NON_PROXY_HOSTS);
		if (nonProxy != null) {
			String[] split = nonProxy.split("\\|");
			for (int i = 0; i < split.length; i++) {
				String np = split[i].trim();
				if (np.length() > 0) {
					boolean wcStart = np.startsWith("*");
					boolean wcEnd = np.endsWith("*");
					if (wcStart) {
						np = np.substring(1);
					}
					if (wcEnd) {
						np = np.substring(0, np.length() - 1);
					}
					if (wcStart && wcEnd && url.contains(np)) {
						return true;
					}
					if (wcStart && url.endsWith(np)) {
						return true;
					}
					if (wcEnd && url.startsWith(np)) {
						return true;
					}
					if (np.equals(url)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void getRespStream(XMLConfiguration result, GetMethod get)
			throws ConfigurationException, IOException {
		InputStream in = null;
		try {
			in = get.getResponseBodyAsStream();
			result.load(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

	public List<String> listServerTypes() throws IaasException {
		HashMap<String, String> request = getBasicParameters();
		request.put(LParameter.ACTION, LOperation.LIST_SERVER_TYPE);
		XMLConfiguration response = execute(request);
		List<String> result = new LinkedList<String>();
		if (response != null) {
			List<HierarchicalConfiguration> types = response
					.configurationsAt("servertypes.servertype");
			for (HierarchicalConfiguration type : types) {
				String name = type.getString("name");
				if (name != null && name.trim().length() > 0) {
					result.add(name);
				}
			}
		}
		return result;
	}

	/**
	 * @param instanceName
	 * @param descriptorId
	 * @return the ID of the created LPlatform
	 * @throws RORException
	 */
	public String createLPlatform(String instanceName, String descriptorId)
			throws IaasException {
		HashMap<String, String> request = getBasicParameters();
		request.put(LParameter.ACTION, LOperation.CREATE_LPLATFORM);
		request.put(LParameter.LPLATFORM_NAME, instanceName);
		request.put(LParameter.LPLATFORM_DESCR_ID, descriptorId);
		XMLConfiguration result = execute(request);
		return result.getString("lplatformId");
	}

	/**
	 * Returns a list of all available LPlatform configurations.
	 * 
	 * @param verbose
	 *            set to <code>true</code> to retrieve extended information
	 * @return the list of configurations (may be empty but not
	 *         <code>null</code>)
	 * @throws RORException
	 */
	public List<LPlatformConfiguration> listLPlatforms(boolean verbose)
			throws IaasException {
		HashMap<String, String> request = getBasicParameters();
		request.put(LParameter.ACTION, LOperation.LIST_LPLATFORM);
		request.put(LParameter.VERBOSE, (verbose ? "true" : "false"));
		XMLConfiguration result = execute(request);

		List<LPlatformConfiguration> resultList = new LinkedList<LPlatformConfiguration>();
		if (result != null) {
			List<HierarchicalConfiguration> platforms = result
					.configurationsAt("lplatforms.lplatform");
			for (HierarchicalConfiguration platform : platforms) {
				resultList.add(new LPlatformConfiguration(platform));
			}
		}
		return resultList;
	}

	/**
	 * Retrieves a list of templates available for the tenant.
	 * 
	 * @return the list
	 * @throws RORException
	 */
	public List<LPlatformDescriptor> listLPlatformDescriptors()
			throws IaasException {
		HashMap<String, String> request = getBasicParameters();
		request.put(LParameter.ACTION, LOperation.LIST_LPLATFORM_DESCR);
		XMLConfiguration result = execute(request);

		List<LPlatformDescriptor> resultList = new LinkedList<LPlatformDescriptor>();
		if (result != null) {
			List<HierarchicalConfiguration> descriptors = result
					.configurationsAt("lplatformdescriptors.lplatformdescriptor");
			for (HierarchicalConfiguration descriptor : descriptors) {
				resultList.add(new LPlatformDescriptor(descriptor));
			}
		}
		return resultList;
	}

	/**
	 * Retrieves a list of disk images available for the tenant.
	 * 
	 * @return the list
	 * @throws RORException
	 */
	public List<DiskImage> listDiskImages() throws IaasException {
		HashMap<String, String> request = getBasicParameters();
		request.put(LParameter.ACTION, LOperation.LIST_DISKIMAGE);
		XMLConfiguration result = execute(request);

		List<DiskImage> resultList = new LinkedList<DiskImage>();
		if (result != null) {
			List<HierarchicalConfiguration> images = result
					.configurationsAt("diskimages.diskimage");
			for (HierarchicalConfiguration image : images) {
				resultList.add(new RORDiskImage(image));
			}
		}
		return resultList;
	}

	/**
	 * Retrieves a list of templates available for the tenant.
	 * 
	 * @param descriptorId
	 * @return the list
	 * @throws RORException
	 */
	public LPlatformDescriptorConfiguration getLPlatformDescriptorConfiguration(
			String descriptorId) throws IaasException {
		HashMap<String, String> request = getBasicParameters();
		request.put(LParameter.ACTION, LOperation.GET_LPLATFORM_DESCR_CONFIG);
		request.put(LParameter.LPLATFORM_DESCR_ID, descriptorId);
		XMLConfiguration result = execute(request);
		return new LPlatformDescriptorConfiguration(
				result.configurationAt("lplatformdescriptor"));
	}

	public void traceAPI(HashMap<String, String> request) {
		if (logger.isTraceEnabled()) {
			StringBuffer sb = new StringBuffer("Sending API request:");
			for (String key : request.keySet()) {
				sb.append("  ");
				sb.append(key);
				sb.append(" => ");
				sb.append(request.get(key));
			}
			logger.trace(sb.toString());
		}
	}

	public void createMissingResourceException(String message,
			HashMap<String, String> values) throws MissingResourceException {
		ResourceType type = ResourceType.UNKNOWN;
		String id = null;
		if (message != null) {
			message = message.trim();
			if (message.startsWith("VSYS10050")) {
				type = ResourceType.VSYSTEM;
				id = values.get(LParameter.LPLATFORM_ID);
			} else if (message.startsWith("VSYS10069")
					|| message.startsWith("VSYS10060")) {
				type = ResourceType.VSERVER;
				id = values.get(LParameter.LSERVER_ID);
			}
		}
		throw new MissingResourceException(message, type, id);
	}
}
