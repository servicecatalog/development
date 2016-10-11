/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                                                                                 
 *  Creation Date: 2013-03-01                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack.controller;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oscm.app.openstack.data.FlowState;
import org.oscm.app.openstack.i18n.Messages;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.AuthenticationException;
import org.oscm.app.v1_0.exceptions.ConfigurationException;
import org.oscm.app.v1_0.exceptions.SuspendException;
import org.oscm.app.v1_0.intf.APPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProvisioningValidator {

    private static final Logger logger = LoggerFactory
            .getLogger(ProvisioningValidator.class);
    private static final String SUSPENDED = "suspended";
    private static final List<FlowState> TIMEOUT_OPERATION = Arrays.asList(
            FlowState.START_REQUESTED, FlowState.STARTING,
            FlowState.STOP_REQUESTED, FlowState.STOPPING);

    public void validateStackName(PropertyHandler paramHandler)
            throws APPlatformException {
        String stackName = paramHandler.getStackName();
        if (isNullOrEmpty(stackName)) {
            throw new APPlatformException(Messages.getAll("error_invalid_name",
                    new Object[] { stackName }));
        }

        String regex = "([A-Za-z][A-Za-z0-9_-]*){1,30}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(stackName);
        if (!m.matches()) {
            logger.error("Validation error on stack name: [" + stackName + "/"
                    + regex + "]");
            throw new APPlatformException(Messages.getAll("error_invalid_name",
                    new Object[] { stackName }));
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * Check timeout for each operation. If operation is not finished during
     * several times, SuspendException will be occurred.
     * 
     * @param instanceId
     * @param ph
     * @throws APPlatformException
     * @throws ConfigurationException
     * @throws AuthenticationException
     */
    public static void validateTimeout(String instanceId, PropertyHandler ph,
            APPlatformService APPService) throws AuthenticationException,
            ConfigurationException, APPlatformException {
        long readyTimeout = ph.getReadyTimeout();
        String startTimeStr = ph.getStartTime();
        if (readyTimeout != 0 && startTimeStr != null
                && TIMEOUT_OPERATION.contains(ph.getState())) {
            if (startTimeStr.equals(SUSPENDED)) {
                logger.debug("Resume request, reset start time");
                ph.setStartTime(String.valueOf(System.currentTimeMillis()));
            }
            try {

                long startTime = Long.parseLong(startTimeStr);
                long currentTime = System.currentTimeMillis();
                long timePast = currentTime - startTime;
                logger.debug("ExecutionTime: " + timePast + "ms (StartTime: "
                        + startTime + "ms, CurrentTime: " + currentTime
                        + "ms), ReadyTimeout: " + readyTimeout + "ms");

                if (timePast > readyTimeout) {
                    logger.debug("Request timeout: over " + timePast + "ms");
                    ph.setStartTime(SUSPENDED);
                    APPService.storeServiceInstanceDetails(
                            OpenStackController.ID, instanceId,
                            ph.getSettings(), ph.getTPAuthentication());
                    throw new SuspendException(
                            "Task not finished after " + readyTimeout + " ms.");
                }
            } catch (NumberFormatException ex) {
                logger.warn(
                        "The action timeout is not a number and therefore ignored.");
            }
        }

    }
}
