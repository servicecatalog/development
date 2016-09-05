/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 12.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.*;

import org.oscm.app.converters.PSConverter;
import org.oscm.string.Strings;
import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.i18n.Messages;
import org.oscm.app.v1_0.data.InstanceStatus;

/**
 * Represents a service instance that was requested to be created by BES.
 * 
 * @author Mike J&auml;ger
 * 
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "ServiceInstance.getForStatusWithTimer", query = "SELECT si FROM ServiceInstance si WHERE si.provisioningStatus IN (:status) AND si.runWithTimer = true ORDER BY tkey"),
        @NamedQuery(name = "ServiceInstance.getForSuspendedByApp", query = "SELECT si FROM ServiceInstance si WHERE si.suspendedByApp = true ORDER BY tkey"),
        @NamedQuery(name = "ServiceInstance.getLockedService", query = "SELECT si FROM ServiceInstance si WHERE si.locked = true AND si.controllerId = :cid"),
        @NamedQuery(name = "ServiceInstance.getForKey", query = "SELECT si FROM ServiceInstance si WHERE si.instanceId = :key"),
        @NamedQuery(name = "ServiceInstance.getForSubscriptionAndOrg", query = "SELECT si FROM ServiceInstance si WHERE si.subscriptionId = :subscriptionId AND si.organizationId = :organizationId"),
        @NamedQuery(name = "ServiceInstance.getForCtrlKey", query = "SELECT si FROM ServiceInstance si WHERE si.instanceId = :key AND si.controllerId = :cid"),
        @NamedQuery(name = "ServiceInstance.getAllForCtrl", query = "SELECT si FROM ServiceInstance si WHERE si.controllerId = :cid") })
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = 4298435124486600408L;

    /**
     * The technical key of the entity.
     */
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "do_seq")
    @TableGenerator(table = "hibernate_sequences", name = "do_seq", allocationSize = 1000)
    private long tkey;

    /**
     * The identifier of the organization the instance is created for.
     */
    @Column(nullable = false)
    private String organizationId;

    /**
     * The name of the organization the instance is created for.
     */
    private String organizationName;

    /**
     * The identifier of the subscription in BES that corresponds to the current
     * service instance.
     */
    @Column(nullable = false)
    private String subscriptionId;

    /**
     * The default locale to use when no different locale is requested in the
     * call.
     */
    @Column(nullable = false)
    private String defaultLocale;

    /**
     * The login URL of BES the created service instance can redirect users to.
     */
    private String besLoginURL;

    /**
     * Reflects the current status of the provisioning process for this service
     * instance.
     */
    @Column(nullable = false)
    @Convert(converter = PSConverter.class)
    private ProvisioningStatus provisioningStatus;

    /**
     * Contains the time the provisioning request was created.
     */
    @Column(nullable = false)
    private long requestTime;

    /**
     * The instance id generated for identifying the provisioned environment.
     */
    @Column(nullable = false)
    private String instanceId;

    /**
     * The controller id identifying the controller implementation handling the
     * actual provisioning process.
     */
    @Column(nullable = false)
    private String controllerId;

    /**
     * Defines whether the service is currently locked for some exclusive
     * operation
     */
    @Column(nullable = false)
    private boolean locked;

    /**
     * Defines whether the service should receive a periodical timer signal
     * during its current operation.
     */
    @Column(nullable = false)
    private boolean runWithTimer = true;

    /**
     * Defines whether the service has requested explicit provisioning calls
     * into the service instance itself.
     */
    @Column(nullable = false)
    private boolean instanceProvisioning = false;

    /**
     * Defines whether the application controller has completed the current
     * provisioning process. Might by <code>true</code> but when instance
     * provisioning is still to be executed the provisioning status is not set
     * to completed.
     */
    @Column(nullable = false)
    private boolean controllerReady = false;

    @Column
    private String rollbackParameters;

    @Column(nullable = false)
    private boolean suspendedByApp = false;

    @Version
    @Column(nullable = false)
    private int version;

    public int getVersion() {
        return version;
    }

    /**
     * The access info returned by the service.
     */
    private String serviceAccessInfo;

    /**
     * The base URL returned by the service.
     */
    private String serviceBaseURL;

    /**
     * The login path returned by the service.
     */
    private String serviceLoginPath;

    /**
     * SubscriptionID key for rollbackParameters
     */
    public static final String ROLLBACK_SUBSCRIPTIONID = "ROLLBACK_SUBSCRIPTIONID";

    /**
     * The instance related parameters.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<InstanceParameter> instanceParameters = new ArrayList<InstanceParameter>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<Operation> operations = new ArrayList<Operation>();

    public long getTkey() {
        return tkey;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public String getBesLoginURL() {
        return besLoginURL;
    }

    public String getRollbackParameters() {
        return rollbackParameters;
    }

    public void setRollbackParameters(String rollbackParameters) {
        this.rollbackParameters = rollbackParameters;
    }

    public void setTkey(long tkey) {
        this.tkey = tkey;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setBesLoginURL(String loginUrl) {
        this.besLoginURL = loginUrl;
    }

    public ProvisioningStatus getProvisioningStatus() {
        return provisioningStatus;
    }

    public void setProvisioningStatus(ProvisioningStatus provisioningStatus) {
        this.provisioningStatus = provisioningStatus;
        if (provisioningStatus.equals(ProvisioningStatus.COMPLETED)) {
            setLocked(false);
        }
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public List<InstanceParameter> getInstanceParameters() {
        return instanceParameters;
    }

    public void setInstanceParameters(List<InstanceParameter> instanceParameters) {
        this.instanceParameters = instanceParameters;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getServiceAccessInfo() {
        return serviceAccessInfo;
    }

    public String getServiceBaseURL() {
        return serviceBaseURL;
    }

    public String getServiceLoginPath() {
        return serviceLoginPath;
    }

    public void setServiceAccessInfo(String serviceAccessInfo) {
        this.serviceAccessInfo = serviceAccessInfo;
    }

    public void setServiceBaseURL(String serviceBaseURL) {
        this.serviceBaseURL = serviceBaseURL;
    }

    public void setServiceLoginPath(String serviceLoginPath) {
        this.serviceLoginPath = serviceLoginPath;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getRunWithTimer() {
        return runWithTimer;
    }

    public void setRunWithTimer(boolean runWithTimer) {
        this.runWithTimer = runWithTimer;
    }

    public boolean isInstanceProvisioning() {
        return instanceProvisioning;
    }

    public void setInstanceProvisioning(boolean instanceProvisioning) {
        this.instanceProvisioning = instanceProvisioning;
    }

    public boolean isControllerReady() {
        return controllerReady;
    }

    public void setControllerReady(boolean controllerReady) {
        this.controllerReady = controllerReady;
    }

    public boolean isSuspendedByApp() {
        return suspendedByApp;
    }

    public void setSuspendedByApp(boolean suspendedByApp) {
        this.suspendedByApp = suspendedByApp;
    }

    public boolean isDeleted() {
        return getSubscriptionId().contains("#");
    }

    /**
     * Returns the instance parameter with the given key, <code>null</code> in
     * case no parameter with that key could be found.
     * 
     * @param parameterKey
     *            The key of the requested parameter.
     * @return The instance parameter with the given key.
     */
    public InstanceParameter getParameterForKey(String parameterKey) {
        for (InstanceParameter param : instanceParameters) {
            if (param.getParameterKey().equals(parameterKey)) {
                return param;
            }
        }
        return null;
    }

    /**
     * Returns all parameters as map.
     * 
     * @return map from parameter keys to their corresponding values.
     * @throws BadResultException
     */
    public HashMap<String, String> getParameterMap() throws BadResultException {
        final HashMap<String, String> map = new HashMap<String, String>();
        for (InstanceParameter param : instanceParameters) {
            map.put(param.getParameterKey(), param.getDecryptedValue());
        }
        return map;
    }

    /**
     * Persist all modified parameters.
     * 
     * @param parameters
     *            all the service specific settings
     * 
     * @throws BadResultException
     */
    public void setInstanceParameters(HashMap<String, String> parameters)
            throws BadResultException {
        if (parameters != null) {
            List<InstanceParameter> instanceParamList = new ArrayList<>();
            for (String key : parameters.keySet()) {
                if (key != null) {
                    InstanceParameter ip = getParameterForKey(key);
                    String value = parameters.get(key);
                    if (ip != null) { // Existing parameter
                        if (!ip.getDecryptedValue().equals(value)) {
                            // Changed => Update
                            ip.setDecryptedValue(value);
                        }
                    } else { // Added parameter
                        ip = new InstanceParameter();
                        ip.setParameterKey(key);
                        ip.setDecryptedValue(value);
                        ip.setServiceInstance(this);
                    }
                    instanceParamList.add(ip);
                }
            }
            this.setInstanceParameters(instanceParamList);
        }
    }

    public void removeParams(HashMap<String, String> parameters,
            EntityManager em) {

        List<InstanceParameter> paramsToRemove = new ArrayList<>();
        List<InstanceParameter> params = this.getInstanceParameters();

        if (parameters != null) {
            for (InstanceParameter ip : this.getInstanceParameters()) {
                if (!parameters.containsKey(ip.getParameterKey())) {
                    paramsToRemove.add(ip);
                    em.remove(ip);
                }
            }
        }
        params.removeAll(paramsToRemove);
    }

    /**
     * Update service status.
     * 
     * @param em
     *            the entity manager
     * @param status
     *            the current instance status
     */
    public void updateStatus(EntityManager em, InstanceStatus status) {
        if (isDeleted()) {
            return;
        }

        if (status != null) {
            if (getServiceAccessInfo() == null
                    && status.getAccessInfo() != null) {
                setServiceAccessInfo(status.getAccessInfo());
            } else if (getServiceAccessInfo() != null
                    && status.getAccessInfo() != null
                    && !getServiceAccessInfo().equals(status.getAccessInfo())) {
                setServiceAccessInfo(status.getAccessInfo());
            }
            if (getRunWithTimer() != status.getRunWithTimer()) {
                setRunWithTimer(status.getRunWithTimer());
                if (getRunWithTimer() == false) {
                    setLocked(false);
                }

            }
            em.persist(this);
        }
    }

    /**
     * Check whether the service instance is currently available (e.g. not
     * processed by some other operation).
     */
    public boolean isAvailable() {
        if (ProvisioningStatus.COMPLETED.equals(getProvisioningStatus())) {
            return true;
        }
        return false;
    }

    /**
     * If the instanceId is not available (creation), return the instance key.
     */
    public String getIdentifier() {
        if (!Strings.isEmpty(instanceId)) {
            return instanceId;
        }

        return Long.valueOf(tkey).toString();
    }

    public void markForDeletion() {
        if (!getSubscriptionId().contains("#")) {
            setSubscriptionId(subscriptionId + "#" + UUID.randomUUID());
            setLocked(false);
        }
    }

    public void unmarkForDeletion() {
        if (getSubscriptionId() != null) {
            setSubscriptionId(getSubscriptionId().split("#")[0]);
        }
    }

    public String getOriginalSubscriptionId() {

        int index = subscriptionId.indexOf("#");
        if (index >= 0) {
            return subscriptionId.substring(0, index);
        } else {
            return subscriptionId;
        }
    }

    public void prepareRollback() throws BadResultException {
        Properties actualProperties = new Properties();
        actualProperties.put(ROLLBACK_SUBSCRIPTIONID, this.getSubscriptionId());
        actualProperties.putAll(this.getParameterMap());
        this.setRollbackParameters(this
                .convertPropertiesToXML(actualProperties));
    }

    public void rollbackServiceInstance(EntityManager em)
            throws BadResultException {

        Properties rollbackProps = new Properties();
        String xmlProps = getRollbackParameters();
        if (xmlProps != null && xmlProps.length() != 0) {
            rollbackProps = this.convertXMLToProperties(xmlProps);
            String rollbackSID = rollbackProps
                    .getProperty(ROLLBACK_SUBSCRIPTIONID);
            rollbackProps.remove(ROLLBACK_SUBSCRIPTIONID);

            rollbackInstanceParameters(rollbackProps, em);
            rollbackSubscriptionId(rollbackSID);
        } else {
            throw new BadResultException(Messages.get(getDefaultLocale(),
                    "error_missing_rollbackparameters", this.getInstanceId()));
        }
    }

    public void rollbackInstanceParameters(
            Properties rollbackInstanceParameters, EntityManager em)
            throws BadResultException {

        HashMap<String, String> rollbackParams = new HashMap<String, String>();

        for (String name : rollbackInstanceParameters.stringPropertyNames()) {
            rollbackParams.put(name,
                    rollbackInstanceParameters.getProperty(name));
        }
        this.removeParams(rollbackParams, em);
        this.setInstanceParameters(rollbackParams);
        this.setRollbackParameters(null);

    }

    public void rollbackSubscriptionId(String subscriptionID)
            throws BadResultException {
        if (subscriptionID != null && !subscriptionID.equals("")) {
            setSubscriptionId(subscriptionID);
        } else {
            throw new BadResultException(Messages.get(getDefaultLocale(),
                    "error_missing_subscriptionId", this.getInstanceId()));
        }
    }

    String convertPropertiesToXML(Properties properties)
            throws RuntimeException {
        String xmlString = null;
        try (OutputStream out = new ByteArrayOutputStream()) {
            properties.storeToXML(out, null, "UTF-8");
            xmlString = out.toString();
        } catch (IOException e) {
            RuntimeException re = new RuntimeException();
            throw re;
        }
        return xmlString;
    }

    Properties convertXMLToProperties(String xmlString) {
        Properties properties = new Properties();
        try (InputStream in = new ByteArrayInputStream(xmlString.getBytes())) {
            properties.loadFromXML(in);
        } catch (IOException e) {
            RuntimeException re = new RuntimeException();
            throw re;
        }
        return properties;
    }
}
