/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 06.10.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.System.getProperty;
import static org.openstack4j.model.common.Identifier.byId;
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
import org.openstack4j.core.transport.Config;
import org.openstack4j.core.transport.ProxyHost;
import org.openstack4j.model.compute.QuotaSet;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.compute.builder.QuotaSetUpdateBuilder;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Role;
import org.openstack4j.model.identity.v3.User;
import org.openstack4j.model.storage.block.builder.BlockQuotaSetBuilder;
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
        client = authenticate(ph.getUserName(), ph.getPassword(), null);
    }

    private OSClientV3 authenticate(String user, String password,
            String projectId) throws MalformedURLException {

        Config config = Config.newConfig();
        if (useProxy() && !useProxyByPass(new URL(ph.getKeystoneUrl()))) {
            config = config.withProxy(newProxyHost());
        }

        if (isNullOrEmpty(projectId)) {
            return OSFactory.builderV3().endpoint(ph.getKeystoneUrl())
                    .withConfig(config)
                    .credentials(user, password, byName(ph.getDomainName()))
                    .authenticate();

        }

        return OSFactory.builderV3().endpoint(ph.getKeystoneUrl())
                .withConfig(config)
                .credentials(user, password, byName(ph.getDomainName()))
                .scopeToProject(byId(projectId)).authenticate();
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
        Project project = client.identity().projects()
                .create(Builders.project().name(ph.getProjectName())
                        .description("Managed by OSCM").enabled(true).build());
        ph.setProjectId(project.getId());
        return project;
    }

    public User createUser() {
        User user = client.identity().users()
                .create(Builders.user().name(ph.getProjectUser())
                        .description("Managed by OSCM")
                        .password(ph.getProjectUserPwd()).build());
        ph.setProjectUserId(user.getId());
        return user;
    }

    public void addUserToProject() {
        Role adminRole = client.identity().roles().getByName(ROLE_ADMIN).get(0);
        client.identity().roles().grantProjectUserRole(ph.getProjectId(),
                ph.getProjectUserId(), adminRole.getId());
    }

    public void updateQuota() throws MalformedURLException {
        OSClientV3 client = authenticate(ph.getProjectUser(),
                ph.getProjectUserPwd(), ph.getProjectId());

        QuotaSetUpdateBuilder builder = Builders.quotaSet();
        if (!isNullOrEmpty(ph.getQuotaCores())) {
            builder.cores(Integer.valueOf(ph.getQuotaCores()));
        }
        if (!isNullOrEmpty(ph.getQuotaInstances())) {
            builder.instances(Integer.valueOf(ph.getQuotaInstances()));
        }
        if (!isNullOrEmpty(ph.getQuotaIp())) {
            builder.floatingIps(Integer.valueOf(ph.getQuotaIp()));
        }
        if (!isNullOrEmpty(ph.getQuotaKeys())) {
            builder.keyPairs(Integer.valueOf(ph.getQuotaKeys()));
        }
        if (!isNullOrEmpty(ph.getQuotaRam())) {
            builder.ram(Integer.valueOf(ph.getQuotaRam()));
        }
        client.compute().quotaSets().updateForTenant(ph.getProjectId(),
                builder.build());

        BlockQuotaSetBuilder blockBuilder = Builders.blockQuotaSet();
        boolean updateBlockQuota = false;
        if (!isNullOrEmpty(ph.getQuotaVolumes())) {
            blockBuilder.volumes(Integer.valueOf(ph.getQuotaVolumes()));
            updateBlockQuota = true;
        }
        if (!isNullOrEmpty(ph.getQuotaGb())) {
            blockBuilder.gigabytes(Integer.valueOf(ph.getQuotaGb()));
            updateBlockQuota = true;
        }
        if (updateBlockQuota) {
            client.blockStorage().quotaSets().updateForTenant(ph.getProjectId(),
                    blockBuilder.build());
        }
    }

    public void getQuota() throws MalformedURLException {
        OSClientV3 client = authenticate(ph.getProjectUser(),
                ph.getProjectUserPwd(), ph.getProjectId());
        QuotaSet qs = client.compute().quotaSets().get(ph.getProjectId());
        ph.setQuotaCores(String.valueOf(qs.getCores()));
        ph.setQuotaGb(String.valueOf(qs.getGigabytes()));
        ph.setQuotaInstances(String.valueOf(qs.getInstances()));
        ph.setQuotaIp(String.valueOf(qs.getFloatingIps()));
        ph.setQuotaKeys(String.valueOf(qs.getKeyPairs()));
        ph.setQuotaRam(String.valueOf(qs.getRam()));
        ph.setQuotaVolumes(String.valueOf(qs.getVolumes()));
    }

    public void deleteUser() {
        client.identity().users().delete(ph.getProjectUserId());
    }

    public void deleteProject() {
        client.identity().projects().delete(ph.getProjectId());
    }

    public SimpleTenantUsage getUsage(String startTime, String endTime) {
        return client.compute().quotaSets().getTenantUsage(ph.getProjectId(),
                startTime, endTime);
    }

    public QuotaSet getQuotas(String projectId) {
        return client.compute().quotaSets().get(projectId);
    }

}
