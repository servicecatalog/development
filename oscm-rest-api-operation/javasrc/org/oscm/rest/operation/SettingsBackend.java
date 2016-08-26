package org.oscm.rest.operation;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.operation.data.SettingRepresentation;

@Stateless
public class SettingsBackend implements RestBackend.Get<SettingRepresentation, OperationParameters>,
        RestBackend.Put<SettingRepresentation, OperationParameters>,
        RestBackend.Post<SettingRepresentation, OperationParameters>, RestBackend.Delete<OperationParameters> {

    @EJB
    OperatorService os;

    @EJB
    ConfigurationService cs;

    @EJB(beanInterface = ConfigurationServiceLocal.class)
    ConfigurationServiceLocal csl;

    @EJB(beanInterface = DataService.class)
    DataService dm;

    @Override
    @RolesAllowed("PLATFORM_OPERATOR")
    public void delete(OperationParameters params) throws Exception {
        ConfigurationSetting s = dm.getReference(ConfigurationSetting.class, params.getId());
        s.setValue(null);
        csl.setConfigurationSetting(s);
    }

    @Override
    @RolesAllowed("PLATFORM_OPERATOR")
    public Object post(SettingRepresentation content, OperationParameters params) throws Exception {
        os.saveConfigurationSetting(content.getVO());
        VOConfigurationSetting vo = cs.getVOConfigurationSetting(content.getInformationId(), content.getContextId());
        return Long.valueOf(vo.getKey());
    }

    @Override
    @RolesAllowed("PLATFORM_OPERATOR")
    public void put(SettingRepresentation content, OperationParameters params) throws Exception {
        os.saveConfigurationSetting(content.getVO());
    }

    @Override
    @RolesAllowed("PLATFORM_OPERATOR")
    public SettingRepresentation get(OperationParameters params) throws Exception {
        ConfigurationSetting s = dm.getReference(ConfigurationSetting.class, params.getId());
        VOConfigurationSetting vo = cs.getVOConfigurationSetting(s.getInformationId(), s.getContextId());
        return new SettingRepresentation(vo);
    }

}
