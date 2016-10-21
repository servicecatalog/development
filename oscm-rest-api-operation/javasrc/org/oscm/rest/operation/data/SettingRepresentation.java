package org.oscm.rest.operation.data;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RepresentationCollection;

public class SettingRepresentation extends Representation {

    private transient VOConfigurationSetting vo;

    private ConfigurationKey informationId;
    private String contextId;
    private String value;

    public SettingRepresentation() {
        this(new VOConfigurationSetting());
    }

    public SettingRepresentation(VOConfigurationSetting cs) {
        vo = cs;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setContextId(getContextId());
        vo.setInformationId(getInformationId());
        vo.setKey(convertIdToKey());
        vo.setValue(getValue());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setContextId(vo.getContextId());
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setInformationId(vo.getInformationId());
        setValue(vo.getValue());
    }

    public ConfigurationKey getInformationId() {
        return informationId;
    }

    public void setInformationId(ConfigurationKey informationId) {
        this.informationId = informationId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public VOConfigurationSetting getVO() {
        return vo;
    }

    public static RepresentationCollection<SettingRepresentation> toCollection(List<VOConfigurationSetting> settings) {
        List<SettingRepresentation> list = new ArrayList<SettingRepresentation>();
        for (VOConfigurationSetting cs : settings) {
            list.add(new SettingRepresentation(cs));
        }
        return new RepresentationCollection<SettingRepresentation>(list);
    }

}
