/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2013-8-21                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.accountNavigation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.oscm.internal.types.constants.HiddenUIConstants;

/**
 * @author Yuyin
 * 
 */
@ManagedBean
@SessionScoped
public class AccountNavigationModel implements Serializable {
    static final String MARKETPLACE_ACCOUNT_OPERATIONS_TITLE = "marketplace.account.operations.title";
    static final String MARKETPLACE_ACCOUNT_PROCESSES_TITLE = "marketplace.account.processes.title";
    static final String MARKETPLACE_ACCOUNT_REPORTS_TITLE = "marketplace.account.reports.title";
    static final String MARKETPLACE_ACCOUNT_USERS_TITLE = "marketplace.account.users.title";
    static final String MARKETPLACE_ACCOUNT_UNITS_TITLE = "marketplace.account.units.title";
    static final String MARKETPLACE_ACCOUNT_SUBSCRIPTIONS_TITLE = "marketplace.account.subscriptions.title";
    static final String MARKETPLACE_ACCOUNT_PAYMENTS_TITLE = "marketplace.account.payments.title";
    static final String MARKETPLACE_ACCOUNT_PROFILE_TITLE = "marketplace.account.profile.title";
    static final String MARKETPLACE_ACCOUNT_TITLE = "marketplace.account.title";

    private static final String ACCOUNT_LINK = "account/index.jsf";
    private static final String PROFILE_LINK = "account/profile.jsf";
    private static final String PAYMENT_LINK = "account/payments.jsf";
    private static final String SUBSCRIPTIONS_LINK = "account/subscriptions.jsf";
    private static final String USERS_LINK = "account/users.jsf";
    private static final String UNITS_LINK = "account/units.jsf";
    private static final String REPORTS_LINK = "account/reports.jsf";
    private static final String PROCESSES_LINK = "account/processes.jsf";
    private static final String OPERATIONS_LINK = "account/operations.jsf";
    private static final long serialVersionUID = 5299680432886964724L;

    private final List<String> link;
    private final List<String> title;
    private final List<String> hiddenElement;

    public AccountNavigationModel() {
        link = new ArrayList<>(8);
        title = new ArrayList<>(8);
        hiddenElement = new ArrayList<>(7);
        initLink();
        initTitle();
        initHiddenElement();
    }

    private void initHiddenElement() {
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_PROFILE);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_PAYMENT);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_SUBSCRIPTIONS);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_USERS);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_UNITS);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_REPORTS);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_PROCESSES);
        getHiddenElement().add(
                HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_OPERATIONS);
    }

    private void initTitle() {
        getTitle().add(MARKETPLACE_ACCOUNT_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_PROFILE_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_PAYMENTS_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_SUBSCRIPTIONS_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_USERS_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_UNITS_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_REPORTS_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_PROCESSES_TITLE);
        getTitle().add(MARKETPLACE_ACCOUNT_OPERATIONS_TITLE);
    }

    private void initLink() {
        getLink().add(ACCOUNT_LINK);
        getLink().add(PROFILE_LINK);
        getLink().add(PAYMENT_LINK);
        getLink().add(SUBSCRIPTIONS_LINK);
        getLink().add(USERS_LINK);
        getLink().add(UNITS_LINK);
        getLink().add(REPORTS_LINK);
        getLink().add(PROCESSES_LINK);
        getLink().add(OPERATIONS_LINK);
    }

    public List<String> getHiddenElement() {
        return hiddenElement;
    }

    public List<String> getLink() {
        return link;
    }

    public List<String> getTitle() {
        return title;
    }

}
