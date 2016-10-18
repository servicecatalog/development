package org.oscm.rest.subscription.data;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOParameter;
import org.oscm.internal.vo.VOService;
import org.oscm.rest.common.Representation;

public class ServiceRepresentation extends Representation {

    // TODO price model
    private String serviceId;

    private List<ParameterRepresentation> parameters = new ArrayList<ParameterRepresentation>();

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
        vo.setKey(convertIdToKey());
        vo.setServiceId(serviceId);
        vo.setVersion(convertETagToVersion());
        if (parameters != null) {
            for (ParameterRepresentation pr : parameters) {
                pr.update();
                vo.getParameters().add(pr.getVO());
            }
        }
    }

    @Override
    public void convert() {
        setId(Long.valueOf(vo.getKey()));
        setServiceId(vo.getServiceId());
        setETag(Long.valueOf(vo.getVersion()));
        List<VOParameter> params = vo.getParameters();
        for (VOParameter p : params) {
            ParameterRepresentation pr = new ParameterRepresentation(p);
            pr.convert();
            parameters.add(pr);
        }
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
