package org.oscm.rest.service.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.vo.VOEventDefinition;
import org.oscm.internal.vo.VOParameterDefinition;
import org.oscm.internal.vo.VORoleDefinition;
import org.oscm.internal.vo.VOTechnicalService;
import org.oscm.internal.vo.VOTechnicalServiceOperation;
import org.oscm.rest.common.Representation;

public class TechnicalServiceRepresentation extends Representation {

    private List<EventDefinitionRepresentation> eventDefinitions = new ArrayList<EventDefinitionRepresentation>();
    private String technicalServiceId;
    private String technicalServiceBuildId;
    private ServiceAccessType accessType;
    private String technicalServiceDescription;
    private String baseUrl;
    private String provisioningUrl;
    private String loginPath;
    private String provisioningVersion;
    private List<ParameterDefinitionRepresentation> parameterDefinitions = new ArrayList<ParameterDefinitionRepresentation>();
    private List<RoleDefinitionRepresentation> roleDefinitions = new ArrayList<RoleDefinitionRepresentation>();
    private List<String> tags = new ArrayList<String>();
    private String license;
    private String accessInfo;
    private String billingIdentifier;
    private List<OperationRepresentation> technicalServiceOperations = new ArrayList<OperationRepresentation>();
    private boolean externalBilling;

    private transient VOTechnicalService vo;

    public TechnicalServiceRepresentation() {
        this(new VOTechnicalService());
    }

    public TechnicalServiceRepresentation(VOTechnicalService technicalService) {
        vo = technicalService;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setAccessInfo(getAccessInfo());
        vo.setAccessType(getAccessType());
        vo.setBaseUrl(getBaseUrl());
        vo.setBillingIdentifier(getBillingIdentifier());
        vo.setEventDefinitions(updateEvents());
        vo.setExternalBilling(isExternalBilling());
        vo.setKey(convertIdToKey());
        vo.setLicense(getLicense());
        vo.setLoginPath(getLoginPath());
        vo.setParameterDefinitions(updateParameters());
        vo.setProvisioningUrl(getProvisioningUrl());
        vo.setProvisioningVersion(getProvisioningVersion());
        vo.setRoleDefinitions(updateRoles());
        vo.setTags(getTags());
        vo.setTechnicalServiceBuildId(getTechnicalServiceBuildId());
        vo.setTechnicalServiceDescription(getTechnicalServiceDescription());
        vo.setTechnicalServiceId(getTechnicalServiceId());
        vo.setTechnicalServiceOperations(updateOperations());
        vo.setVersion(convertETagToVersion());
    }

    private List<VOTechnicalServiceOperation> updateOperations() {
        List<VOTechnicalServiceOperation> result = new ArrayList<VOTechnicalServiceOperation>();
        if (technicalServiceOperations == null) {
            return result;
        }
        for (OperationRepresentation or : technicalServiceOperations) {
            or.update();
            result.add(or.getVO());
        }
        return result;
    }

    private List<VORoleDefinition> updateRoles() {
        List<VORoleDefinition> result = new ArrayList<VORoleDefinition>();
        if (roleDefinitions == null) {
            return result;
        }
        for (RoleDefinitionRepresentation r : roleDefinitions) {
            r.update();
            result.add(r.getVO());
        }
        return result;
    }

    private List<VOParameterDefinition> updateParameters() {
        List<VOParameterDefinition> result = new ArrayList<VOParameterDefinition>();
        if (parameterDefinitions == null) {
            return result;
        }
        for (ParameterDefinitionRepresentation p : parameterDefinitions) {
            p.update();
            result.add(p.getVO());
        }
        return result;
    }

    private List<VOEventDefinition> updateEvents() {
        List<VOEventDefinition> result = new ArrayList<VOEventDefinition>();
        if (eventDefinitions == null) {
            return result;
        }
        for (EventDefinitionRepresentation e : eventDefinitions) {
            e.update();
            result.add(e.getVO());
        }
        return result;
    }

    @Override
    public void convert() {
        setAccessInfo(vo.getAccessInfo());
        setAccessType(vo.getAccessType());
        setBaseUrl(vo.getBaseUrl());
        setBillingIdentifier(vo.getBillingIdentifier());
        setEventDefinitions(convertEvents());
        setExternalBilling(vo.isExternalBilling());
        setId(Long.valueOf(vo.getKey()));
        setLicense(vo.getLicense());
        setLoginPath(vo.getLoginPath());
        setParameterDefinitions(convertParameters());
        setProvisioningUrl(vo.getProvisioningUrl());
        setProvisioningVersion(vo.getProvisioningVersion());
        setRoleDefinitions(convertRoles());
        setETag(Long.valueOf(vo.getVersion()));
        setTags(vo.getTags());
        setTechnicalServiceBuildId(vo.getTechnicalServiceBuildId());
        setTechnicalServiceDescription(vo.getTechnicalServiceDescription());
        setTechnicalServiceId(vo.getTechnicalServiceId());
        setTechnicalServiceOperations(convertOperations());
    }

