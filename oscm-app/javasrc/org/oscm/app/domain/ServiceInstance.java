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
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.i18n.Messages;
import org.oscm.app.v2_0.data.InstanceStatus;
import org.oscm.app.v2_0.data.Setting;
import org.oscm.string.Strings;

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
    @SequenceGenerator(name = "do_seq", allocationSize = 1000)
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
     * The identifier of the subscription specified by the customer.
     */
    private String referenceId;

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
    @Enumerated(EnumType.STRING)
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
    private static final String ROLLBACK_SUBSCRIPTIONID = "ROLLBACK_SUBSCRIPTIONID";

    /**
     * The instance related parameters.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<InstanceParameter> instanceParameters = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<InstanceAttribute> instanceAttributes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance", fetch = FetchType.LAZY)
    private List<Operation> operations = new ArrayList<>();

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

    public String getReferenceId() {
        return referenceId;
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

    void setTkey(long tkey) {
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

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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

    public List<InstanceAttribute> getInstanceAttributes() {
        return instanceAttributes;
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
    public HashMap<String, Setting> getParameterMap() throws BadResultException {
        final HashMap<String, Setting> map = new HashMap<>();
        for (InstanceParameter param : instanceParameters) {
            map.put(param.getParameterKey(),
                    new Setting(param.getParameterKey(), param
                            .getDecryptedValue(), param.isEncrypted()));
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
    public void setInstanceParameters(HashMap<String, Setting> parameters)
            throws BadResultException {
        if (parameters != null) {
            List<InstanceParameter> instanceParamList = new ArrayList<>();
            for (String key : parameters.keySet()) {
                if (key != null) {
                    InstanceParameter ip = getParameterForKey(key);
                    Setting param = parameters.get(key);
                    if (ip != null) { // Existing parameter
                        if (!ip.getDecryptedValue().equals(param.getValue())) {
                            // Changed => Update
                            ip.setEncrypted(param.isEncrypted());
                            ip.setDecryptedValue(param.getValue());
                        }
                    } else { // Added parameter
                        ip = new InstanceParameter();
                        ip.setParameterKey(key);
                        ip.setEncrypted(param.isEncrypted());
                        ip.setDecryptedValue(param.getValue());
                        ip.setServiceInstance(this);
                    }
                    instanceParamList.add(ip);
                }
            }
            this.setInstanceParameters(instanceParamList);
        }
    }

    /**
     * Returns the instance attributes with the given key, <code>null</code> in
     * case no attribute with that key could be found.
     * 
     * @param attributeKey
     *            The key of the requested attribute.
     * @return The instance attribute with the given key.
     */
    InstanceAttribute getAttributeForKey(String attributeKey) {
        for (InstanceAttribute attr : instanceAttributes) {
            if (attr.getAttributeKey().equals(attributeKey)) {
                return attr;
            }
        }
        return null;
    }

    /**
     * Returns all attributes as map.
     * 
     * @return map from attribute keys to their corresponding values.
     * @throws BadResultException
     */
    public HashMap<String, Setting> getAttributeMap() throws BadResultException {
        final HashMap<String, Setting> map = new HashMap<>();
        for (InstanceAttribute attr : instanceAttributes) {
            map.put(attr.getAttributeKey(),
                    new Setting(attr.getAttributeKey(), attr
                            .getDecryptedValue(), attr.isEncrypted(), attr
                            .getControllerId()));
        }
        return map;
    }

    /**
     * Persist all modified attributes.
     * 
     * @param attributes
     *            all the service specific settings
     * 
     * @throws BadResultException
     */
    public void setInstanceAttributes(HashMap<String, Setting> attributes)
            throws BadResultException {
        if (attributes != null) {
            List<InstanceAttribute> instanceAttrList = new ArrayList<>();
            for (String key : attributes.keySet()) {
                if (key != null) {
                    InstanceAttribute ia = getAttributeForKey(key);
                    Setting attr = attributes.get(key);
                    if (ia != null) { // Existing parameter
                        if (!ia.getDecryptedValue().equals(attr.getValue())) {
                            // Changed => Update
                            ia.setEncrypted(attr.isEncrypted());
                            ia.setDecryptedValue(attr.getValue());
                        }
                    } else { // Added parameter
                        ia = new InstanceAttribute();
                        ia.setAttributeKey(key);
                        ia.setEncrypted(attr.isEncrypted());
                        ia.setDecryptedValue(attr.getValue());
                        ia.setServiceInstance(this);
                    }
                    instanceAttrList.add(ia);
                }
            }
            this.setInstanceAttributes(instanceAttrList);
        }
    }

    public void setInstanceAttributes(List<InstanceAttribute> instanceAttributes) {
        this.instanceAttributes = instanceAttributes;
    }

    void removeParams(HashMap<String, Setting> parameters,
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
                if (!getRunWithTimer()) {
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
        return ProvisioningStatus.COMPLETED.equals(getProvisioningStatus());
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

        Properties rollbackProps;
        String xmlProps = getRollbackParameters();
        if (xmlProps != null && xmlProps.length() != 0) {
            rollbackProps = this.convertXMLToProperties(xmlProps);
            String rollbackSID = rollbackProps
                    .getProperty(ROLLBACK_SUBSCRIPTIONID);
            rollbackProps.remove(ROLLBACK_SUBSCRIPTIONID);

            rollbackInstanceParameters(rollbackProps, em);
            rollbackInstanceAttributes(em);
            rollbackSubscriptionId(rollbackSID);
        } else {
            throw new BadResultException(Messages.get(getDefaultLocale(),
                    "error_missing_rollbackparameters", this.getInstanceId()));
        }
    }

    private void rollbackInstanceAttributes(EntityManager em) {
        List<InstanceAttribute> instanceAttributes = getInstanceAttributes();
        if (instanceAttributes != null) {
            Iterator<InstanceAttribute> iterator = instanceAttributes.iterator();
            while (iterator.hasNext()) {
                InstanceAttribute next = iterator.next();
                iterator.remove();
                em.remove(next);
            }
        }
    }

    private void rollbackInstanceParameters(
            Properties rollbackInstanceParameters, EntityManager em)
            throws BadResultException {

        HashMap<String, Setting> rollbackParams = new HashMap<>();

        for (String name : rollbackInstanceParameters.stringPropertyNames()) {
            rollbackParams.put(name, new Setting(name,
                    rollbackInstanceParameters.getProperty(name)));
        }
        this.removeParams(rollbackParams, em);
        this.setInstanceParameters(rollbackParams);
        this.setRollbackParameters(null);

    }

    private void rollbackSubscriptionId(String subscriptionID)
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
        String xmlString;
        try (OutputStream out = new ByteArrayOutputStream()) {
            properties.storeToXML(out, null, "UTF-8");
            xmlString = out.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return xmlString;
    }

    Properties convertXMLToProperties(String xmlString) {
        Properties properties = new Properties();
        try (InputStream in = new ByteArrayInputStream(xmlString.getBytes())) {
            properties.loadFromXML(in);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return properties;
    }
}
