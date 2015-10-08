/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: Jul 3, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import org.oscm.app.openstack.controller.OpenStackStatus;

/**
 * @author Dirk Bernsau
 * 
 */
public class MockURLStreamHandler extends URLStreamHandler {

    private final Map<String, MockHttpURLConnection> connection;

    public MockURLStreamHandler() {
        connection = new HashMap<String, MockHttpURLConnection>();
        put("/tokens", new MockHttpURLConnection(200, respTokens(true, true)));
        put("/stacks/instanceName", new MockHttpURLConnection(200,
                respStacksInstanceName(OpenStackStatus.CREATE_COMPLETE, true)));
        put("/stacks/Instance4", new MockHttpURLConnection(200,
                respStacksInstanceName(OpenStackStatus.CREATE_COMPLETE, true)));
        put("/stacks/Instance4/resources", new MockHttpURLConnection(200,
                respStacksResources(true, "serverId")));
        put("/servers/serverId", new MockHttpURLConnection(200,
                respServer("Instance4")));
        put("/stacks/Instance4/sID/actions", new MockHttpURLConnection(200,
                respStacksInstance4sIdActions()));
        put("/stacks", new MockHttpURLConnection(200, respStacks()));
        put("/templates/fosi_v2.json", new MockHttpURLConnection(200,
                respTemplatesFosi_v2()));
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        URLConnection conn = connection.get(u.getFile());
        if (conn == null) {
            throw new RuntimeException("Mock connection for " + u.getFile()
                    + " not found!");
        }
        return conn;
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        URLConnection conn = connection.get(u.getFile());
        if (conn == null) {
            throw new RuntimeException("Mock connection for " + u.getFile()
                    + " not found!");
        }
        return conn;
    }

    public void put(String url, MockHttpURLConnection mock) {
        connection.put(url, mock);
    }

    public static String respTokens(boolean addPublicURL,
            boolean addPublicURLNova) {
        try {
            JSONObject response = new JSONObject();
            JSONObject access = new JSONObject();
            JSONObject token = new JSONObject();
            JSONArray serviceCatalog = new JSONArray();

            JSONObject endpoints = new JSONObject();
            JSONArray endpointsList = new JSONArray();
            endpoints.put("endpoints", endpointsList);
            if (addPublicURL) {
                JSONObject publicUrl = new JSONObject();
                publicUrl.put("publicURL", "http://heatendpoint/");
                endpointsList.put(publicUrl);
            }

            endpoints.put("type", KeystoneClient.TYPE_HEAT);
            endpoints.put("name", KeystoneClient.NAME_HEAT);
            serviceCatalog.put(endpoints);

            JSONObject endpointsNova = new JSONObject();

            JSONArray endpointsListNova = new JSONArray();
            endpointsNova.put("endpoints", endpointsListNova);
            if (addPublicURLNova) {
                JSONObject publicUrlNova = new JSONObject();
                publicUrlNova.put("publicURL", "http://novaendpoint/");
                endpointsListNova.put(publicUrlNova);
            }

            endpointsNova.put("type", KeystoneClient.TYPE_NOVA);
            endpointsNova.put("name", KeystoneClient.NAME_NOVA);
            serviceCatalog.put(endpointsNova);

            token.put("id", "authId");

            access.put("token", token);
            access.put("serviceCatalog", serviceCatalog);

            response.put("access", access);
            return response.toString();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String respStacksInstanceName(OpenStackStatus status,
            boolean withStatusReason, String... stackStatusReason) {
        String reason;
        if (stackStatusReason == null || stackStatusReason.length == 0) {
            reason = "SSR";
        } else {
            reason = Arrays.toString(stackStatusReason);
        }
        try {
            JSONObject response = new JSONObject();
            JSONObject stack = new JSONObject();
            response.put("stack", stack);
            stack.put("stack_name", "SN");
            stack.put("id", "ID");
            stack.put("stack_status",
                    status == null ? "bullshit" : status.name());
            if (withStatusReason) {
                stack.put("stack_status_reason", reason);
            }
            JSONArray outputs = new JSONArray();
            JSONObject output = new JSONObject();
            output.put("output_key", "OK");
            output.put("output_value", "OV");
            outputs.put(output);
            stack.put("outputs", outputs);
            return response.toString();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String respStacksResources(boolean serverExists,
            String serverId) {
        try {
            JSONObject response = new JSONObject();
            JSONArray resources = new JSONArray();
            response.put("resources", resources);

            if (serverExists) {
                JSONObject server = new JSONObject();
                server.put("resource_name", "server");
                server.put("physical_resource_id", serverId);
                resources.put(server);
            }

            return response.toString();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String respServer(String stackName) {
        try {
            JSONObject response = new JSONObject();
            JSONObject server = new JSONObject();
            response.put("server", server);
            server.put("name", stackName);

            return response.toString();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String respStacksInstance4sIdActions() {
        JSONObject response = new JSONObject();
        return response.toString();
    }

    private static String respStacks() {
        try {
            JSONObject response = new JSONObject();
            JSONObject state = new JSONObject();
            state.put("label", "OK");
            state.put("id", "OK");
            response.put("state", state);
            response.put("id", "1234");
            response.put("stack", "1234");
            response.put("name", "Appliance1");
            response.put("projectUri",
                    "http://test.com/cloud/api/projects/54346");
            JSONObject stack = new JSONObject();
            stack.put("id", "idValue");
            response.put("stack", stack);
            return response.toString();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String respStackDeleted() {
        JSONObject response = new JSONObject();
        return response.toString();
    }

    private static String respTemplatesFosi_v2() {
        return "{\"AWSTemplateFormatVersion\": \"2010-09-09\","
                + "\"Parameters\": {\"InstanceType\" : {"
                + "\"Description\" : \"Server instance type\","
                + "\"Type\" : \"String\",\"Default\" : \"m1.tiny\"},"
                + "\"ImageId\" : {"
                + "\"Description\" : \"The image to start up\","
                + "\"Type\" : \"String\","
                + "\"Default\" : \"3e0e9883-382a-4068-8962-f883b5e39b31\""
                + "},\"KeyName\" : {"
                + "\"Description\" : \"Reference to keypair\","
                + "\"Type\" : \"String\",\"Default\" : \"dirk\"}},"
                + "\"Resources\": {\"Server\": {"
                + "\"Type\" : \"AWS::EC2::Instance\",\"Properties\" : {"
                + "\"InstanceType\" : { \"Ref\" : \"InstanceType\" },"
                + "\"KeyName\" : { \"Ref\" : \"KeyName\" },"
                + "\"ImageId\" : { \"Ref\" : \"ImageId\" }}"
                + "},\"IP\": { \"Type\": \"AWS::EC2::EIP\"},"
                + "\"IP_Assoc\": {\"Type\": \"AWS::EC2::EIPAssociation\","
                + "\"Properties\": {\"EIP\": { \"Ref\": \"IP\" },"
                + "\"InstanceId\": { \"Ref\": \"Server\" }}}},"
                + "\"Outputs\" : {\"IP_Out\" : {"
                + "\"Description\" : \"IP Address of the access host\","
                + "\"Value\" :  { \"Ref\" : \"IP\" }},\"KP_Out\" : {"
                + "\"Description\" : \"Key pair name\","
                + "\"Value\" :  { \"Ref\" : \"KeyName\" }}" + "}" + "}";
    }
}
