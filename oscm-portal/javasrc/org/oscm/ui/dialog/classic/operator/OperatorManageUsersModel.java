/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: afschar                                                   
 *                                                                              
 *  Creation Date: 05.03.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.operator;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import org.oscm.internal.vo.VOUser;
import org.oscm.ui.beans.BaseModel;
import org.oscm.ui.model.Marketplace;

/**
 * Bean for operator functionality for locking and unlocking a user account and
 * for resetting a users password.
 * 
 * @author afschar
 * 
 */
@ManagedBean(name = "operatorManageUsersModel")
@SessionScoped
public class OperatorManageUsersModel extends BaseModel implements Serializable {

    private static final long serialVersionUID = -6358643494917077721L;

    private String userId;
    private boolean userIdChanged;
    private VOUser user;
    private UploadedFile userImport;
    private List<Marketplace> marketplaces;
    private String marketplace;
    private boolean initialized = false;
    private long maxNumberOfRegisteredUsers;
    private long numberOfRegisteredUsers;

    public UploadedFile getUserImport() {
        return userImport;
    }

    public void setUserImport(UploadedFile userImport) {
        this.userImport = userImport;
    }

    public void setUser(VOUser user) {
        this.user = user;
        if (user == null) {
            userIdChanged = false;
        }
    }

    public VOUser getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        userIdChanged = true;
    }

    public boolean isUserIdChanged() {
        return userIdChanged;
    }

    public void setMarketplaces(List<Marketplace> marketplaces) {
        this.marketplaces = marketplaces;
    }

    public List<Marketplace> getMarketplaces() {
        return this.marketplaces;
    }

    public void setSelectedMarketplace(String marketplace) {
        this.marketplace = marketplace;
    }

    public String getSelectedMarketplace() {
        return this.marketplace;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public long getMaxNumberOfRegisteredUsers() {
        return maxNumberOfRegisteredUsers;
    }

    public void setMaxNumberOfRegisteredUsers(long maxNumberOfRegisteredUsers) {
        this.maxNumberOfRegisteredUsers = maxNumberOfRegisteredUsers;
    }

    public long getNumberOfRegisteredUsers() {
        return numberOfRegisteredUsers;
    }

    public void setNumberOfRegisteredUsers(long numberOfRegisteredUsers) {
        this.numberOfRegisteredUsers = numberOfRegisteredUsers;
    }

}
