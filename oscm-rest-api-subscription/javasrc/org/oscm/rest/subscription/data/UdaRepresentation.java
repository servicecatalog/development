package org.oscm.rest.subscription.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOUda;
import org.oscm.rest.common.Representation;

public class UdaRepresentation extends Representation {

    private transient VOUda vo;

    private UdaDefinitionRepresentation udaDefinition;
    private String udaValue;
    private long targetObjectKey;

    public UdaRepresentation() {
        this(new VOUda());
    }

    public UdaRepresentation(VOUda uda) {
        vo = uda;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        if (udaDefinition != null) {
            udaDefinition.update();
            vo.setUdaDefinition(udaDefinition.getVO());
        }
        vo.setKey(convertIdToKey());
        vo.setTargetObjectKey(getTargetObjectKey());
        vo.setUdaValue(getUdaValue());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        udaDefinition = new UdaDefinitionRepresentation(vo.getUdaDefinition());
        udaDefinition.convert();
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setTargetObjectKey(vo.getTargetObjectKey());
        setUdaValue(vo.getUdaValue());
    }

    public VOUda getVO() {
        return vo;
    }

    public UdaDefinitionRepresentation getUdaDefinition() {
        return udaDefinition;
    }

    public void setUdaDefinition(UdaDefinitionRepresentation udaDefinition) {
        this.udaDefinition = udaDefinition;
    }

    public String getUdaValue() {
        return udaValue;
    }

    public void setUdaValue(String udaValue) {
        this.udaValue = udaValue;
    }

    public long getTargetObjectKey() {
        return targetObjectKey;
    }

    public void setTargetObjectKey(long targetObjectKey) {
        this.targetObjectKey = targetObjectKey;
    }
}
