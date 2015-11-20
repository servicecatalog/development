package org.oscm.internal.usermanagement;

import java.io.Serializable;

import org.oscm.internal.types.enumtypes.Salutation;

public class POUserInUnit implements Serializable {

    private static final long serialVersionUID = 6037969626330082505L;
    private boolean selected;
    private POUser poUser;
    private String roleInUnit;
    private Salutation salutation;
    private String locale;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public POUser getPoUser() {
        return poUser;
    }

    public void setPoUser(POUser poUser) {
        this.poUser = poUser;
    }

    public String getUserId() {
        return poUser.getUserId();
    }
    
    public String getFirstName() {
        return poUser.getFirstName();
    }

    public String getLastName() {
        return poUser.getLastName();
    }

    public String getRoleInUnit() {
        return roleInUnit;
    }

    public void setRoleInUnit(String roleInUnit) {
        this.roleInUnit = roleInUnit;
    }

    public Salutation getSalutation() {
        return salutation;
    }

    public void setSalutation(Salutation salutation) {
        this.salutation = salutation;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

}
