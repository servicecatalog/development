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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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
import org.oscm.internal.usermanagement.POUser;
import org.oscm.internal.usermanagement.UserManagementService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.types.constants.Configuration;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.operator.BaseOperatorBean;
import org.oscm.ui.beans.operator.OperatorSelectOrgBean;
import org.oscm.ui.common.ExceptionHandler;
import org.oscm.ui.model.Marketplace;
import org.oscm.validation.ArgumentValidator;


/**
 * Controller for operator manage users.
 *
 * @author afschar
 */
@ManagedBean(name = "operatorManageUsersCtrl")
@ViewScoped
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
    private List<String> dataTableHeaders = new ArrayList<>();
    private List<VOUserDetails> userAndOrganizations = new ArrayList<>();
    private List<Marketplace> marketplaces = new ArrayList<>();
    private boolean isInternalAuthMode;
    private Long maxRegisteredUsersCount;

    @ManagedProperty(value = "#{operatorManageUsersModel}")
    OperatorManageUsersModel model;

    @PostConstruct
    public void getInitialize() {
        isInternalAuthMode = getApplicationBean().isInternalAuthMode();
        initModel();
    }

    long getMaxRegisteredUsersCount() {
        if (maxRegisteredUsersCount == null) {
            VOConfigurationSetting configurationSetting = getConfigurationService()
                    .getVOConfigurationSetting(
                            ConfigurationKey.MAX_NUMBER_ALLOWED_USERS,
                            Configuration.GLOBAL_CONTEXT);
            maxRegisteredUsersCount = Long.valueOf(configurationSetting
                    .getValue());
        }
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

    List<VOUserDetails> getUsersList() {
        try {
            return getOperatorService().getUsers();
        } catch (OrganizationAuthoritiesException e) {
            ExceptionHandler.execute(e);
        }
        return Collections.emptyList();
    }

    public List<String> getDataTableHeaders() {
        if (dataTableHeaders == null || dataTableHeaders.isEmpty()) {
            try {
                dataTableHeaders = Arrays.asList("userId", "EMail", "organizationName", "organizationId");
            } catch (Exception e) {
                throw new SaaSSystemException(e);
            }
        }
        return dataTableHeaders;
    }

    public int getUsersListSize() {
        return getUsersList().size();
    }

    public boolean isCheckResetPasswordSupported() {
        if (model.isUserIdChanged()) {
            try {
                reinitUser();
            } catch (SaaSApplicationException e) {
                ExceptionHandler.execute(e);
            }
        }
        return getApplicationBean().isInternalAuthMode();
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
            model.getUser().setStatus(UserAccountStatus.LOCKED);
        }

        return (user != null) ? getOutcome(true) : OUTCOME_ERROR;
    }

    public String unlockUser() throws ObjectNotFoundException,
            ValidationException, OrganizationAuthoritiesException {

        VOUser user = model.getUser();
        if (user != null) {
            getOperatorService().setUserAccountStatus(user,
                    UserAccountStatus.ACTIVE);
            model.getUser().setStatus(UserAccountStatus.LOCKED);
        }

        return (user != null) ? getOutcome(true) : OUTCOME_ERROR;
    }

    public void updateSelectedUser() throws OperationNotPermittedException, ObjectNotFoundException, OrganizationRemovedException {
        for (VOUserDetails userDetails : userAndOrganizations) {
            if (userDetails.getUserId().equals(selectedUserId)) {
                model.setUser(userDetails);
            }
        }
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
            if (marketplaces.isEmpty()) {
                marketplaces = new ArrayList<>();
                for (VOMarketplace mp : getMarketplaceService()
                        .getMarketplacesForOperator()) {
                    marketplaces.add(new Marketplace(mp));
                }
                return marketplaces;
            }
        }
        return Collections.emptyList();
    }

    public boolean isPwdButtonEnabled() {
        return selectedUserId != null && !selectedUserId.isEmpty();
    }

    public boolean isLockButtonEnabled() throws ValidationException, ObjectNotFoundException {
        if (model.getUser() == null) {
            return false;
        }
        return isUserActive();
    }

    public boolean isUnlockButtonEnabled() throws ValidationException, ObjectNotFoundException {
        if (model.getUser() == null) {
            return false;
        }
        return isUserLocked();
    }

    private boolean isUserLocked() {
        if (model.getUser().getStatus().equals(UserAccountStatus.LOCKED)) {
            return true;
        }
        return false;
    }

    private boolean isUserActive() {
        if (model.getUser().getStatus().equals(UserAccountStatus.ACTIVE)) {
            return true;
        }
        return false;
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

    public List<VOUserDetails> getUserAndOrganizations() {
        if (userAndOrganizations.isEmpty()) {
            userAndOrganizations = getUsersList();
        }
        return userAndOrganizations;
    }

    public void setUserAndOrganizations(List<VOUserDetails> userAndOrganizations) {
        this.userAndOrganizations = userAndOrganizations;
    }

    public boolean isInternalAuthMode() {
        return isInternalAuthMode;
    }

    public void setInternalAuthMode(boolean internalAuthMode) {
        isInternalAuthMode = internalAuthMode;
    }
}
