/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 2014-6-26                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.createuser;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.SelectItem;

import org.oscm.internal.usergroupmgmt.POUserGroup;

/**
 * @author qiuw
 * 
 */
public class UserGroup implements Serializable {

    private static final long serialVersionUID = -4007271052444698069L;
    private POUserGroup poUserGroup;
    private boolean selected;
    private String name;
    private String description;
    private String referenceId;

    public POUserGroup getPoUserGroup() {
        return poUserGroup;
    }

    public void setPoUserGroup(POUserGroup poUserGroup) {
        this.poUserGroup = poUserGroup;
    }

    public boolean isSelected() {
        return selected || isDefault();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
            return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSelectedRole() {
        return poUserGroup.getSelectedRole();
    }

    public void setSelectedRole(String selectedRole) {
        poUserGroup.setSelectedRole(selectedRole);
    }

    public List<SelectItem> getRoles() {
        return poUserGroup.getRoles();
    }

    public void setRoles(List<SelectItem> roles) {
        poUserGroup.setRoles(roles);
    }

    public boolean isDefault() {
        return poUserGroup.isDefault();
    }
}
