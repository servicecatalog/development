package org.oscm.rest.operation;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.operation.data.SettingRepresentation;

@Stateless
public class SettingsBackend {

    @EJB
    OperatorService os;

    @EJB
    ConfigurationService cs;

    public RestBackend.Delete<OperationParameters> delete() throws Exception {
        return new RestBackend.Delete<OperationParameters>() {

            @Override
            public boolean delete(OperationParameters params) throws Exception {
                os.deleteConfigurationSetting(params.getId());
                return true;
            }
        };
    }

    public RestBackend.Post<SettingRepresentation, OperationParameters> post() throws Exception {
        return new RestBackend.Post<SettingRepresentation, OperationParameters>() {

            @Override
            public Object post(SettingRepresentation content, OperationParameters params) throws Exception {
                os.saveConfigurationSetting(content.getVO());
                VOConfigurationSetting vo = cs.getVOConfigurationSetting(content.getInformationId(),
                        content.getContextId());
                return Long.valueOf(vo.getKey());
            }
        };
    }

    public RestBackend.Put<SettingRepresentation, OperationParameters> put() throws Exception {
        return new RestBackend.Put<SettingRepresentation, OperationParameters>() {

            @Override
            public boolean put(SettingRepresentation content, OperationParameters params) throws Exception {
                os.saveConfigurationSetting(content.getVO());
                return true;
            }
        };
    }

    public RestBackend.Get<SettingRepresentation, OperationParameters> get() throws Exception {
        return new RestBackend.Get<SettingRepresentation, OperationParameters>() {

            @Override
            public SettingRepresentation get(OperationParameters params) throws Exception {
                VOConfigurationSetting vo = os.getConfigurationSetting(params.getId());
                return new SettingRepresentation(vo);
            }
        };
    }

    public RestBackend.GetCollection<SettingRepresentation, OperationParameters> getCollection() {
        return new RestBackend.GetCollection<SettingRepresentation, OperationParameters>() {

            @Override
            public RepresentationCollection<SettingRepresentation> getCollection(OperationParameters params)
                    throws Exception {
                List<VOConfigurationSetting> settings = os.getConfigurationSettings();
                return SettingRepresentation.toCollection(settings);
            }
        };
    }
}
