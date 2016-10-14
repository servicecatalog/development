package org.oscm.rest.subscription.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOParameterDefinition;
import org.oscm.rest.common.Representation;

public class ParameterDefinitionRepresentation extends Representation {

    private String parameterId;

    private transient VOParameterDefinition vo;

    public ParameterDefinitionRepresentation() {
        this(new VOParameterDefinition());
    }

    public ParameterDefinitionRepresentation(VOParameterDefinition def) {
        vo = def;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        vo.setParameterId(getParameterId());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setId(Long.valueOf(vo.getKey()));
        setParameterId(vo.getParameterId());
        setETag(Long.valueOf(vo.getVersion()));
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    public VOParameterDefinition getVO() {
        return vo;
    }

}
