/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 2013-11-28                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.common.i18n.Messages;
import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.v1_0.exceptions.APPlatformException;

/**
 * Keystone is the identity service used by OpenStack for authentication and
 * authorization.
 */
public class KeystoneClient {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(KeystoneClient.class);

    final static String TYPE_HEAT = "orchestration";
    final static String NAME_HEAT = "heat";

    final static String TYPE_NOVA = "compute";
    final static String NAME_NOVA = "nova";

    private final OpenStackConnection connection;

    /**
     * 
     * @param connection
     *            The connection that needs to be authenticated
     */
    public KeystoneClient(OpenStackConnection connection) {
        this.connection = connection;
    }

    /**
     * Authenticate the connection that was given in the constructor.
     * 
     * @param user
     * @param password
     * @param tenantName
     * 
     * @throws HeatException
     */
    public void authenticate(String user, String password, String tenantName)
            throws HeatException, APPlatformException {
        LOGGER.debug("KeystoneClient.authenticate() user: " + user
                + "  tenant:" + tenantName + "  endpoint: "
                + connection.getKeystoneEndpoint());
        String uri = connection.getKeystoneEndpoint() + "/tokens";
        JSONObject request = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject passwordCredentials = new JSONObject();
        try {
            passwordCredentials.put("username", user);
            passwordCredentials.put("password", password);
            auth.put("passwordCredentials", passwordCredentials);
            auth.put("tenantName", tenantName);
            request.put("auth", auth);
        } catch (JSONException e) {
            // this can basically not happen with string parameters
            throw new RuntimeException(e);
        }
        RESTResponse response = connection.processRequest(uri, "POST",
                request.toString());

        if (response.getResponseCode() != 200) {
            throw new RuntimeException(
                    "Failed to retrieve token for authentication, response code "
                            + response.getResponseCode());
        }

        String body = response.getResponseBody();
        try {
            String heatEndpoint = null;
            String novaEndpoint = null;
            JSONObject jsonObj = new JSONObject(body);
            JSONObject access = jsonObj.getJSONObject("access");
            JSONObject token = access.getJSONObject("token");
            String authToken = token.getString("id");
            JSONArray catalog = access.getJSONArray("serviceCatalog");
            int catalogSize = catalog.length();
            for (int i = 0; i < catalogSize; i++) {
                JSONObject entry = catalog.getJSONObject(i);
                if (entry != null) {
                    String type = entry.getString("type");
                    String name = entry.getString("name");
                    if (TYPE_HEAT.equals(type)) {
                        JSONArray endpoints = entry.getJSONArray("endpoints");
                        int endpointSize = endpoints.length();
                        for (int j = 0; j < endpointSize; j++) {
                            JSONObject endpoint = endpoints.getJSONObject(j);
                            if (endpoint != null) {
                                String publicURL = endpoint
                                        .getString("publicURL");
                                if (publicURL != null
                                        && publicURL.trim().length() > 0) {
                                    heatEndpoint = publicURL;
                                }
                            }
                        }
                    } else if (TYPE_NOVA.equals(type)) {
                        JSONArray endpoints = entry.getJSONArray("endpoints");
                        int endpointSize = endpoints.length();
                        for (int j = 0; j < endpointSize; j++) {
                            JSONObject endpoint = endpoints.getJSONObject(j);
                            if (endpoint != null) {
                                String publicURL = endpoint
                                        .getString("publicURL");
                                if (publicURL != null
                                        && publicURL.trim().length() > 0) {
                                    novaEndpoint = publicURL;
                                }
                            }
                        }
                    }
                }
            }
            if (heatEndpoint == null) {
                LOGGER.error("KeystoneClient.authenticate() heat endpoint not defined");
                throw new APPlatformException(
                        Messages.getAll("error_missing_heat_endpoint"));
            } else {
                LOGGER.debug("KeystoneClient.authenticate() heat endpoint: "
                        + heatEndpoint);
            }
            if (novaEndpoint == null) {
                LOGGER.error("KeystoneClient.authenticate() nova endpoint not defined");
                throw new APPlatformException(
                        Messages.getAll("error_missing_nova_endpoint"));
            } else {
                LOGGER.debug("KeystoneClient.authenticate() nova endpoint: "
                        + novaEndpoint);
            }
            connection.useAuthentication(authToken);
            connection.setHeatEndpoint(heatEndpoint);
            connection.setNovaEndpoint(novaEndpoint);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
