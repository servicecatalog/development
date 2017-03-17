/*
 * ******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *   Creation Date: 30.03.15 13:11
 *
 * ******************************************************************************
 */
package org.oscm.app.sample.ui;

import org.jboss.weld.context.http.HttpConversationContext;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

@ManagedBean(eager=true)
@ApplicationScoped
public class Application {

    @Inject
    private HttpConversationContext conversationContext;

    @PostConstruct
    public void init() {
        hideConversationScope();
    }

    /**
     * "Hide" conversation scope by replacing its default "cid" parameter name
     * by something unpredictable.
     */
    private void hideConversationScope() {
        conversationContext.setParameterName("conversationID");
    }

}
