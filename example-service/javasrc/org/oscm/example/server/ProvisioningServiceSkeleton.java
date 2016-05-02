/**
 * ProvisioningServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */
package org.oscm.example.server;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;

import org.oscm.xsd.ActivateInstanceResponse;
import org.oscm.xsd.ActivateInstanceResponseE;
import org.oscm.xsd.AsyncCreateInstanceResponse;
import org.oscm.xsd.AsyncCreateInstanceResponseE;
import org.oscm.xsd.BaseResult;
import org.oscm.xsd.CreateInstanceResponse;
import org.oscm.xsd.CreateInstanceResponseE;
import org.oscm.xsd.CreateUsersResponse;
import org.oscm.xsd.CreateUsersResponseE;
import org.oscm.xsd.DeactivateInstanceResponse;
import org.oscm.xsd.DeactivateInstanceResponseE;
import org.oscm.xsd.DeleteInstanceResponse;
import org.oscm.xsd.DeleteInstanceResponseE;
import org.oscm.xsd.DeleteUsersResponse;
import org.oscm.xsd.DeleteUsersResponseE;
import org.oscm.xsd.InstanceInfo;
import org.oscm.xsd.InstanceResult;
import org.oscm.xsd.ModifySubscriptionResponse;
import org.oscm.xsd.ModifySubscriptionResponseE;
import org.oscm.xsd.SendPingResponse;
import org.oscm.xsd.SendPingResponseE;
import org.oscm.xsd.ServiceParameter;
import org.oscm.xsd.UpdateUsersResponse;
import org.oscm.xsd.UpdateUsersResponseE;
import org.oscm.xsd.UpgradeSubscriptionResponse;
import org.oscm.xsd.UpgradeSubscriptionResponseE;
import org.oscm.xsd.User;
import org.oscm.xsd.UserResult;
import org.oscm.example.common.ServiceParameterDAO;
import org.oscm.example.servlets.ExampleServlet;

/**
 * ProvisioningServiceSkeleton java skeleton for the axisService
 */
@SuppressWarnings({ "unused" })
public class ProvisioningServiceSkeleton {

    // The name of the parameter in the technical service definition
    // which defines the "sleep" time for executing a call of createUsers. This
    // is used to test the WS timeout configuration setting
    private final String WS_SLEEP = "WS_SLEEP";

    // The actual value of the parameter
    private static int wsSleepValue = 0;

    public org.oscm.xsd.ModifySubscriptionResponseE modifySubscription(
            org.oscm.xsd.ModifySubscriptionE modifySubscription) {
        String instanceId = modifySubscription.getModifySubscription()
                .getInstanceId();
        ServiceParameter[] parameterValues = modifySubscription
                .getModifySubscription().getParameterValues();
        BaseResult result = checkParameterSet(instanceId, parameterValues);
        ModifySubscriptionResponseE response = new ModifySubscriptionResponseE();
        ModifySubscriptionResponse param = new ModifySubscriptionResponse();
        param.set_return(result);
        response.setModifySubscriptionResponse(param);
        if (result.getRc() == RETURN_CODE_OK) {
            try {
                ServiceParameterDAO dao = new ServiceParameterDAO();
                File dir = new File(
                        ExampleServlet.getRealRootPath(getRequest()),
                        instanceId);
                dao.store(dir, parameterValues);

                // update the delayed execution of createUsers
                setWeSleep(parameterValues);
            } catch (IOException e) {
                setException(result, e);
                return response;
            }
        }
        return response;
    }

