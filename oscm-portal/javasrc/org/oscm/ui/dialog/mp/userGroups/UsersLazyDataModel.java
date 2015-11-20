package org.oscm.ui.dialog.mp.userGroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.oscm.internal.components.response.Response;
import org.oscm.internal.types.enumtypes.UnitRoleType;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.usergroupmgmt.UserGroupService;
import org.oscm.internal.usermanagement.POUserInUnit;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.pagination.Pagination;
import org.oscm.pagination.TableColumns;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.dialog.mp.usesubscriptions.MySubscriptionsLazyDataModel;
import org.oscm.ui.model.RichLazyDataModel;
import org.richfaces.component.SortOrder;
import org.richfaces.model.FilterField;
import org.richfaces.model.SortField;

@ViewScoped
@ManagedBean(name = "usersLazyDataModel")
public class UsersLazyDataModel extends RichLazyDataModel<POUserInUnit> {

    private static final String USER_ID = "userId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String ROLE_IN_UNIT = "roleInUnit";

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(MySubscriptionsLazyDataModel.class);

    @EJB
    private UserGroupService userGroupService;

    @ManagedProperty(value = "#{manageGroupModel}")
    private ManageGroupModel manageGroupModel;

    public UsersLazyDataModel() {
        super(false);
    }

    @PostConstruct
    public void init() {
        getColumnNamesMapping().put(USER_ID, TableColumns.USER_ID);
        getColumnNamesMapping().put(FIRST_NAME, TableColumns.FIRST_NAME);
        getColumnNamesMapping().put(LAST_NAME, TableColumns.LAST_NAME);
        getColumnNamesMapping().put(ROLE_IN_UNIT, TableColumns.ROLE_IN_UNIT);

        getSortOrders().put(USER_ID, SortOrder.unsorted);
        getSortOrders().put(FIRST_NAME, SortOrder.unsorted);
        getSortOrders().put(LAST_NAME, SortOrder.unsorted);
        getSortOrders().put(ROLE_IN_UNIT, SortOrder.unsorted);
    }

    private void decorateWithLocalizedRoles(Pagination pagination) {
        Map<UnitRoleType, String> localizedRolesMap = pagination
                .getLocalizedRolesMap();
        for (UnitRoleType role : UnitRoleType.values()) {
            localizedRolesMap.put(
                    role,
                    JSFUtils.getText(UnitRoleType.class.getSimpleName() + "."
                            + role.name() + ".enum", null));
        }
    }

    @Override
    public List<POUserInUnit> getDataList(int firstRow, int numRows,
            List<FilterField> filterFields, List<SortField> sortFields) {
        Pagination pagination = new Pagination(firstRow, numRows);
        applyFilters(getArrangeable().getFilterFields(), pagination);
        applySorting(getArrangeable().getSortFields(), pagination);
        decorateWithLocalizedRoles(pagination);
        List<POUserInUnit> resultList = Collections.emptyList();

        try {
            Response response = userGroupService.getUsersForGroup(pagination,
                    getManageGroupModel().getSelectedGroupId());
            resultList = response.getResultList(POUserInUnit.class);
        } catch (OrganizationAuthoritiesException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR);
        }
        /*for (POUserInUnit poUserInUnit : resultList) {
            Boolean isSelected = manageGroupModel.getSelectedUsersIds().get(
                    poUserInUnit.getUserId());
            if (isSelected == null) {
                continue;
            }
            poUserInUnit.setSelected(isSelected.booleanValue());
        }*/
        manageGroupModel.setCurrentResultUsers(new ArrayList<POUserInUnit>(
                resultList));

        return resultList;
    }

    @Override
    public Object getKey(POUserInUnit t) {
        return t.getUserId();
    }

    public int getTotalCount() {
        try {
            Pagination pagination = new Pagination();
            applyFilters(getArrangeable().getFilterFields(), pagination);
            decorateWithLocalizedRoles(pagination);
            Integer response = userGroupService.getCountUsersForGroup(pagination,
                    getManageGroupModel().getSelectedGroupId());
            setTotalCount(response.intValue());
        } catch (OrganizationAuthoritiesException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR);
        }
        return super.getTotalCount();
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getFIRST_NAME() {
        return FIRST_NAME;
    }

    public String getLAST_NAME() {
        return LAST_NAME;
    }

    public String getROLE_IN_UNIT() {
        return ROLE_IN_UNIT;
    }

    public ManageGroupModel getManageGroupModel() {
        return manageGroupModel;
    }

    public void setManageGroupModel(ManageGroupModel manageGroupModel) {
        this.manageGroupModel = manageGroupModel;
    }

}
