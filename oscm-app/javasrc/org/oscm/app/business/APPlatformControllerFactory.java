/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.app.business;

import javax.naming.InitialContext;

import org.oscm.app.adapter.APPlatformControllerAdapter;
import org.oscm.app.i18n.Messages;
import org.oscm.app.v2_0.exceptions.ControllerLookupException;
import org.oscm.app.v2_0.intf.APPlatformController;
import org.oscm.app.adapter.APPlatformControllerLegacyAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APPlatformControllerFactory {

    private static final Logger logger = LoggerFactory
            .getLogger(APPlatformControllerFactory.class);

    public static APPlatformController getInstance(String controllerId)
            throws ControllerLookupException {
        String controllerFullId = APPlatformController.JNDI_PREFIX
                + controllerId;
        try {
            InitialContext context = new InitialContext();
            Object lookup = context.lookup(controllerFullId);
            APPlatformController adapter;

            if (APPlatformController.class.isAssignableFrom(lookup.getClass())) {
                adapter = new APPlatformControllerAdapter(
                        (APPlatformController) lookup);
            } else if (org.oscm.app.v1_0.intf.APPlatformController.class
                    .isAssignableFrom(lookup.getClass())) {
                adapter = new APPlatformControllerLegacyAdapter(
                        (org.oscm.app.v1_0.intf.APPlatformController) lookup);
            } else {
                logger.warn("Exception during controller lookup ["
                        + controllerFullId + "]");
                throw new ControllerLookupException(
                        Messages.getAll("error_controller_lookup"));
            }
            return adapter;
        } catch (Exception e) {
            logger.warn("Exception during controller lookup ["
                    + controllerFullId + "]", e);
            throw new ControllerLookupException(
                    Messages.getAll("error_controller_lookup"), e);
        }
    }
}
