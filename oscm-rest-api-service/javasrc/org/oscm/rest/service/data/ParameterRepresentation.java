package org.oscm.rest.service.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOParameter;
import org.oscm.rest.common.Representation;

public class ParameterRepresentation extends Representation {

    private ParameterDefinitionRepresentation parameterDefinition;
    private String value;
    private boolean configurable;

    private transient VOParameter vo;

    public ParameterRepresentation() {
        this(new VOParameter());
    }

    public ParameterRepresentation(VOParameter param) {
        vo = param;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setConfigurable(isConfigurable());
        vo.setKey(convertIdToKey());
        if (parameterDefinition != null) {
            parameterDefinition.update();
            vo.setParameterDefinition(parameterDefinition.getVO());
        }
        vo.setValue(getValue());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setConfigurable(vo.isConfigurable());
        setId(Long.valueOf(vo.getKey()));
        ParameterDefinitionRepresentation def = new ParameterDefinitionRepresentation(vo.getParameterDefinition());
        def.convert();
        setParameterDefinition(def);
        setETag(Long.valueOf(vo.getVersion()));
        setValue(vo.getValue());
    }

    public ParameterDefinitionRepresentation getParameterDefinition() {
        return parameterDefinition;
    }

    public void setParameterDefinition(ParameterDefinitionRepresentation parameterDefinition) {
        this.parameterDefinition = parameterDefinition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    public VOParameter getVO() {
        return vo;
    }

}
