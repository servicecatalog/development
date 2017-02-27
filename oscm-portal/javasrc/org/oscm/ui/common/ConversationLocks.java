/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *  Creation Date: 20.08.15 11:18
 *
 *******************************************************************************/

package org.oscm.ui.common;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SessionScoped
public class ConversationLocks
        implements Serializable {
    
    private static final long serialVersionUID = -6133806696076501269L;
    
    private Map<String, Lock> conversationLocks = new ConcurrentHashMap<>();
    
    public Lock get(String cid) {
        if (conversationLocks.containsKey(cid)) {
            return conversationLocks.get(cid);
        } else {
            return initConversationLock(cid);
        }
    }
    
    private synchronized Lock initConversationLock(String key) {
        if (conversationLocks.containsKey(key)) {
            return conversationLocks.get(key);
        } else {
            ReentrantLock lock = new ReentrantLock();
            conversationLocks.put(key, lock);
            return lock;
        }
    }
}
