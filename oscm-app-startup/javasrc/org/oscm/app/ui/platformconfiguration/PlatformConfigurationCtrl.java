package org.oscm.app.ui.platformconfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.oscm.app.ui.BaseCtrl;
import org.oscm.app.v2_0.data.Setting;
import org.oscm.app.v2_0.exceptions.ConfigurationException;
import org.oscm.app.v2_0.service.APPConfigurationServiceBean;

/**
 * Created by PLGrubskiM on 2017-04-24.
 */
@ViewScoped
@ManagedBean
public class PlatformConfigurationCtrl extends BaseCtrl {

    @ManagedProperty(value = "#{platformConfigurationModel}")
    private PlatformConfigurationModel model;

    @Inject
    private APPConfigurationServiceBean appConfigService;

    @PostConstruct
    public void getInitialize() {
        try {
            model.setItems(initPlatformSettings());
            model.setKeys(initItemKeys());
            setModel(model);
        } catch (Exception e) {
            addError(e);
        }
    }

    //// TODO: 2017-04-24
    public String save() {
        try {
            HashMap<String, String> store = new HashMap<>(
                    model.getItems());
//            getAppConfigService().storeControllerOrganizations(store);
            addMessage(SUCCESS_SAVED);
        } catch (Exception e) {
            addError(e);
        }
        return OUTCOME_SUCCESS;
    }

    private HashMap<String, String> initPlatformSettings() throws ConfigurationException {
        HashMap<String, Setting> map = appConfigService
                .getProxyConfigurationSettings();

        HashMap<String, String> result = new HashMap<>();

        for (Map.Entry<String, Setting> entry : map.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue().getValue();
            result.put(key, value);
        }
        return result;
    }

    private List<String> initItemKeys() {
        List<String> keys = new ArrayList<>();
        HashMap<String, String> items = model.getItems();
        if (items != null) {
            keys.addAll(items.keySet());
        }
        return keys;
    }

    public void setModel(PlatformConfigurationModel model) {
        this.model = model;
    }

    public PlatformConfigurationModel getModel() {
        return model;
    }
}
