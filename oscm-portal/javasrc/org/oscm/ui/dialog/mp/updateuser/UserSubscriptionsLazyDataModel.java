package org.oscm.ui.dialog.mp.updateuser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.oscm.internal.types.enumtypes.UnitRoleType;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.usermanagement.POServiceRole;
import org.oscm.internal.usermanagement.POSubscription;
import org.oscm.internal.usermanagement.UserService;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.pagination.Pagination;
import org.oscm.pagination.TableColumns;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.dialog.mp.createuser.Subscription;
import org.oscm.ui.model.RichLazyDataModel;
import org.richfaces.component.SortOrder;
import org.richfaces.model.FilterField;
import org.richfaces.model.SortField;

@ViewScoped
@ManagedBean(name = "userSubscriptionsLazyDataModel")
public class UserSubscriptionsLazyDataModel
        extends RichLazyDataModel<Subscription> {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(UserSubscriptionsLazyDataModel.class);
    
    private static final String SUBSCRIPTION_ID = "id";
    private static final String ROLE_IN_SUB = "selectedRole";

    @EJB
    private UserService userService;
    
    @ManagedProperty(value = "#{updateUserModel}")
    private UpdateUserModel model;

    public UserSubscriptionsLazyDataModel() {
        super(false);
    }

    @PostConstruct
    public void init() {
        getColumnNamesMapping().put(SUBSCRIPTION_ID,
                TableColumns.SUBSCRIPTION_ID);
        getColumnNamesMapping().put(ROLE_IN_SUB, TableColumns.ROLE_IN_SUB);

        getSortOrders().put(SUBSCRIPTION_ID, SortOrder.unsorted);
        getSortOrders().put(ROLE_IN_SUB, SortOrder.unsorted);
    }

    @Override
    public List<Subscription> getDataList(int firstRow, int numRows,
            List<FilterField> filterFields, List<SortField> sortFields) {

        Pagination pagination = new Pagination(firstRow, numRows);

        applyFilters(getArrangeable().getFilterFields(), pagination);
        applySorting(getArrangeable().getSortFields(), pagination);

        List<Subscription> resultList = Collections.emptyList();
        String userId = model.getUser().getUserId();
        
        try {
            List<POSubscription> userSubscriptions = userService
                    .getUserAssignableSubscriptions(pagination, userId);
            
            resultList = toSubscriptionList(userSubscriptions);
            
        } catch (SaaSApplicationException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR);
        }
        
        model.setSubscriptions(resultList);
        return resultList;
    }

    private List<Subscription> toSubscriptionList(List<POSubscription> poSubs) {
        
        ArrayList<Subscription> list = new ArrayList<>();
        
        for (POSubscription poSub : poSubs) {
            
            Subscription sub = new Subscription();
            sub.setId(poSub.getId());
            List<POServiceRole> roles = poSub.getRoles();
            sub.setRolesRendered(!roles.isEmpty());
            List<SelectItem> items = new ArrayList<>();
            
            for (POServiceRole r : roles) {
                SelectItem si = new SelectItem(String.format("%s:%s",
                        Long.valueOf(r.getKey()), r.getId()), r.getName());
                if(r.getName().equalsIgnoreCase(UnitRoleType.USER.name())) {
                    items.add(0, si);
                } else {
                    items.add(si);
                }
            }
            
            sub.setRoles(items);
            sub.setSelected(poSub.isAssigned());
            
            String selectedRole = null;
            
            if (poSub.getUsageLicense() != null
                    && poSub.getUsageLicense().getPoServieRole() != null) {
                POServiceRole poServiceRole = poSub.getUsageLicense()
                        .getPoServieRole();
                selectedRole = String.format("%s:%s",
                        Long.valueOf(poServiceRole.getKey()),
                        poServiceRole.getId());

                sub.setLicKey(poSub.getUsageLicense().getKey());
                sub.setLicVersion(poSub.getUsageLicense().getVersion());
            }
            
            sub.setSelectedRole(selectedRole);
            list.add(sub);
        }
        
        return list;
    }

    @Override
    public Object getKey(Subscription t) {

        return t.getId();
    }
    
    @Override
    public int getTotalCount() {
        
        String userId = model.getUser().getUserId();  
        
        try {
            Pagination pagination = new Pagination();
            applyFilters(getArrangeable().getFilterFields(), pagination);
            
            Long totalCount = userService
                    .getUserAssignableSubscriptionsNumber(pagination, userId);
            
            setTotalCount(totalCount.intValue());
            
        } catch (SaaSApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return super.getTotalCount();
    }

    public String getSUBSCRIPTION_ID() {
        return SUBSCRIPTION_ID;
    }

    public String getROLE_IN_SUB() {
        return ROLE_IN_SUB;
    }
    
    public UpdateUserModel getModel() {
        return model;
    }

    public void setModel(UpdateUserModel model) {
        this.model = model;
    }
}
