package org.oscm.rest.subscription.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.internal.vo.VOUdaDefinition;
import org.oscm.rest.common.Representation;

public class UdaDefinitionRepresentation extends Representation {

    private transient VOUdaDefinition vo;

    private String udaId;
    private String targetType;
    private String defaultValue;
    private UdaConfigurationType configurationType;

    public UdaDefinitionRepresentation() {
        this(new VOUdaDefinition());
    }

    public UdaDefinitionRepresentation(VOUdaDefinition def) {
        vo = def;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setConfigurationType(getConfigurationType());
        vo.setDefaultValue(getDefaultValue());
        vo.setKey(convertIdToKey());
        vo.setTargetType(getTargetType());
        vo.setUdaId(getUdaId());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setConfigurationType(vo.getConfigurationType());
        setDefaultValue(vo.getDefaultValue());
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setTargetType(vo.getTargetType());
        setUdaId(vo.getUdaId());
    }

    public VOUdaDefinition getVO() {
        return vo;
    }

    public String getUdaId() {
        return udaId;
    }

    public void setUdaId(String udaId) {
        this.udaId = udaId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public UdaConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(UdaConfigurationType configurationType) {
        this.configurationType = configurationType;
    }
}
