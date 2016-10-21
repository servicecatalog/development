package org.oscm.rest.service.data;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOServiceOperationParameter;
import org.oscm.internal.vo.VOTechnicalServiceOperation;
import org.oscm.rest.common.Representation;

public class OperationRepresentation extends Representation {

    private String operationId;
    private String operationName;
    private String operationDescription;
    private List<OperationParameterRepresentation> operationParameters = new ArrayList<OperationParameterRepresentation>();

    private transient VOTechnicalServiceOperation vo;

    public OperationRepresentation() {
        this(new VOTechnicalServiceOperation());
    }

    public OperationRepresentation(VOTechnicalServiceOperation o) {
        vo = o;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        vo.setOperationDescription(getOperationDescription());
        vo.setOperationId(getOperationId());
        vo.setOperationName(getOperationName());
        vo.setOperationParameters(updateParameters());
        vo.setVersion(convertETagToVersion());
    }

    private List<VOServiceOperationParameter> updateParameters() {
        List<VOServiceOperationParameter> result = new ArrayList<VOServiceOperationParameter>();
        if (operationParameters == null) {
            return result;
        }
        for (OperationParameterRepresentation op : operationParameters) {
            op.update();
            result.add(op.getVO());
        }
        return result;
    }

    @Override
    public void convert() {
        setId(Long.valueOf(vo.getKey()));
        setOperationDescription(vo.getOperationDescription());
        setOperationId(vo.getOperationId());
        setOperationName(vo.getOperationName());
        setOperationParameters(convertParameters());
        setETag(Long.valueOf(vo.getVersion()));
    }

    private List<OperationParameterRepresentation> convertParameters() {
        List<OperationParameterRepresentation> result = new ArrayList<OperationParameterRepresentation>();
        if (vo.getOperationParameters() == null) {
            return result;
        }
        for (VOServiceOperationParameter op : vo.getOperationParameters()) {
            OperationParameterRepresentation opr = new OperationParameterRepresentation(op);
            opr.convert();
            result.add(opr);
        }
        return result;
    }

    public VOTechnicalServiceOperation getVO() {
        return vo;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }

    public List<OperationParameterRepresentation> getOperationParameters() {
        return operationParameters;
    }

    public void setOperationParameters(List<OperationParameterRepresentation> operationParameters) {
        this.operationParameters = operationParameters;
    }

}
