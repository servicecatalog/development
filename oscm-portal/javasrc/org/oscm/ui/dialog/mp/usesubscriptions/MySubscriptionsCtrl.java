/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 14.03.2013                                                     
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.usesubscriptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.oscm.string.Strings;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.validator.MySubscriptionActivationValidator;
import org.oscm.ui.validator.MySubscriptionStatusValidator;
import org.oscm.ui.validator.ValidationPerformer;
import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.subscriptions.OperationModel;
import org.oscm.internal.subscriptions.OperationParameterModel;
import org.oscm.internal.subscriptions.POSubscription;
import org.oscm.internal.subscriptions.SubscriptionsService;
import org.oscm.internal.triggerprocess.TriggerProcessesService;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.vo.VOServiceOperationParameter;
import org.oscm.internal.vo.VOServiceOperationParameterValues;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.internal.vo.VOTechnicalServiceOperation;

@ManagedBean
@ViewScoped
public class MySubscriptionsCtrl implements Serializable {

    private static final long serialVersionUID = -9209968842729517052L;
    @ManagedProperty(value = "#{mySubscriptionsLazyDataModel}")
    private MySubscriptionsLazyDataModel model;
    
    @ManagedProperty(value = "#{appBean}")
    ApplicationBean applicationBean;

    @ManagedProperty(value = "#{myTriggerProcessesModel}")
    private MyTriggerProcessesModel myTriggerProcessesModel;

    UiDelegate ui = new UiDelegate();
    String selectId;

    public static final String OUTCOME_ERROR = "error";
    public static final String OUTCOME_SUCCESS = "success";
    public static final String INFO_OPERATION_EXECUTED = "info.operation.executed";
    public static final String ERROR_SUBSCRIPTION_CONCURRENTMODIFY = "error.subscription.concurrentModify";

    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    /**
     * Services injected through setters.
     */
    SubscriptionsService subscriptionsService;
    SubscriptionService subscriptionService;

    @EJB
    TriggerProcessesService triggerProcessService;

    @PostConstruct
    public void initialize(){
    	initializeTriggerSubscriptions();
    	checkSelectedSubscription();
    }

    public void initializeTriggerSubscriptions() {
    	myTriggerProcessesModel.setWaitingForApprovalSubs(triggerProcessService
                .getMyWaitingForApprovalSubscriptions().getResultList(
                        POSubscription.class));
    }

    @EJB
    public void setSubscriptionsService(SubscriptionsService subscriptionsService) {
        this.subscriptionsService = subscriptionsService;
    }

    @EJB
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public MySubscriptionsLazyDataModel getModel() {
        return model;
    }

    public void setModel(MySubscriptionsLazyDataModel model) {
        this.model = model;
    }

    /**
     * Execute the selected operation for the selected subscription
     *
     * @throws SaaSApplicationException
     *             Thrown from the business logic.
     * @return the logical outcome.
     */
    public void executeOperation() throws SaaSApplicationException {
        POSubscription sub = model.getSelectedSubscription();
        if (sub == null) {
            return;
        }
        OperationModel selectedOperation = sub.getSelectedOperation();
        if (selectedOperation == null || selectedOperation.getOperation() == null) {
            return;
        }
        VOTechnicalServiceOperation operation = selectedOperation.getOperation();
        try {
            subscriptionService.executeServiceOperation(
                    sub.getVOSubscription(), operation);
        } catch (ConcurrentModificationException e) {
            ui.handleError(null, ERROR_SUBSCRIPTION_CONCURRENTMODIFY);
            return;
        }
        ui.handle(INFO_OPERATION_EXECUTED, operation.getOperationName());
    }

    VOTechnicalServiceOperation findSelectedOperation(VOSubscription sub,
            String operationId) {
        List<VOTechnicalServiceOperation> ops = sub
                .getTechnicalServiceOperations();
        for (VOTechnicalServiceOperation op : ops) {
            if (op.getOperationId().equals(operationId)) {
                return op;
            }
        }
        return null;
    }

