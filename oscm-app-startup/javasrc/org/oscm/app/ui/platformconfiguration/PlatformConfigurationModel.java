package org.oscm.app.ui.platformconfiguration;

import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.oscm.app.ui.BaseModel;

/**
 * Created by PLGrubskiM on 2017-04-24.
 */
@ViewScoped
@ManagedBean
public class PlatformConfigurationModel extends BaseModel {

    private List<String> keys;
    private HashMap<String, String> items;


    public HashMap<String, String> getItems() {
        return items;
    }

    public void setItems(HashMap<String, String> items) {
        this.items = items;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
