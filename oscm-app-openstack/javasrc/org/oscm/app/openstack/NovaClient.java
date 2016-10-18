/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 27.09.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.oscm.app.openstack.controller.PropertyHandler;
import org.oscm.app.openstack.data.Server;
import org.oscm.app.openstack.exceptions.OpenStackConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tateiwamext
 *
 */
public class NovaClient {

    private final OpenStackConnection connection;
    private final Logger logger = LoggerFactory.getLogger(NovaClient.class);

    /**
     * @param connection
     */
    public NovaClient(OpenStackConnection connection) {
        this.connection = connection;
    }

    /**
     * startServer This method request to OpenStack to start Instance.
     * 
     * @param ph
     *            PropertyHandler: This is used for information when CTMG get
     *            Heat Exception.
     * @param serverId
     *            String: The ID of Instance(VM) which is used for POST start
     *            API
     * @return Boolean: If the request is successful, return Boolean.TRUE. If
     *         the request is failed, return Boolean.FALSE.
     */
    public Boolean startServer(PropertyHandler ph, String serverId) {
        String uri;
        try {
            uri = connection.getNovaEndpoint() + "/servers/"
                    + URLEncoder.encode(serverId, "UTF-8") + "/action";

            connection.processRequest(uri, "POST", "{\"os-start\": null}");
            logger.debug("Start server: " + serverId);
            return Boolean.TRUE;
        } catch (UnsupportedEncodingException e) {
            logger.error("Runtime error happened during encoding", e);
            throw new RuntimeException(e);
        } catch (OpenStackConnectionException e) {
            logger.info(
                    "Could not start server (Server ID:" + serverId
                            + ") in stack (Stack ID: " + ph.getStackId() + ")",
                    e);
        }
        return Boolean.FALSE;
    }

    /**
     * stopServer This method request to OpenStack to stop instance.
     * 
     * @param ph
     *            PropertyHandler: This is used for information when CTMG get
     *            Heat Exception.
     * @param serverId
     *            String: The ID of Instance(VM) which is used for POST start
     *            API
     * @return Boolean: If the request is successful, return Boolean.TRUE. If
     *         the request is failed, return Boolean.FALSE.
     */
    public Boolean stopServer(PropertyHandler ph, String serverId) {
        String uri;
        try {
            uri = connection.getNovaEndpoint() + "/servers/"
                    + URLEncoder.encode(serverId, "UTF-8") + "/action";
            connection.processRequest(uri, "POST", "{\"os-stop\": null}");
            logger.debug("Stop server: " + serverId);
            return Boolean.TRUE;
        } catch (UnsupportedEncodingException e) {
            logger.error("Runtime error happened during encoding", e);
            throw new RuntimeException(e);
        } catch (OpenStackConnectionException e) {
            logger.info(
                    "Could not stop server (Server ID:" + serverId
                            + ") in stack (Stack ID: " + ph.getStackId() + ")",
                    e);
        }
        return Boolean.FALSE;
    }

    /**
     * Get server details
     * 
     * @param ph
     * @param serverId
     * @return Server object which contain id, name and status
     */
    public Server getServerDetails(PropertyHandler ph, String serverId) {
        String uri;
        Server result = new Server();
        result.setId(serverId);
        try {
            uri = connection.getNovaEndpoint() + "/servers/"
                    + URLEncoder.encode(serverId, "UTF-8");

            RESTResponse response = connection.processRequest(uri, "GET");
            String body = response.getResponseBody();
            logger.debug("NovaClient.getServerDetails() Responsecode: "
                    + response.getResponseCode());
            JSONObject responseJson = new JSONObject(body);
            JSONObject server = responseJson.getJSONObject("server");
            result.setId(server.getString("id"));
            result.setStatus(server.getString("status"));
            result.setName(server.getString("name"));

            return result;
        } catch (UnsupportedEncodingException e) {
            logger.error("Runtime error happened during encoding", e);
            throw new RuntimeException(e);
        } catch (JSONException e) {
            logger.error("NovaClient.getServerDetails() JSONException occurred",
                    e);
        } catch (OpenStackConnectionException e) {
            logger.error(
                    "NovaClient.getServerDetails() Could not get server status (Server ID:"
                            + serverId + ") in stack (Stack ID: "
                            + ph.getStackId() + ")",
                    e);
        }
        result.setName("");
        result.setStatus("-1");
        return result;
    }

}