    public void operationChanged() {
        POSubscription subscription = model.getSelectedSubscription();
        String operationId = subscription.getSelectedOperationId();
        if (Strings.isEmpty(operationId)) {
            subscription.setSelectedOperation(null);
            subscription.setSelectedOperationId(null);
            subscription.setExecuteDisabled(true);
        } else {
            VOTechnicalServiceOperation op = findSelectedOperation(
                    subscription.getVOSubscription(), operationId);
            OperationModel operationModel = new OperationModel();
            operationModel.setOperation(op);

            try {
                operationModel.setParameters(convert(op,
                        subscription.getVOSubscription()));
            } catch (SaaSApplicationException e) {
                subscription.setExecuteDisabled(true);
                ui.handleException(e);
            }
            subscription.setSelectedOperation(operationModel);
            subscription.setExecuteDisabled(false);
        }
    }

    List<OperationParameterModel> convert(VOTechnicalServiceOperation op,
            VOSubscription sub) throws SaaSApplicationException {
        Map<String, List<String>> paramValues = new HashMap<>();
        if (requestValuesNecessary(op)) {
            List<VOServiceOperationParameterValues> values = subscriptionService
                    .getServiceOperationParameterValues(sub, op);
            for (VOServiceOperationParameterValues v : values) {
                paramValues.put(v.getParameterId(), v.getValues());
            }
        }
        List<OperationParameterModel> result = new LinkedList<>();
        List<VOServiceOperationParameter> list = op.getOperationParameters();
        for (VOServiceOperationParameter param : list) {
            OperationParameterModel opm = new OperationParameterModel();
            opm.setParameter(param);
            if (paramValues.containsKey(param.getParameterId())) {
                opm.setValues(convert(paramValues.get(param.getParameterId())));
            }
            result.add(opm);
        }
        return result;
    }

    List<SelectItem> convert(List<String> list) {
        List<SelectItem> result = new LinkedList<>();
        if (list != null) {
            for (String s : list) {
                result.add(new SelectItem(s, s));
            }
        }
        return result;
    }

    boolean requestValuesNecessary(VOTechnicalServiceOperation op) {
        List<VOServiceOperationParameter> list = op.getOperationParameters();
        for (VOServiceOperationParameter p : list) {
            if (p.getType().isRequestValues()) {
                return true;
            }
        }
        return false;
    }

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

	public MyTriggerProcessesModel getMyTriggerProcessesModel() {
		return myTriggerProcessesModel;
	}

	public void setMyTriggerProcessesModel(MyTriggerProcessesModel myTriggerProcessesModel) {
		this.myTriggerProcessesModel = myTriggerProcessesModel;
	}

    public void validateSubscriptionStatus() {
        String subKey = model.getSelectedSubscriptionId();
        POSubscription mySubscriptionDetails = subscriptionsService.getMySubscriptionDetails(Long.parseLong(subKey));
        if (mySubscriptionDetails == null) {
            JSFUtils.addMessage(
                    null,
                    FacesMessage.SEVERITY_ERROR,
                    BaseBean.ERROR_SUBSCRIPTION_MODIFIED_OR_DELETED_CONCURRENTLY,
                    null);
            model.setSelectedSubscription(null);
            model.setSelectedSubscriptionId(null);
        } else {
            model.setSelectedSubscription(mySubscriptionDetails);
        }
    }
    
    public void checkSelectedSubscription() {
        String subKey = model.getSelectedSubscriptionId();
        if(subKey!=null){
            POSubscription mySubscriptionDetails = subscriptionsService.getMySubscriptionDetails(Long.parseLong(subKey));
            if (mySubscriptionDetails == null) {
                model.setSelectedSubscription(null);
                model.setSelectedSubscriptionId(null);
            }
        }
    }
}
