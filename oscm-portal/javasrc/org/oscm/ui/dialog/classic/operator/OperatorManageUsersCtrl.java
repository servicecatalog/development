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

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.oscm.types.constants.Configuration;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.MarketplaceBean;
import org.oscm.ui.beans.operator.BaseOperatorBean;
import org.oscm.ui.beans.operator.OperatorSelectOrgBean;
import org.oscm.ui.common.ExceptionHandler;
import org.oscm.ui.model.Marketplace;
import org.oscm.ui.model.User;
import org.oscm.validation.ArgumentValidator;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.UserAccountStatus;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.types.exception.OrganizationRemovedException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.usermanagement.UserManagementService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;

/**
 * Controller for operator manage users.
 *
 * @author afschar
 */
@ManagedBean(name="operatorManageUsersCtrl")
@RequestScoped
public class OperatorManageUsersCtrl extends BaseOperatorBean implements
        Serializable {

    /**
     *
     */
    private static final String NO_SELECTION = "0";
    public static final String APPLICATION_BEAN = "appBean";
    private static final long serialVersionUID = -9126265695343363133L;

    transient ApplicationBean appBean;
    private String selectedUserId;

    @ManagedProperty(value = "#{operatorManageUsersModel}")
    OperatorManageUsersModel model;

    public String getInitialize() {
        initModel();
        return "";
    }

    long getMaxRegisteredUsersCount() {
        VOConfigurationSetting configurationSetting = getConfigurationService()
                .getVOConfigurationSetting(
                        ConfigurationKey.MAX_NUMBER_ALLOWED_USERS,
                        Configuration.GLOBAL_CONTEXT);
        Long maxRegisteredUsersCount = Long.valueOf(configurationSetting
                .getValue());

        return maxRegisteredUsersCount.longValue();
    }

    void initModel() {
        model.setMarketplaces(getSelectableMarketplaces());
        model.setMaxNumberOfRegisteredUsers(getMaxRegisteredUsersCount());
        model.setNumberOfRegisteredUsers(getAccountingService()
                .countRegisteredUsers());
        model.setInitialized(true);
    }

    void reinitUser() throws ObjectNotFoundException,
            OperationNotPermittedException, OrganizationRemovedException {
        model.setUser(null);
        if (!isBlank(model.getUserId())) {
            VOUser reqUser = new VOUser();
            reqUser.setUserId(model.getUserId());
            model.setUser(getIdService().getUser(reqUser));
        }
    }

    ApplicationBean getApplicationBean() {
        if (appBean == null) {
            appBean = ui.findBean(APPLICATION_BEAN);
        }
        return appBean;
    }

    @SuppressWarnings("unused")
    public List<User> suggest(FacesContext context, UIComponent component, String userId) {
		userId = userId.replaceAll("\\p{C}", "");
        Vo2ModelMapper<VOUserDetails, User> mapper = new Vo2ModelMapper<VOUserDetails, User>() {
            @Override
            public User createModel(final VOUserDetails vo) {
                return new User(vo);
            }
        };
        try {
            String pattern = userId + "%";
            return mapper.map(getOperatorService().getUsers(pattern));
        } catch (SaaSApplicationException e) {
            ExceptionHandler.execute(e);
        }
        return null;
    }

    public List<User> getUsersList() {
        Vo2ModelMapper<VOUserDetails, User> mapper = new Vo2ModelMapper<VOUserDetails, User>() {
            @Override
            public User createModel(final VOUserDetails vo) {
                return new User(vo);
            }
        };
        try {
            return mapper.map(getOperatorService().getUsers("%"));
        } catch (OrganizationAuthoritiesException e) {
            ExceptionHandler.execute(e);
        }
        return null;
    }

    public boolean isCheckResetPasswordSupported() {
        final boolean b = getApplicationBean().isInternalAuthMode();
        if (model.isUserIdChanged()) {
            try {
                reinitUser();
            } catch (SaaSApplicationException e) {
                ExceptionHandler.execute(e);
            }
        }
        return b;
    }

    public boolean isExceedMaxNumberOfUsers() {
        if (model.getNumberOfRegisteredUsers() <= model
                .getMaxNumberOfRegisteredUsers())
            return false;
        else {
            return true;
        }
    }

    public String resetPasswordForUser() throws ObjectNotFoundException,
            OperationNotPermittedException, MailOperationException,
            OrganizationAuthoritiesException {

        VOUser user = model.getUser();

        if (user == null) {
            return OUTCOME_ERROR;
        }

        if (validateLdapUser(user)) {
            return OUTCOME_ERROR;
        }

        getOperatorService().resetPasswordForUser(user.getUserId());

        return getOutcome(true);
    }

    public String lockUser() throws ObjectNotFoundException,
            ValidationException, OrganizationAuthoritiesException {

        VOUser user = model.getUser();
        if (user != null) {
            getOperatorService().setUserAccountStatus(user,
                    UserAccountStatus.LOCKED);
        }

        return (user != null) ? getOutcome(true) : OUTCOME_ERROR;
    }

    public String unlockUser() throws ObjectNotFoundException,
            ValidationException, OrganizationAuthoritiesException {

        VOUser user = model.getUser();
        if (user != null) {
            getOperatorService().setUserAccountStatus(user,
                    UserAccountStatus.ACTIVE);
        }

        return (user != null) ? getOutcome(true) : OUTCOME_ERROR;
    }

    public void updateSelectedUser(){
        VOUser selectedUser = new VOUser();
        selectedUser.setUserId(selectedUserId);
        model.setUser(selectedUser);
    }

    public OperatorManageUsersModel getModel() {
        return model;
    }

    public void setModel(OperatorManageUsersModel model) {
        this.model = model;
    }

    public String importUsers() {
        if (!model.isTokenValid()) {
            return OUTCOME_SUCCESS;
        }

        ArgumentValidator.notNull("userImport", model.getUserImport());
        try {
            getUserService().importUsers(
                    model.getUserImport().getBytes(),
                    getSelectedOrganization(), getSelectedMarketplace());
            model.resetToken();
            ui.handle("info.user.importStarted.updateHint", model.getUserImport()
                    .getName());
        } catch (SaaSApplicationException ex) {
            ui.handleException(ex);
        } catch (IOException ex) {
            throw new SaaSSystemException(ex);
        }
        return OUTCOME_SUCCESS;
    }

    public void setSelectedUserId(String userId) {
        this.selectedUserId = userId;
    }

    public String getSelectedUserId() {
        return selectedUserId;
    }

    String getSelectedMarketplace() {
        if (model.getSelectedMarketplace().equals(NO_SELECTION)) {
            return null;
        } else {
            return model.getSelectedMarketplace();
        }
    }

    String getSelectedOrganization() {
        return ((OperatorSelectOrgBean) ui.findBean("operatorSelectOrgBean"))
                .getOrganizationId();
    }

    List<Marketplace> getSelectableMarketplaces() {
        if (isLoggedInAndPlatformOperator()) {
            List<Marketplace> marketplaces = ((MarketplaceBean) ui
                    .findBean("marketplaceBean")).getMarketplacesForOperator();
            if (marketplaces != null) {
                return marketplaces;
            }
        }

        return Collections.emptyList();
    }

    private boolean validateLdapUser(VOUser user)
            throws ObjectNotFoundException {
        UserManagementService service = sl
                .findService(UserManagementService.class);

        String organizationId;
        organizationId = user.getOrganizationId();
        if (service.isOrganizationLDAPManaged(organizationId)) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    BaseBean.ERROR_LDAPUSER_RESETPASSWORD);
            return true;
        }
        return false;
    }
}
