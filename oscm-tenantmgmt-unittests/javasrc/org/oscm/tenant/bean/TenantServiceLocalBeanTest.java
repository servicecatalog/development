/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.bean;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Tenant;
import org.oscm.domobjects.TenantSetting;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.tenant.dao.TenantDao;

public class TenantServiceLocalBeanTest {

    private TenantServiceLocalBean tenantServiceLocalBean;
    private TenantDao tenantDao;
    private DataService dataService;

    @Before
    public void setup() {
        tenantServiceLocalBean = spy(new TenantServiceLocalBean());
        tenantDao = mock(TenantDao.class);
        dataService = mock(DataService.class);
        tenantServiceLocalBean.setTenantDao(tenantDao);
        tenantServiceLocalBean.setDataManager(dataService);
    }

    @Test
    public void testGetAllTenants() {
        //given
        when(tenantDao.getAllTenants()).thenReturn(new ArrayList<Tenant>());

        //when
        tenantServiceLocalBean.getAllTenants();

        //then
        verify(tenantDao, times(1)).getAllTenants();
    }

    @Test
    public void testGetTenantByTenantId() throws ObjectNotFoundException {
        //given
        when(tenantDao.getTenantByTenantId(anyString())).thenReturn(new Tenant());

        //when
        tenantServiceLocalBean.getTenantByTenantId("tenantId");

        //then
        verify(tenantDao, times(1)).getTenantByTenantId("tenantId");
    }

    @Test
    public void testSaveTenant() throws NonUniqueBusinessKeyException {
        //given
        doNothing().when(dataService).persist(any(Tenant.class));

        //when
        tenantServiceLocalBean.saveTenant(new Tenant());

        //then
        verify(dataService, times(1)).persist(any(Tenant.class));
    }

    @Test
    public void testGetTenantByKey() throws ObjectNotFoundException {
        //given
        Tenant t = new Tenant();
        t.setKey(1L);
        when(dataService.getReference(Tenant.class, 1L)).thenReturn(t);

        //when
        Tenant result = tenantServiceLocalBean.getTenantByKey(1L);

        //then
        assertEquals(result.getKey(), t.getKey());
    }

    @Test
    public void testRemovetenant() {
        //given
        doNothing().when(dataService).remove(any(Tenant.class));

        //when
        tenantServiceLocalBean.removeTenant(new Tenant());

        //then
        verify(dataService, times(1)).remove(any(Tenant.class));
    }

    @Test
    public void testSaveTenantSetting() throws NonUniqueBusinessKeyException {
        //given
        doNothing().when(dataService).persist(any(TenantSetting.class));

        //when
        tenantServiceLocalBean.saveTenantSetting(new TenantSetting());

        //then
        verify(dataService, times(1)).persist(any(TenantSetting.class));
    }

    @Test
    public void testRemoveTenantSetting() throws NonUniqueBusinessKeyException, ObjectNotFoundException {
        //given
        doNothing().when(dataService).remove(any(TenantSetting.class));

        //when
        tenantServiceLocalBean.removeTenantSetting(new TenantSetting());

        //then
        verify(dataService, times(1)).remove(any(TenantSetting.class));
    }
}
