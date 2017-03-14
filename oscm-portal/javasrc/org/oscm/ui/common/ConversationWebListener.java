/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *  Creation Date: 20.08.15 11:17
 *
 *******************************************************************************/

package org.oscm.ui.common;

import javax.inject.Inject;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@WebListener
public class ConversationWebListener implements ServletRequestListener,
        Serializable {
    
    private static final long serialVersionUID = -2283109272540232945L;
    
    @Inject
    private ConversationLocks conversationLocks;
    
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        if (hasConversationContext(event)) {
            conversationLocks.get(getConversationId(event)).unlock();
        }
    }
    
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        if (hasConversationContext(event)) {
            obtainConvesationLock(event);
        }
    }
    
    private void obtainConvesationLock(ServletRequestEvent event) {
        conversationLocks.get(getConversationId(event)).lock();
    }
    
    private boolean hasConversationContext(ServletRequestEvent event) {
        return StringUtils.isNotEmpty(getConversationId(event));
    }
    
    private String getConversationId(ServletRequestEvent event) {
        return event.getServletRequest().getParameter("conversationID");
    }
}