    private List<OperationRepresentation> convertOperations() {
        List<OperationRepresentation> result = new ArrayList<OperationRepresentation>();
        if (vo.getTechnicalServiceOperations() == null) {
            return result;
        }
        for (VOTechnicalServiceOperation o : vo.getTechnicalServiceOperations()) {
            OperationRepresentation op = new OperationRepresentation(o);
            op.convert();
            result.add(op);
        }
        return result;
    }

    private List<RoleDefinitionRepresentation> convertRoles() {
        List<RoleDefinitionRepresentation> result = new ArrayList<RoleDefinitionRepresentation>();
        if (vo.getRoleDefinitions() == null) {
            return result;
        }
        for (VORoleDefinition r : vo.getRoleDefinitions()) {
            RoleDefinitionRepresentation rr = new RoleDefinitionRepresentation(r);
            rr.convert();
            result.add(rr);
        }
        return result;
    }

    private List<ParameterDefinitionRepresentation> convertParameters() {
        List<ParameterDefinitionRepresentation> result = new ArrayList<ParameterDefinitionRepresentation>();
        if (vo.getParameterDefinitions() == null) {
            return result;
        }
        for (VOParameterDefinition p : vo.getParameterDefinitions()) {
            ParameterDefinitionRepresentation pr = new ParameterDefinitionRepresentation(p);
            pr.convert();
            result.add(pr);
        }
        return result;
    }

    private List<EventDefinitionRepresentation> convertEvents() {
        List<EventDefinitionRepresentation> result = new ArrayList<EventDefinitionRepresentation>();
        if (vo.getEventDefinitions() == null) {
            return result;
        }
        for (VOEventDefinition e : vo.getEventDefinitions()) {
            EventDefinitionRepresentation er = new EventDefinitionRepresentation(e);
            er.convert();
            result.add(er);
        }
        return result;
    }

    public VOTechnicalService getVO() {
        return vo;
    }

    public List<EventDefinitionRepresentation> getEventDefinitions() {
        return eventDefinitions;
    }

    public void setEventDefinitions(List<EventDefinitionRepresentation> eventDefinitions) {
        this.eventDefinitions = eventDefinitions;
    }

    public String getTechnicalServiceId() {
        return technicalServiceId;
    }

    public void setTechnicalServiceId(String technicalServiceId) {
        this.technicalServiceId = technicalServiceId;
    }

    public String getTechnicalServiceBuildId() {
        return technicalServiceBuildId;
    }

    public void setTechnicalServiceBuildId(String technicalServiceBuildId) {
        this.technicalServiceBuildId = technicalServiceBuildId;
    }

    public ServiceAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(ServiceAccessType accessType) {
        this.accessType = accessType;
    }

    public String getTechnicalServiceDescription() {
        return technicalServiceDescription;
    }

    public void setTechnicalServiceDescription(String technicalServiceDescription) {
        this.technicalServiceDescription = technicalServiceDescription;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getProvisioningUrl() {
        return provisioningUrl;
    }

    public void setProvisioningUrl(String provisioningUrl) {
        this.provisioningUrl = provisioningUrl;
    }

    public String getLoginPath() {
        return loginPath;
    }

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public String getProvisioningVersion() {
        return provisioningVersion;
    }

    public void setProvisioningVersion(String provisioningVersion) {
        this.provisioningVersion = provisioningVersion;
    }

    public List<ParameterDefinitionRepresentation> getParameterDefinitions() {
        return parameterDefinitions;
    }

    public void setParameterDefinitions(List<ParameterDefinitionRepresentation> parameterDefinitions) {
        this.parameterDefinitions = parameterDefinitions;
    }

    public List<RoleDefinitionRepresentation> getRoleDefinitions() {
        return roleDefinitions;
    }

    public void setRoleDefinitions(List<RoleDefinitionRepresentation> roleDefinitions) {
        this.roleDefinitions = roleDefinitions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAccessInfo() {
        return accessInfo;
    }

    public void setAccessInfo(String accessInfo) {
        this.accessInfo = accessInfo;
    }

    public String getBillingIdentifier() {
        return billingIdentifier;
    }

    public void setBillingIdentifier(String billingIdentifier) {
        this.billingIdentifier = billingIdentifier;
    }

    public List<OperationRepresentation> getTechnicalServiceOperations() {
        return technicalServiceOperations;
    }

    public void setTechnicalServiceOperations(List<OperationRepresentation> technicalServiceOperations) {
        this.technicalServiceOperations = technicalServiceOperations;
    }

    public boolean isExternalBilling() {
        return externalBilling;
    }

    public void setExternalBilling(boolean externalBilling) {
        this.externalBilling = externalBilling;
    }

    public static Collection<TechnicalServiceRepresentation> toCollection(List<VOTechnicalService> technicalServices) {
        List<TechnicalServiceRepresentation> result = new ArrayList<TechnicalServiceRepresentation>();
        for (VOTechnicalService ts : technicalServices) {
            result.add(new TechnicalServiceRepresentation(ts));
        }
        return result;
    }
}
