/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.sp;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.oscm.provisioning.data.BaseResult;
import org.oscm.provisioning.data.InstanceInfo;
import org.oscm.provisioning.data.InstanceRequest;
import org.oscm.provisioning.data.InstanceResult;
import org.oscm.provisioning.data.ServiceAttribute;
import org.oscm.provisioning.data.ServiceParameter;
import org.oscm.provisioning.data.User;
import org.oscm.provisioning.data.UserResult;
import org.oscm.provisioning.intf.ProvisioningService;

/**
 * This is a stub implementation of the {@link ProvisioningService}
 * 
 * @author kulle
 */
@WebService(serviceName = "SamlProvisioningService", targetNamespace = "http://oscm.org/xsd", endpointInterface = "org.oscm.provisioning.intf.ProvisioningService")
public class ProvisioningServiceBean implements ProvisioningService {

    @Resource
    private WebServiceContext context;

    private static final int RETURN_CODE_OK = 0;

    private <T extends BaseResult> T setOk(T result) {
        result.setRc(RETURN_CODE_OK);
        result.setDesc("Ok");
        return result;
    }

    private BaseResult getBaseResultOk() {
        return setOk(new BaseResult());
    }

    @Override
    public BaseResult asyncCreateInstance(InstanceRequest request,
            User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public InstanceResult createInstance(InstanceRequest request,
            User requestingUser) {
        InstanceInfo instance = new InstanceInfo();
        instance.setInstanceId(request.getSubscriptionId());
        instance.setAccessInfo(null);

        InstanceResult instanceResult = new InstanceResult();
        instanceResult.setInstance(instance);
        setOk(instanceResult);

        return instanceResult;
    }

    @Override
    public UserResult createUsers(String instanceId, List<User> users,
            User requestingUser) {
        UserResult userResult = new UserResult();
        for (User user : users) {
            user.setApplicationUserId(user.getUserId());
        }
        userResult.setUsers(users);
        setOk(userResult);

        return userResult;
    }

    @Override
    public BaseResult deleteInstance(String instanceId, String organizationId,
            String subscriptionId, User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult deleteUsers(String instanceId, List<User> users,
            User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public String sendPing(String arg) {
        return arg;
    }

    @Override
    public BaseResult modifySubscription(String instanceId,
            String subscriptionId, String referenceId,
            List<ServiceParameter> parameterValues,
            List<ServiceAttribute> attributeValues, User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult updateUsers(String instanceId, List<User> users,
            User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult activateInstance(String instanceId, User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult deactivateInstance(String instanceId,
            User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult asyncModifySubscription(String instanceId,
            String subscriptionId, String referenceId,
            List<ServiceParameter> parameterValues,
            List<ServiceAttribute> attributeValues, User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult asyncUpgradeSubscription(String instanceId,
            String subscriptionId, String referenceId,
            List<ServiceParameter> parameterValues,
            List<ServiceAttribute> attributeValues, User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult upgradeSubscription(String instanceId,
            String subscriptionId, String referenceId,
            List<ServiceParameter> parameterValues,
            List<ServiceAttribute> attributeValues, User requestingUser) {
        return getBaseResultOk();
    }

    @Override
    public BaseResult saveAttributes(String organizationId,
            List<ServiceAttribute> attributeValues, User requestingUser) {
        return getBaseResultOk();
    }

}
