/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.app.ror.controller;

import java.util.ArrayList;
import java.util.List;

import org.oscm.app.common.intf.ControllerAccess;
import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.ror.i18n.Messages;

public class RORControllerAccess implements ControllerAccess {

    private static final long serialVersionUID = 148240965524197522L;

    @Override
    public String getControllerId() {
        return RORController.ID;
    }

    @Override
    public String getMessage(String locale, String key, Object... arguments) {
        // intentionally referencing ROR message properties
        return Messages.get(locale, key, arguments);
    }

    @Override
    public List<String> getControllerParameterKeys() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(PropertyHandler.IAAS_API_URI);
        list.add(PropertyHandler.IAAS_API_USER);
        list.add(PropertyHandler.IAAS_API_PWD);
        list.add(PropertyHandler.IAAS_API_TENANT);
        list.add(PropertyHandler.IAAS_API_LOCALE);
        return list;
    }

}
