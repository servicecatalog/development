/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                        
 *                                                                              
 *  Creation Date: 30.05.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.aws.controller;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.oscm.app.aws.i18n.Messages;
import org.oscm.app.common.intf.ControllerAccess;

@Singleton
public class AWSControllerAccess implements ControllerAccess {

    private static final long serialVersionUID = -5454221403279778113L;

    public String getControllerId() {
        return AWSController.ID;
    }

    public String getMessage(String locale, String key, Object... args) {
        return Messages.get(locale, key, args);
    }

    public List<String> getControllerParameterKeys() {
        LinkedList<String> result = new LinkedList<String>();
        result.add(PropertyHandler.ACCESS_KEY_ID_PWD);
        result.add(PropertyHandler.SECRET_KEY_PWD);
        return result;
    }

}