    public org.oscm.xsd.CreateInstanceResponseE createInstance(
            org.oscm.xsd.CreateInstanceE createInstance) {
        InstanceResult result = new InstanceResult();
        CreateInstanceResponseE response = new CreateInstanceResponseE();
        CreateInstanceResponse param = new CreateInstanceResponse();
        param.set_return(result);
        response.setCreateInstanceResponse(param);
        Random rand = new Random();
        String instanceId = null;
        HttpServletRequest req = getRequest();
        for (int i = 0; i < 20 && instanceId == null; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < 8; j++) {
                sb = sb.append(HEX_CHARSET[rand.nextInt(HEX_CHARSET.length)]);
            }
            instanceId = sb.toString();
            try {
                File dir = new File(ExampleServlet.getRealRootPath(req),
                        instanceId);
                if (dir.exists()) {
                    instanceId = null;
                } else {
                    create(dir);
                    ServiceParameterDAO dao = new ServiceParameterDAO();
                    ServiceParameter[] parameters = createInstance
                            .getCreateInstance().getRequest()
                            .getParameterValue();
                    dao.store(dir, parameters);

                    // set the delayed execution of createUsers
                    setWeSleep(parameters);
                }
            } catch (IOException e) {
                setException(result, e);
                return response;
            }
        }
        if (instanceId == null) {
            result.setRc(RETURN_CODE_ERROR_EXISTS);
            result.setDesc("Instance creation failed, no instanceId found!");
            return response;
        }
        String protocol = req.getProtocol();
        String baseUrl = protocol.substring(0, protocol.indexOf("/"))
                .toLowerCase()
                + "://"
                + req.getLocalAddr()
                + ":"
                + req.getLocalPort() + req.getContextPath();
        InstanceInfo instance = new InstanceInfo();
        instance.setInstanceId(instanceId);
        instance.setAccessInfo(null);
        instance.setBaseUrl(baseUrl);
        instance.setLoginPath("/login");
        setOk(result);
        result.setInstance(instance);
        return response;
    }

    public org.oscm.xsd.SendPingResponseE sendPing(
            org.oscm.xsd.SendPingE sendPing) {
        SendPingResponseE response = new SendPingResponseE();
        SendPingResponse param = new SendPingResponse();
        param.set_return(sendPing.getSendPing().getArg());
        response.setSendPingResponse(param);
        return response;
    }

    public org.oscm.xsd.AsyncCreateInstanceResponseE asyncCreateInstance(
            org.oscm.xsd.AsyncCreateInstanceE asyncCreateInstance) {
        AsyncCreateInstanceResponseE response = new AsyncCreateInstanceResponseE();
        AsyncCreateInstanceResponse param = new AsyncCreateInstanceResponse();
        BaseResult result = new BaseResult();
        result.setRc(RETURN_CODE_NOT_SUPPORTED);
        result.setDesc("Not supported");
        param.set_return(result);
        response.setAsyncCreateInstanceResponse(param);
        return response;
    }

    public org.oscm.xsd.ActivateInstanceResponseE activateInstance(
            org.oscm.xsd.ActivateInstanceE activateInstance) {
        ActivateInstanceResponseE response = new ActivateInstanceResponseE();
        ActivateInstanceResponse param = new ActivateInstanceResponse();
        param.set_return(getVoidResultOk());
        response.setActivateInstanceResponse(param);
        return response;
    }

    public org.oscm.xsd.DeactivateInstanceResponseE deactivateInstance(
            org.oscm.xsd.DeactivateInstanceE deactivateInstance) {
        DeactivateInstanceResponseE response = new DeactivateInstanceResponseE();
        DeactivateInstanceResponse param = new DeactivateInstanceResponse();
        param.set_return(getVoidResultOk());
        response.setDeactivateInstanceResponse(param);
        return response;
    }

    public org.oscm.xsd.CreateUsersResponseE createUsers(
            org.oscm.xsd.CreateUsersE createUsers) {
        User[] users = createUsers.getCreateUsers().getUsers();
        UserResult userResult = new UserResult();
        for (User user : users) {
            user.setApplicationUserId(String.valueOf(user.getUserId()
                    .hashCode()));
        }

        // This was introduced in order to tests the timeout
        if (wsSleepValue != 0) {
            try {
                Thread.sleep(wsSleepValue);
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }

        userResult.setUsers(users);
        setOk(userResult);
        CreateUsersResponseE response = new CreateUsersResponseE();
        CreateUsersResponse param = new CreateUsersResponse();
        param.set_return(userResult);
        response.setCreateUsersResponse(param);
        return response;
    }

    public org.oscm.xsd.UpdateUsersResponseE updateUsers(
            org.oscm.xsd.UpdateUsersE updateUsers) {
        UpdateUsersResponseE response = new UpdateUsersResponseE();
        UpdateUsersResponse param = new UpdateUsersResponse();
        param.set_return(getVoidResultOk());
        response.setUpdateUsersResponse(param);
        return response;
    }

    public org.oscm.xsd.DeleteUsersResponseE deleteUsers(
            org.oscm.xsd.DeleteUsersE deleteUsers) {
        DeleteUsersResponseE response = new DeleteUsersResponseE();
        DeleteUsersResponse param = new DeleteUsersResponse();
        param.set_return(getVoidResultOk());
        response.setDeleteUsersResponse(param);
        return response;
    }

    public org.oscm.xsd.DeleteInstanceResponseE deleteInstance(
            org.oscm.xsd.DeleteInstanceE deleteInstance) {
        BaseResult result = new BaseResult();
        DeleteInstanceResponseE response = new DeleteInstanceResponseE();
        DeleteInstanceResponse param = new DeleteInstanceResponse();
        param.set_return(result);
        response.setDeleteInstanceResponse(param);
        try {
            String instanceId = deleteInstance.getDeleteInstance()
                    .getInstanceId();
            File dir = new File(ExampleServlet.getRealRootPath(getRequest()),
                    instanceId);
            if (dir.exists()) {
                ServiceParameterDAO dao = new ServiceParameterDAO();
                dao.delete(dir);

                delete(dir, Arrays.asList(dir.list()));
                if (!dir.delete()) {
                    result.setRc(RETURN_CODE_ERROR_DELETE);
                    result.setDesc("Failed to delete instance: " + instanceId);
                    return response;
                }
            }
            setOk(result);
        } catch (IOException e) {
            setException(result, e);
        }
        return response;
    }

    private static final int RETURN_CODE_OK = 0;
    private static final int RETURN_CODE_ERROR_EXISTS = 100;
    private static final int RETURN_CODE_ERROR_DELETE = 110;
    private static final int RETURN_CODE_ERROR_INVALID_PARAM_VALUE = 120;
    private static final int RETURN_CODE_NOT_SUPPORTED = 190;
    private static final int RETURN_CODE_EXCEPTION = 200;

    public final static String EVENT_ID_FILE_DOWNLOAD = "FILE_DOWNLOAD";
    public final static String EVENT_ID_FILE_UPLOAD = "FILE_UPLOAD";
    public final static String EVENT_ID_FOLDER_NEW = "FOLDER_NEW";

    public final static String PARAM_MAX_FOLDER_NUM = "MAX_FOLDER_NUMBER";
    public final static String PARAM_MAX_FILE_NUM = "MAX_FILE_NUMBER";

    private static final String[] HEX_CHARSET = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", };

    private static BaseResult getVoidResultOk() {
        BaseResult result = new BaseResult();
        return setOk(result);
    }

    private static BaseResult setOk(BaseResult result) {
        result.setRc(RETURN_CODE_OK);
        result.setDesc("");
        return result;
    }

    private static BaseResult setException(BaseResult result, Exception e) {
        result.setRc(RETURN_CODE_EXCEPTION);
        result.setDesc(e.getMessage());
        return result;
    }

    private static HttpServletRequest getRequest() {
        return (HttpServletRequest) MessageContext.getCurrentMessageContext()
                .getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
    }

    private static void delete(File dir, List<String> elementList)
            throws IOException {
        if (elementList == null) {
            return;
        }
        for (Iterator<String> it = elementList.iterator(); it.hasNext();) {
            String element = it.next();
            if (element.length() > 0) {
                File file = new File(dir, element);
                if (file.isDirectory()) {
                    delete(file, Arrays.asList(file.list()));
                }
                if (!file.delete()) {
                    throw new IOException("Can't delete file: " + file);
                }
            }
        }
    }

    private BaseResult checkParameterSet(String instanceId,
            ServiceParameter[] parameterValues) {
        BaseResult result = new BaseResult();
        try {
            if (parameterValues != null) {
                for (ServiceParameter sp : parameterValues) {
                    if (PARAM_MAX_FILE_NUM.equals(sp.getParameterId())) {
                        BaseResult checkParameter = checkParameter(instanceId,
                                sp);
                        if (checkParameter.getRc() != RETURN_CODE_OK) {
                            return checkParameter;
                        }
                    } else if (PARAM_MAX_FOLDER_NUM.equals(sp.getParameterId())) {
                        BaseResult checkParameter = checkParameter(instanceId,
                                sp);
                        if (checkParameter.getRc() != RETURN_CODE_OK) {
                            return checkParameter;
                        }
                    }
                }
            }
            return getVoidResultOk();
        } catch (NumberFormatException e) {
            return setException(result, e);
        } catch (IOException e) {
            return setException(result, e);
        }
    }

    private BaseResult checkParameter(String instanceId,
            ServiceParameter parameter) throws IOException {
        BaseResult result = new BaseResult();
        int max = Integer.parseInt(parameter.getValue());
        if (max >= 0) {
            int n = 0;
            File dir = new File(ExampleServlet.getRealRootPath(getRequest()),
                    instanceId);
            n = ExampleServlet.getFolderCount(dir);
            if (max < n) {
                result.setRc(RETURN_CODE_ERROR_INVALID_PARAM_VALUE);
                result.setDesc("The current value for "
                        + parameter.getParameterId() + " is " + n
                        + " but must not be more than " + max);
                return result;
            }
        }
        return getVoidResultOk();
    }

    /**
     * This small helper sets the wsSleepValue, the time interval to simulate a
     * long execution time of createUsers.
     */
    private void setWeSleep(ServiceParameter[] parameters) {
        if (parameters == null) {
            return;
        }
        for (int j = 0; j < parameters.length; j++) {
            if (parameters[j].getParameterId().equals(WS_SLEEP)) {
                try {
                    wsSleepValue = Integer.parseInt(parameters[j].getValue());
                    break;
                } catch (NumberFormatException e) {
                    // We simply ignore the parameter
                }
            }
        }
    }

    private void create(File location) throws IOException {
        boolean folderCreated = location.mkdirs();
        if (!folderCreated)
            throw new IOException("Failed to create directory at "
                    + location.getPath());
    }

    public org.oscm.xsd.UpgradeSubscriptionResponseE upgradeSubscription(
            org.oscm.xsd.UpgradeSubscriptionE upgradeSubscription) {
        String instanceId = upgradeSubscription.getUpgradeSubscription()
                .getInstanceId();
        ServiceParameter[] parameterValues = upgradeSubscription
                .getUpgradeSubscription().getParameterValues();
        BaseResult result = checkParameterSet(instanceId, parameterValues);
        UpgradeSubscriptionResponseE response = new UpgradeSubscriptionResponseE();
        UpgradeSubscriptionResponse param = new UpgradeSubscriptionResponse();
        param.set_return(result);
        response.setUpgradeSubscriptionResponse(param);
        if (result.getRc() == RETURN_CODE_OK) {
            try {
                ServiceParameterDAO dao = new ServiceParameterDAO();
                File dir = new File(
                        ExampleServlet.getRealRootPath(getRequest()),
                        instanceId);
                dao.store(dir, parameterValues);

                // update the delayed execution of createUsers
                setWeSleep(parameterValues);
            } catch (IOException e) {
                setException(result, e);
                return response;
            }
        }
        return response;
    }

    public org.oscm.xsd.AsyncModifySubscriptionResponseE asyncModifySubscription(
            org.oscm.xsd.AsyncModifySubscriptionE asyncModifySubscription) {
        // TODO : fill this with the necessary business logic
        throw new java.lang.UnsupportedOperationException("Please implement "
                + this.getClass().getName() + "#asyncModifySubscription");
    }

    public org.oscm.xsd.AsyncUpgradeSubscriptionResponseE asyncUpgradeSubscription(
            org.oscm.xsd.AsyncUpgradeSubscriptionE asyncUpgradeSubscription) {
        // TODO : fill this with the necessary business logic
        throw new java.lang.UnsupportedOperationException("Please implement "
                + this.getClass().getName() + "#asyncUpgradeSubscription");
    }

}
