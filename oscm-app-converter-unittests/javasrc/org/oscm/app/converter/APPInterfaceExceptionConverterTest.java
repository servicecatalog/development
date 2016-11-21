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

import java.util.HashMap;

import org.junit.Test;
import org.oscm.app.converter.APPInterfaceExceptionConverter;

import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.AbortException;
import org.oscm.app.v1_0.exceptions.AuthenticationException;
import org.oscm.app.v1_0.exceptions.ConfigurationException;
import org.oscm.app.v1_0.exceptions.ControllerLookupException;
import org.oscm.app.v1_0.exceptions.InstanceExistsException;
import org.oscm.app.v1_0.exceptions.InstanceNotAliveException;
import org.oscm.app.v1_0.exceptions.ObjectNotFoundException;
import org.oscm.app.v1_0.exceptions.SuspendException;

public class APPInterfaceExceptionConverterTest {
    private APPInterfaceExceptionConverter exceptionConverter = new APPInterfaceExceptionConverter();

    @Test
    public void nullConversionControllerAbortException1() {
        assertNull(exceptionConverter.convertToNew((AbortException) null));
    }

    @Test
    public void nullConversionControllerAbortException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.AbortException) null));
    }

    @Test
    public void nullConversionControllerAPPlatformException1() {
        assertNull(exceptionConverter.convertToNew((APPlatformException) null));
    }

    @Test
    public void nullConversionControllerAPPlatformException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.APPlatformException) null));
    }

    @Test
    public void nullConversionControllerAuthenticationException1() {
        assertNull(exceptionConverter
                .convertToNew((AuthenticationException) null));
    }

    @Test
    public void nullConversionControllerAuthenticationException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.AuthenticationException) null));
    }

    @Test
    public void nullConversionControllerConfigurationException1() {
        assertNull(exceptionConverter
                .convertToNew((ConfigurationException) null));
    }

    @Test
    public void nullConversionControllerConfigurationException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.ConfigurationException) null));
    }

    @Test
    public void nullConversionControllerControllerLookupException1() {
        assertNull(exceptionConverter
                .convertToNew((ControllerLookupException) null));
    }

    @Test
    public void nullConversionControllerControllerLookupException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.ControllerLookupException) null));
    }

    @Test
    public void nullConversionControllerInstanceExistsException1() {
        assertNull(exceptionConverter
                .convertToNew((InstanceExistsException) null));
    }

    @Test
    public void nullConversionControllerInstanceExistsException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.InstanceExistsException) null));
    }

    @Test
    public void nullConversionControllerInstanceNotAliveException1() {
        assertNull(exceptionConverter
                .convertToNew((InstanceNotAliveException) null));
    }

    @Test
    public void nullConversionControllerInstanceNotAliveException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.InstanceNotAliveException) null));
    }

    @Test
    public void nullConversionControllerObjectNotFoundException1() {
        assertNull(exceptionConverter
                .convertToNew((ObjectNotFoundException) null));
    }

    @Test
    public void nullConversionControllerObjectNotFoundException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.ObjectNotFoundException) null));
    }

    @Test
    public void nullConversionControllerSuspendException1() {
        assertNull(exceptionConverter.convertToNew((SuspendException) null));
    }

    @Test
    public void nullConversionControllerSuspendException2() {
        assertNull(exceptionConverter
                .convertToOld((org.oscm.app.v2_0.exceptions.SuspendException) null));
    }

    @Test
    public void testConvertToNewAbortException() {
        // given
        AbortException oldEx = mock(AbortException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.AbortException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());
        assertEquals(oldEx.getProviderMessages(), newEx.getProviderMessages());
    }

    @Test
    public void testConvertToOldAbortException() {
        // given
        org.oscm.app.v2_0.exceptions.AbortException newEx = mock(org.oscm.app.v2_0.exceptions.AbortException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        AbortException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());
        assertEquals(oldEx.getProviderMessages(), newEx.getProviderMessages());
    }

    @Test
    public void testConvertToNewAPPlatformException() {
        // given
        APPlatformException oldEx = mock(APPlatformException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.APPlatformException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());
    }

    @Test
    public void testConvertToOldAPPlatformException() {
        // given
        org.oscm.app.v2_0.exceptions.APPlatformException newEx = mock(org.oscm.app.v2_0.exceptions.APPlatformException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        APPlatformException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());
    }

    @Test
    public void testConvertToNewAuthenticationException() {
        // given
        AuthenticationException oldEx = mock(AuthenticationException.class);
        // when
        org.oscm.app.v2_0.exceptions.AuthenticationException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getCause(), newEx.getCause());
    }

    @Test
    public void testConvertToOldAuthenticationException() {
        // given
        org.oscm.app.v2_0.exceptions.AuthenticationException newEx = mock(org.oscm.app.v2_0.exceptions.AuthenticationException.class);
        // when
        AuthenticationException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getCause(), newEx.getCause());
    }

    @Test
    public void testConvertToNewConfigurationException() {
        // given
        ConfigurationException oldEx = mock(ConfigurationException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.ConfigurationException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getAffectedKey(), newEx.getAffectedKey());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
    }

    @Test
    public void testConvertToOldConfigurationException() {
        // given
        org.oscm.app.v2_0.exceptions.ConfigurationException newEx = mock(org.oscm.app.v2_0.exceptions.ConfigurationException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        ConfigurationException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getAffectedKey(), newEx.getAffectedKey());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
    }

    @Test
    public void testConvertToNewControllerLookupException() {
        // given
        ControllerLookupException oldEx = mock(ControllerLookupException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.ControllerLookupException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());
    }

    @Test
    public void testConvertToOldControllerLookupException() {
        // given
        org.oscm.app.v2_0.exceptions.ControllerLookupException newEx = mock(org.oscm.app.v2_0.exceptions.ControllerLookupException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        ControllerLookupException oldEx = exceptionConverter
                .convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());

    }

    @Test
    public void testConvertToNewInstanceExistsException() {
        // given
        InstanceExistsException oldEx = mock(InstanceExistsException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.InstanceExistsException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());
    }

    @Test
    public void testConvertToOldInstanceExistsException() {
        // given
        org.oscm.app.v2_0.exceptions.InstanceExistsException newEx = mock(org.oscm.app.v2_0.exceptions.InstanceExistsException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        InstanceExistsException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());
    }

    @Test
    public void testConvertToNewInstanceNotAliveException() {
        // given
        InstanceNotAliveException oldEx = mock(InstanceNotAliveException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.InstanceNotAliveException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());
    }

    @Test
    public void testConvertToOldInstanceNotAliveException() {
        // given
        org.oscm.app.v2_0.exceptions.InstanceNotAliveException newEx = mock(org.oscm.app.v2_0.exceptions.InstanceNotAliveException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        InstanceNotAliveException oldEx = exceptionConverter
                .convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());
    }

    @Test
    public void testConvertToNewObjectNotFoundException() {
        // given
        ObjectNotFoundException oldEx = mock(ObjectNotFoundException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.ObjectNotFoundException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());

    }

    @Test
    public void testConvertToOldObjectNotFoundException() {
        // given
        org.oscm.app.v2_0.exceptions.ObjectNotFoundException newEx = mock(org.oscm.app.v2_0.exceptions.ObjectNotFoundException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        ObjectNotFoundException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());

    }

    @Test
    public void testConvertToNewSuspendException() {
        // given
        SuspendException oldEx = mock(SuspendException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(oldEx).getChangedParameters();
        // when
        org.oscm.app.v2_0.exceptions.SuspendException newEx = exceptionConverter
                .convertToNew(oldEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, newEx.getChangedParameters());

    }

    @Test
    public void testConvertToOldSuspendException() {
        // given
        org.oscm.app.v2_0.exceptions.SuspendException newEx = mock(org.oscm.app.v2_0.exceptions.SuspendException.class);
        HashMap<String, String> map = new HashMap<>(10);
        doReturn(map).when(newEx).getChangedParameters();
        // when
        SuspendException oldEx = exceptionConverter.convertToOld(newEx);
        // then
        assertEquals(oldEx.getLocalizedMessage(), newEx.getLocalizedMessage());
        assertEquals(oldEx.getMessage(), newEx.getMessage());
        assertEquals(oldEx.getLocalizedMessages(), newEx.getLocalizedMessages());
        assertEquals(map, oldEx.getChangedParameters());

    }

}
