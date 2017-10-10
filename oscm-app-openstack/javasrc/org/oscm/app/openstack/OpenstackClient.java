/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 06.10.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import static java.lang.System.getProperty;
import static org.openstack4j.model.common.Identifier.byName;
import static org.oscm.app.openstack.proxy.ProxySettings.HTTPS_PROXY_HOST;
import static org.oscm.app.openstack.proxy.ProxySettings.HTTPS_PROXY_PASSWORD;
import static org.oscm.app.openstack.proxy.ProxySettings.HTTPS_PROXY_PORT;
import static org.oscm.app.openstack.proxy.ProxySettings.HTTPS_PROXY_USER;
import static org.oscm.app.openstack.proxy.ProxySettings.useProxyByPass;
import static org.oscm.string.Strings.isEmpty;

import java.net.MalformedURLException;
import java.net.URL;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.client.IOSClientBuilder.V3;
import org.openstack4j.core.transport.Config;
import org.openstack4j.core.transport.ProxyHost;
import org.openstack4j.model.compute.QuotaSet;
import org.openstack4j.model.compute.QuotaSetUpdate;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Role;
import org.openstack4j.model.identity.v3.User;
import org.openstack4j.openstack.OSFactory;
import org.oscm.app.openstack.controller.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kulle
 *
 */
public class OpenstackClient {

    private static final Logger LOG = LoggerFactory
            .getLogger(OpenstackClient.class);

    private static final String ROLE_ADMIN = "admin";

    private OSClientV3 client;

    private PropertyHandler ph;

    public OpenstackClient(PropertyHandler ph) throws MalformedURLException {
        this.ph = ph;
        Config config = Config.newConfig();
        if (useProxy() && !useProxyByPass(new URL(ph.getKeystoneUrl()))) {
            config = config.withProxy(newProxyHost());
        }
        V3 credentials = OSFactory.builderV3().endpoint(ph.getKeystoneUrl())
                .withConfig(config).credentials(ph.getUserName(),
                        ph.getPassword(), byName(ph.getDomainName()));
        client = credentials.authenticate();
    }

    private boolean useProxy() {
        String port = getProperty(HTTPS_PROXY_PORT);
        if (port == null) {
            return false;
        }
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            LOG.warn("Found invalid proxy port: " + port);
            return false;
        }

        String proxyHost = getProperty(HTTPS_PROXY_HOST);
        if (proxyHost == null || proxyHost.trim().length() == 0) {
            return false;
        }

        return true;
    }

    private ProxyHost newProxyHost() {
        String user = getProperty(HTTPS_PROXY_USER);
        String password = getProperty(HTTPS_PROXY_PASSWORD);
        String host = getProperty(HTTPS_PROXY_HOST);
        int port = Integer.parseInt(getProperty(HTTPS_PROXY_PORT));

        if (!isEmpty(user) && !isEmpty(password)) {
            return ProxyHost.of(host, port, user, password);
        }
        return ProxyHost.of(host, port);
    }

    public Project createProject() {
        return client.identity().projects()
                .create(Builders.project().name(ph.getProjectName())
                        .description("OSCM").enabled(true).build());
    }

    public User createUser() {
        return client.identity().users()
                .create(Builders.user().name(ph.getProjectUser())
                        .password(ph.getProjectUserPwd()).build());
    }

    public void addUserToProject(String projectId, String userId) {
        Role adminRole = client.identity().roles().getByName(ROLE_ADMIN).get(0);
        client.identity().roles().grantProjectUserRole(projectId, userId,
                adminRole.getId());
    }

    public void updateQuota(String projectId, int numInst) {
        QuotaSetUpdate qs = Builders.quotaSet().instances(numInst).build();
        client.compute().quotaSets().updateForTenant(projectId, qs);
    }

    public void deleteUser() {
        client.identity().users().delete(ph.getProjectUserId());
    }

    public void deleteProject() {
        client.identity().projects().delete(ph.getProjectId());
    }

    public SimpleTenantUsage getUsage(String projectId, String startTime, String endTime) {
        return client.compute().quotaSets().getTenantUsage(projectId, startTime, endTime);
    }

    public QuotaSet getQuotas(String projectId) {
        return client.compute().quotaSets().get(projectId);
    }

}
