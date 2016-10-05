/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                                                                                 
 *  Creation Date: 2013-03-01                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.oscm.app.openstack.i18n.Messages;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProvisioningValidator {

    private static final Logger logger = LoggerFactory.getLogger(ProvisioningValidator.class);

    public void validateStackName(PropertyHandler paramHandler) throws APPlatformException {
        String stackName = paramHandler.getStackName();
        if (isNullOrEmpty(stackName)) {
            throw new APPlatformException(Messages.getAll("error_invalid_name", new Object[] { stackName }));
        }

        String regex = "([A-Za-z][A-Za-z0-9_-]*){1,30}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(stackName);
        if (!m.matches()) {
            logger.error("Validation error on stack name: [" + stackName + "/" + regex + "]");
            throw new APPlatformException(Messages.getAll("error_invalid_name", new Object[] { stackName }));
        }

        String stackNamePattern = paramHandler.getStackNamePattern();
        if (!isNullOrEmpty(stackNamePattern)) {

            try {
                p = Pattern.compile(stackNamePattern);
            } catch (PatternSyntaxException e) {
                logger.error("Compile error on stack name pattern: [" + stackNamePattern + "]");
                throw new APPlatformException(
                        Messages.getAll("error_invalid_pattern", new Object[] { stackNamePattern }));
            }

            m = p.matcher(stackName);
            if (!m.matches()) {
                logger.error("Validation error on stack name: [" + stackName + "/" + stackNamePattern + "]");
                throw new APPlatformException(
                        Messages.getAll("error_name_match", new Object[] { stackName, stackNamePattern }));
            }
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}
