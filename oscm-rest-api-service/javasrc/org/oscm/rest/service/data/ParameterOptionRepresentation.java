package org.oscm.rest.service.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOParameterOption;
import org.oscm.rest.common.Representation;

public class ParameterOptionRepresentation extends Representation {

    private String optionId;
    private String optionDescription;
    private String paramDefId;

    private transient VOParameterOption vo;

    public ParameterOptionRepresentation() {
        this(new VOParameterOption());
    }

    public ParameterOptionRepresentation(VOParameterOption option) {
        vo = option;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        vo.setOptionDescription(getOptionDescription());
        vo.setOptionId(getOptionId());
        vo.setParamDefId(getParamDefId());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setId(Long.valueOf(vo.getKey()));
        setOptionDescription(vo.getOptionDescription());
        setOptionId(vo.getOptionId());
        setETag(Long.valueOf(vo.getVersion()));
        setParamDefId(vo.getParamDefId());
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public String getParamDefId() {
        return paramDefId;
    }

    public void setParamDefId(String paramDefId) {
        this.paramDefId = paramDefId;
    }

    public VOParameterOption getVO() {
        return vo;
    }

}
