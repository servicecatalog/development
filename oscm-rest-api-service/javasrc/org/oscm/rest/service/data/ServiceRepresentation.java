package org.oscm.rest.service.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.OfferingType;
import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.ServiceType;
import org.oscm.internal.vo.VOParameter;
import org.oscm.internal.vo.VOService;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RepresentationCollection;

public class ServiceRepresentation extends Representation {

    private List<ParameterRepresentation> parameters = new ArrayList<ParameterRepresentation>();
    private String description;
    private String name;
    private String serviceId;
    private String technicalId;
    private ServiceStatus status;
    private ServiceAccessType accessType;
    private String shortDescription;
    private OfferingType offeringType;
    private String configuratorUrl;
    private String billingIdentifier;
    private ServiceType serviceType;

    private transient VOService vo;

    public ServiceRepresentation() {
        this(new VOService());
    }

    public ServiceRepresentation(VOService svc) {
        vo = svc;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setAccessType(getAccessType());
        vo.setBillingIdentifier(getBillingIdentifier());
        vo.setConfiguratorUrl(getConfiguratorUrl());
        vo.setDescription(getDescription());
        vo.setKey(convertIdToKey());
        vo.setName(getName());
        vo.setOfferingType(getOfferingType());
        vo.setParameters(updateParameters());
        vo.setServiceId(getServiceId());
        vo.setServiceType(getServiceType());
        vo.setShortDescription(getShortDescription());
        vo.setStatus(getStatus());
        vo.setTechnicalId(getTechnicalId());
        vo.setVersion(convertETagToVersion());
    }

    private List<VOParameter> updateParameters() {
        List<VOParameter> result = new ArrayList<VOParameter>();
        if (parameters == null) {
            return result;
        }
        for (ParameterRepresentation p : parameters) {
            p.update();
            result.add(p.getVO());
        }
        return result;
    }

    @Override
    public void convert() {
        setAccessType(vo.getAccessType());
        setBillingIdentifier(vo.getBillingIdentifier());
        setConfiguratorUrl(vo.getConfiguratorUrl());
        setDescription(vo.getDescription());
        setId(Long.valueOf(vo.getKey()));
        setName(vo.getName());
        setOfferingType(vo.getOfferingType());
        setParameters(convertParameters());
        setServiceId(vo.getServiceId());
        setServiceType(vo.getServiceType());
        setShortDescription(vo.getShortDescription());
        setStatus(vo.getStatus());
        setETag(Long.valueOf(vo.getVersion()));
        setTechnicalId(vo.getTechnicalId());
    }

    private List<ParameterRepresentation> convertParameters() {
        List<ParameterRepresentation> result = new ArrayList<ParameterRepresentation>();
        if (vo == null || vo.getParameters() == null) {
            return result;
        }
        for (VOParameter p : vo.getParameters()) {
            ParameterRepresentation pr = new ParameterRepresentation(p);
            pr.convert();
            result.add(pr);
        }
        return result;
    }

    public VOService getVO() {
        return vo;
    }

    public List<ParameterRepresentation> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterRepresentation> parameters) {
        this.parameters = parameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTechnicalId() {
        return technicalId;
    }

    public void setTechnicalId(String technicalId) {
        this.technicalId = technicalId;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public ServiceAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(ServiceAccessType accessType) {
        this.accessType = accessType;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public OfferingType getOfferingType() {
        return offeringType;
    }

    public void setOfferingType(OfferingType offeringType) {
        this.offeringType = offeringType;
    }

    public String getConfiguratorUrl() {
        return configuratorUrl;
    }

    public void setConfiguratorUrl(String configuratorUrl) {
        this.configuratorUrl = configuratorUrl;
    }

    public String getBillingIdentifier() {
        return billingIdentifier;
    }

    public void setBillingIdentifier(String billingIdentifier) {
        this.billingIdentifier = billingIdentifier;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public static Collection<ServiceRepresentation> toCollection(Collection<VOService> list) {
        Collection<ServiceRepresentation> result = new ArrayList<ServiceRepresentation>();
        for (VOService vo : list) {
            result.add(new ServiceRepresentation(vo));
        }
        return result;
    }

    public static List<VOService> toList(RepresentationCollection<ServiceRepresentation> content) {
        if (content == null || content.getItems() == null) {
            return null;
        }
        List<VOService> result = new ArrayList<VOService>();
        for (ServiceRepresentation sr : content.getItems()) {
            result.add(sr.getVO());
        }
        return result;
    }
}
