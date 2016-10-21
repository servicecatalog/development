package org.oscm.rest.service.data;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.ParameterModificationType;
import org.oscm.internal.types.enumtypes.ParameterType;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.vo.VOParameterDefinition;
import org.oscm.internal.vo.VOParameterOption;
import org.oscm.rest.common.Representation;

public class ParameterDefinitionRepresentation extends Representation {

    private List<ParameterOptionRepresentation> parameterOptions = new ArrayList<ParameterOptionRepresentation>();
    private String defaultValue;
    private Long minValue;
    private Long maxValue;
    private boolean mandatory;
    private boolean configurable;
    private ParameterType parameterType;
    private String parameterId;
    private ParameterValueType valueType;
    private ParameterModificationType modificationType;
    private String description;

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
        vo.setConfigurable(isConfigurable());
        vo.setDefaultValue(getDefaultValue());
        vo.setDescription(getDescription());
        vo.setKey(convertIdToKey());
        vo.setMandatory(isMandatory());
        vo.setMaxValue(getMaxValue());
        vo.setMinValue(getMinValue());
        vo.setModificationType(getModificationType());
        vo.setParameterId(getParameterId());
        vo.setParameterOptions(updateOptions());
        vo.setParameterType(getParameterType());
        vo.setValueType(getValueType());
        vo.setVersion(convertETagToVersion());
    }

    private List<VOParameterOption> updateOptions() {
        List<VOParameterOption> result = new ArrayList<VOParameterOption>();
        if (parameterOptions == null) {
            return result;
        }
        for (ParameterOptionRepresentation po : parameterOptions) {
            po.update();
            result.add(po.getVO());
        }
        return result;
    }

    @Override
    public void convert() {
        setConfigurable(vo.isConfigurable());
        setDefaultValue(vo.getDefaultValue());
        setDescription(vo.getDescription());
        setId(Long.valueOf(vo.getKey()));
        setMandatory(vo.isMandatory());
        setMaxValue(vo.getMaxValue());
        setMinValue(vo.getMinValue());
        setModificationType(vo.getModificationType());
        setParameterId(vo.getParameterId());
        setParameterOptions(convertOptions());
        setParameterType(vo.getParameterType());
        setETag(Long.valueOf(vo.getVersion()));
        setValueType(vo.getValueType());
    }

    private List<ParameterOptionRepresentation> convertOptions() {
        List<ParameterOptionRepresentation> result = new ArrayList<ParameterOptionRepresentation>();
        if (vo == null || vo.getParameterOptions() == null) {
            return result;
        }
        for (VOParameterOption po : vo.getParameterOptions()) {
            ParameterOptionRepresentation rep = new ParameterOptionRepresentation(po);
            rep.convert();
            result.add(rep);
        }
        return result;
    }

    public List<ParameterOptionRepresentation> getParameterOptions() {
        return parameterOptions;
    }

    public void setParameterOptions(List<ParameterOptionRepresentation> parameterOptions) {
        this.parameterOptions = parameterOptions;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    public ParameterValueType getValueType() {
        return valueType;
    }

    public void setValueType(ParameterValueType valueType) {
        this.valueType = valueType;
    }

    public ParameterModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ParameterModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VOParameterDefinition getVO() {
        return vo;
    }

}
