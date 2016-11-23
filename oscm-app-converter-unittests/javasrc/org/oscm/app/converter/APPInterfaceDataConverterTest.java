/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                                                                                 
 *  Creation Date: 27 paź 2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.converter;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.oscm.app.v1_0.data.ControllerConfigurationKey;
import org.oscm.app.v1_0.data.ControllerSettings;
import org.oscm.app.v1_0.data.InstanceDescription;
import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.InstanceStatusUsers;
import org.oscm.app.v1_0.data.LocalizedText;
import org.oscm.app.v1_0.data.OperationParameter;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.data.ServiceUser;
import org.oscm.app.v1_0.data.User;

public class APPInterfaceDataConverterTest {

    APPInterfaceDataConverter dataConverter = new APPInterfaceDataConverter();

    @Test
    public void nullConversionControllerConfigurationKey1() {
        assertNull(dataConverter
                .convertToNew((ControllerConfigurationKey) null));
    }

    @Test
    public void nullConversionControllerConfigurationKey2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.ControllerConfigurationKey) null));
    }

    @Test
    public void nullConversionControllerSettings1() {
        assertNull(dataConverter.convertToNew((ControllerSettings) null));
    }

    @Test
    public void nullConversionControllerSettings2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.ControllerSettings) null));
    }

    @Test
    public void nullConversionInstanceDescription1() {
        assertNull(dataConverter.convertToNew((InstanceDescription) null));
    }

    @Test
    public void nullConversionInstanceDescription2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.InstanceDescription) null));
    }

    @Test
    public void nullConversionInstanceStatus1() {
        assertNull(dataConverter.convertToNew((InstanceStatus) null));
    }

    @Test
    public void nullConversionInstanceStatus2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.InstanceDescription) null));
    }

    @Test
    public void nullConversionInstanceStatusUsers1() {
        assertNull(dataConverter.convertToNew((InstanceStatusUsers) null));
    }

    @Test
    public void nullConversionInstanceStatusUsers2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.InstanceStatusUsers) null));
    }

    @Test
    public void nullConversionListOperationParameter1() {
        assertNull(dataConverter.convertToNew((List<OperationParameter>) null));
    }

    @Test
    public void nullConversionListOperationParameter2() {
        assertNull(dataConverter
                .convertToOldOperationParametersList((List<org.oscm.app.v2_0.data.OperationParameter>) null));
    }

    @Test
    public void nullConversionLocalizedText1() {
        assertNull(dataConverter.convertToNew((LocalizedText) null));
    }

    @Test
    public void nullConversionLocalizedText2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.LocalizedText) null));
    }

    @Test
    public void nullConversionOperationParameter1() {
        assertNull(dataConverter.convertToNew((OperationParameter) null));
    }

    @Test
    public void nullConversionOperationParameter2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.OperationParameter) null));
    }

    @Test
    public void nullConversionPasswordAuthentication1() {
        assertNull(dataConverter.convertToNew((PasswordAuthentication) null));
    }

    @Test
    public void nullConversionPasswordAuthentication2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.PasswordAuthentication) null));
    }

    @Test
    public void nullConversionProvisioningSettings1() {
        assertNull(dataConverter.convertToNew((ProvisioningSettings) null));
    }

    @Test
    public void nullConversionProvisioningSettings2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.ProvisioningSettings) null));
    }

    @Test
    public void nullConversionServiceUser1() {
        assertNull(dataConverter.convertToNew((ServiceUser) null));
    }

    @Test
    public void nullConversionServiceUser2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.ServiceUser) null));
    }

    @Test
    public void nullConversionUser1() {
        assertNull(dataConverter.convertToNew((User) null));
    }

    @Test
    public void nullConversionUser2() {
        assertNull(dataConverter
                .convertToOld((org.oscm.app.v2_0.data.User) null));
    }

    @Test
    public void testConvertToNewControllerControllerConfigurationKey() {
        // given
        ControllerConfigurationKey oldObject = ControllerConfigurationKey.BSS_USER_ID;

        // when
        org.oscm.app.v2_0.data.ControllerConfigurationKey newObject = dataConverter
                .convertToNew(oldObject);
        // then

        assertEquals(newObject.name(), oldObject.name());
        assertEquals(newObject.isMandatory(), oldObject.isMandatory());
    }

    @Test
    public void testConvertToOldControllerControllerConfigurationKey() {
        // given
        org.oscm.app.v2_0.data.ControllerConfigurationKey newObject = org.oscm.app.v2_0.data.ControllerConfigurationKey.BSS_ORGANIZATION_ID;
        // when
        ControllerConfigurationKey oldObject = ControllerConfigurationKey
                .valueOf(newObject.name());
        // then
        assertEquals(oldObject.name(), newObject.name());
        assertEquals(oldObject.isMandatory(), newObject.isMandatory());
    }

    @Test
    public void testConvertToNewControllerSettings() {
        // given
        org.oscm.app.v2_0.data.PasswordAuthentication goodResult = new org.oscm.app.v2_0.data.PasswordAuthentication(
                "newLogin", "newPass");
        ControllerSettings oldObject = mock(ControllerSettings.class);
        doReturn(new PasswordAuthentication("newLogin", "newPass")).when(
                oldObject).getAuthentication();
        // when
        org.oscm.app.v2_0.data.ControllerSettings newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(goodResult, newObject.getAuthentication());
        assertEquals(oldObject.getConfigSettings(),
                newObject.getConfigSettings());
    }

    @Test
    public void testConvertToOldControllerSettings() {
        // given
        String password = "newPass";
        String newLogin = "newLogin";
        PasswordAuthentication goodResult = new PasswordAuthentication(
                newLogin, password);
        HashMap<String, String> configSettings = new HashMap<>();
        org.oscm.app.v2_0.data.ControllerSettings newObject = new org.oscm.app.v2_0.data.ControllerSettings(
                dataConverter.convertToNew(configSettings));
        org.oscm.app.v2_0.data.PasswordAuthentication oldAuthentication = new org.oscm.app.v2_0.data.PasswordAuthentication(
                newLogin, password);
        newObject.setAuthentication(oldAuthentication);
        // when
        ControllerSettings oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(goodResult, oldObject.getAuthentication());
        assertEquals(oldObject.getConfigSettings(),
                newObject.getConfigSettings());
    }

    @Test
    public void testConvertToNewInstanceDescription() {
        // given
        InstanceDescription oldObject = mock(InstanceDescription.class);
        // when
        org.oscm.app.v2_0.data.InstanceDescription newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getInstanceId(), newObject.getInstanceId());
        assertEquals(oldObject.getAccessInfo(), newObject.getAccessInfo());
        assertEquals(oldObject.getBaseUrl(), newObject.getBaseUrl());
        assertEquals(oldObject.getChangedParameters(),
                newObject.getChangedParameters());
        assertEquals(oldObject.getDescription(), newObject.getDescription());
        assertEquals(oldObject.getLoginPath(), newObject.getLoginPath());
        assertEquals(oldObject.getRunWithTimer(), newObject.getRunWithTimer());
    }

    @Test
    public void testConvertToOldInstanceDescription() {
        // given
        org.oscm.app.v2_0.data.InstanceDescription newObject = mock(org.oscm.app.v2_0.data.InstanceDescription.class);
        // when
        InstanceDescription oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getInstanceId(), newObject.getInstanceId());
        assertEquals(oldObject.getAccessInfo(), newObject.getAccessInfo());
        assertEquals(oldObject.getBaseUrl(), newObject.getBaseUrl());
        assertEquals(oldObject.getChangedParameters(),
                newObject.getChangedParameters());
        assertEquals(oldObject.getDescription(), newObject.getDescription());
        assertEquals(oldObject.getLoginPath(), newObject.getLoginPath());
        assertEquals(oldObject.getRunWithTimer(), newObject.getRunWithTimer());
    }

    @Test
    public void testConvertToNewInstanceStatus() {
        // given
        InstanceStatus oldObject = mock(InstanceStatus.class);
        // when
        org.oscm.app.v2_0.data.InstanceStatus newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getRunWithTimer(), newObject.getRunWithTimer());
        assertEquals(oldObject.getLoginPath(), newObject.getLoginPath());
        assertEquals(oldObject.getDescription(), newObject.getDescription());
        assertEquals(oldObject.getChangedParameters(),
                newObject.getChangedParameters());
        assertEquals(oldObject.getBaseUrl(), newObject.getBaseUrl());
        assertEquals(oldObject.getAccessInfo(), newObject.getAccessInfo());
    }

    @Test
    public void testConvertToOldInstanceStatus() {
        // given
        org.oscm.app.v2_0.data.InstanceStatus newObject = mock(org.oscm.app.v2_0.data.InstanceStatus.class);
        // when
        InstanceStatus oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getRunWithTimer(), newObject.getRunWithTimer());
        assertEquals(oldObject.getLoginPath(), newObject.getLoginPath());
        assertEquals(oldObject.getDescription(), newObject.getDescription());
        assertEquals(oldObject.getChangedParameters(),
                newObject.getChangedParameters());
        assertEquals(oldObject.getBaseUrl(), newObject.getBaseUrl());
        assertEquals(oldObject.getAccessInfo(), newObject.getAccessInfo());
    }

    @Test
    public void testConvertToNewInstanceStatusUsers() {
        // given
        InstanceStatusUsers oldObject = mock(InstanceStatusUsers.class);
        List<LocalizedText> localizedTextList = new ArrayList<>(10);
        doReturn(localizedTextList).when(oldObject).getDescription();
        // when
        org.oscm.app.v2_0.data.InstanceStatusUsers newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getChangedUsers(), newObject.getChangedUsers());
        assertEquals(oldObject.getLoginPath(), newObject.getLoginPath());
        assertEquals(oldObject.getBaseUrl(), newObject.getBaseUrl());
        assertEquals(oldObject.getAccessInfo(), newObject.getAccessInfo());
        assertEquals(localizedTextList, newObject.getDescription());
        assertEquals(oldObject.getRunWithTimer(), oldObject.getRunWithTimer());
    }

    @Test
    public void testConvertToOldInstanceStatusUsers() {
        // given
        org.oscm.app.v2_0.data.InstanceStatusUsers newObject = mock(org.oscm.app.v2_0.data.InstanceStatusUsers.class);
        List<LocalizedText> localizedTextList = new ArrayList<>(10);
        doReturn(localizedTextList).when(newObject).getDescription();
        // when
        InstanceStatusUsers oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getChangedUsers(), newObject.getChangedUsers());
        assertEquals(oldObject.getLoginPath(), newObject.getLoginPath());
        assertEquals(oldObject.getBaseUrl(), newObject.getBaseUrl());
        assertEquals(oldObject.getAccessInfo(), newObject.getAccessInfo());
        assertEquals(oldObject.getDescription(), localizedTextList);
        assertEquals(oldObject.getRunWithTimer(), oldObject.getRunWithTimer());
    }

    @Test
    public void testConvertToNewLocalizedText() {
        // given
        LocalizedText oldObject = mock(LocalizedText.class);
        // when
        org.oscm.app.v2_0.data.LocalizedText newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getText(), newObject.getText());
    }

    @Test
    public void testConvertToOldLocalizedText() {
        // given
        org.oscm.app.v2_0.data.LocalizedText newObject = mock(org.oscm.app.v2_0.data.LocalizedText.class);
        // when
        LocalizedText oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getText(), newObject.getText());
    }

    @Test
    public void testConvertToNewOperationParameter() {
        // given
        OperationParameter oldObject = mock(OperationParameter.class);
        // when
        org.oscm.app.v2_0.data.OperationParameter newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getName(), newObject.getName());
        assertEquals(oldObject.getValue(), newObject.getValue());
    }

    @Test
    public void testConvertToOldOperationParameter() {
        // given
        org.oscm.app.v2_0.data.OperationParameter newObject = mock(org.oscm.app.v2_0.data.OperationParameter.class);
        // when
        OperationParameter oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getName(), newObject.getName());
        assertEquals(oldObject.getValue(), newObject.getValue());
    }

    @Test
    public void testConvertToNewPasswordAuthentication() {
        // given
        PasswordAuthentication oldObject = mock(PasswordAuthentication.class);
        // when
        org.oscm.app.v2_0.data.PasswordAuthentication newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getPassword(), newObject.getPassword());
        assertEquals(oldObject.getUserName(), newObject.getUserName());
    }

    @Test
    public void testConvertToOldPasswordAuthentication() {
        // given
        org.oscm.app.v2_0.data.PasswordAuthentication newObject = mock(org.oscm.app.v2_0.data.PasswordAuthentication.class);
        // when
        PasswordAuthentication oldObject = dataConverter
                .convertToOld(newObject);
        // then
        assertEquals(oldObject.getPassword(), newObject.getPassword());
        assertEquals(oldObject.getUserName(), newObject.getUserName());
    }

    @Test
    public void testConvertToNewProvisioningSettings() {
        // given
        ProvisioningSettings oldObject = mock(ProvisioningSettings.class);
        doReturn("123#").when(oldObject).getSubscriptionId();
        // when
        org.oscm.app.v2_0.data.ProvisioningSettings newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getBesLoginURL(), newObject.getBesLoginURL());
        assertEquals(oldObject.getOrganizationId(),
                newObject.getOrganizationId());
        assertEquals(oldObject.getOrganizationName(),
                newObject.getOrganizationName());
        assertEquals(oldObject.getSubscriptionId(),
                newObject.getSubscriptionId());
        assertEquals(oldObject.getConfigSettings(),
                newObject.getConfigSettings());
        assertEquals(oldObject.getParameters(), newObject.getParameters());
        assertEquals(oldObject.getAuthentication(),
                newObject.getAuthentication());
        assertEquals(oldObject.getRequestingUser(),
                newObject.getRequestingUser());
        assertEquals("123", newObject.getOriginalSubscriptionId());
    }

    @Test
    public void testConvertToOldProvisioningSettings() {
        // given
        org.oscm.app.v2_0.data.ProvisioningSettings newObject = mock(org.oscm.app.v2_0.data.ProvisioningSettings.class);
        doReturn("123#").when(newObject).getSubscriptionId();
        // when
        ProvisioningSettings oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(newObject.getLocale(), oldObject.getLocale());
        assertEquals(newObject.getBesLoginURL(), oldObject.getBesLoginURL());
        assertEquals(newObject.getOrganizationId(),
                oldObject.getOrganizationId());
        assertEquals(newObject.getOrganizationName(),
                oldObject.getOrganizationName());
        assertEquals(newObject.getSubscriptionId(),
                oldObject.getSubscriptionId());
        assertEquals(newObject.getConfigSettings(),
                oldObject.getConfigSettings());
        assertEquals(newObject.getParameters(), oldObject.getParameters());
        assertEquals(newObject.getAuthentication(),
                oldObject.getAuthentication());
        assertEquals(newObject.getRequestingUser(),
                oldObject.getRequestingUser());
        assertEquals("123", oldObject.getOriginalSubscriptionId());
    }

    @Test
    public void testConvertToNewServiceUser() {
        // given
        ServiceUser oldObject = mock((ServiceUser.class));
        // when
        org.oscm.app.v2_0.data.ServiceUser newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getApplicationUserId(),
                newObject.getApplicationUserId());
        assertEquals(oldObject.getEmail(), newObject.getEmail());
        assertEquals(oldObject.getFirstName(), newObject.getLastName());
        assertEquals(oldObject.getRoleIdentifier(),
                newObject.getRoleIdentifier());
        assertEquals(oldObject.getLastName(), newObject.getLastName());
        assertEquals(oldObject.getUserId(), newObject.getUserId());
    }

    @Test
    public void testConvertToOldServiceUser() {
        // given
        org.oscm.app.v2_0.data.ServiceUser newObject = mock((org.oscm.app.v2_0.data.ServiceUser.class));
        // when
        ServiceUser oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getApplicationUserId(),
                newObject.getApplicationUserId());
        assertEquals(oldObject.getEmail(), newObject.getEmail());
        assertEquals(oldObject.getFirstName(), newObject.getLastName());
        assertEquals(oldObject.getRoleIdentifier(),
                newObject.getRoleIdentifier());
        assertEquals(oldObject.getLastName(), newObject.getLastName());
        assertEquals(oldObject.getUserId(), newObject.getUserId());
    }

    @Test
    public void testConvertToNewUser() {
        // given
        User oldObject = mock(User.class);
        // when
        org.oscm.app.v2_0.data.User newObject = dataConverter
                .convertToNew(oldObject);
        // then
        assertEquals(oldObject.getUserId(), newObject.getUserId());
        assertEquals(oldObject.getFirstName(), newObject.getFirstName());
        assertEquals(oldObject.getLastName(), newObject.getLastName());
        assertEquals(oldObject.getEmail(), newObject.getEmail());
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getUserKey(), newObject.getUserKey());
    }

    @Test
    public void testConvertToOldUser() {
        // given
        org.oscm.app.v2_0.data.User newObject = mock(org.oscm.app.v2_0.data.User.class);
        // when
        User oldObject = dataConverter.convertToOld(newObject);
        // then
        assertEquals(oldObject.getUserId(), newObject.getUserId());
        assertEquals(oldObject.getFirstName(), newObject.getFirstName());
        assertEquals(oldObject.getLastName(), newObject.getLastName());
        assertEquals(oldObject.getEmail(), newObject.getEmail());
        assertEquals(oldObject.getLocale(), newObject.getLocale());
        assertEquals(oldObject.getUserKey(), newObject.getUserKey());
    }

}
