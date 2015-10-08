/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 17.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v1_0.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;

import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;
import org.oscm.app.domain.ConfigurationSetting;
import org.oscm.app.domain.PlatformConfigurationKey;
import org.oscm.app.v1_0.data.ControllerConfigurationKey;
import org.oscm.app.v1_0.exceptions.ConfigurationException;

public class APPConfigurationServiceIT extends EJBTestBase {

    private APPConfigurationServiceBean cs;
    private EntityManager em;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.addBean(new APPConfigurationServiceBean());

        cs = container.get(APPConfigurationServiceBean.class);
        em = container.getPersistenceUnit("oscm-app");
    }

    @Test(expected = ConfigurationException.class)
    public void testGetConfigurationSettingNoHit() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                cs.getProxyConfigurationSetting(PlatformConfigurationKey.BSS_WEBSERVICE_URL);
                return null;
            }
        });
    }

    @Test
    public void getConfigurationSetting_APP_SUSPEND_Null() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                assertEquals(
                        "",
                        cs.getProxyConfigurationSetting(PlatformConfigurationKey.APP_SUSPEND));
                return null;
            }
        });
    }

    @Test
    public void testGetConfigurationSettingHit() throws Exception {
        final String setting = PlatformConfigurationKey.BSS_WEBSERVICE_URL
                .name();
        createConfigSetting(setting, "testValue");
        String result = runTX(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return cs
                        .getProxyConfigurationSetting(PlatformConfigurationKey.BSS_WEBSERVICE_URL);
            }
        });
        Assert.assertNotNull(result);
        Assert.assertEquals("testValue", result);
    }

    @Test(expected = ConfigurationException.class)
    public void testGetAllConfigurationSettings_NoCS() throws Exception {
        runTX(new Callable<Map<String, String>>() {
            @Override
            public Map<String, String> call() throws Exception {
                return cs.getAllProxyConfigurationSettings();
            }
        });
    }

    @Test
    public void testGetAllConfigurationSettings_TwoHits() throws Exception {
        createConfigSetting("setting1", "testValue");
        createConfigSetting("setting2"
                + APPConfigurationServiceBean.CRYPT_KEY_SUFFIX,
                APPConfigurationServiceBean.CRYPT_PREFIX + "testValue");
        createConfigSetting("setting3"
                + APPConfigurationServiceBean.CRYPT_KEY_SUFFIX_PASS,
                APPConfigurationServiceBean.CRYPT_PREFIX + "testValue");
        PlatformConfigurationKey[] keys = PlatformConfigurationKey.values();
        for (int i = 0; i < keys.length; i++) {
            createConfigSetting(keys[i].name(), "testValue");
        }
        Map<String, String> result = runTX(new Callable<Map<String, String>>() {
            @Override
            public Map<String, String> call() throws Exception {
                return cs.getAllProxyConfigurationSettings();
            }
        });
        assertNotNull(result);
        assertEquals(keys.length + 3, result.keySet().size());
        assertTrue(result.keySet().contains("setting1"));
        assertTrue(result.keySet().contains("setting2_PWD"));
        assertTrue(result.keySet().contains("setting3_PASS"));
        assertEquals("testValue", result.get("setting1"));
        assertEquals("testValue", result.get("setting2_PWD"));
        assertEquals("testValue", result.get("setting3_PASS"));
    }

    @Test(expected = ConfigurationException.class)
    public void testControllerSetting_allEmpty() throws Exception {
        runTX(new Callable<Map<String, String>>() {
            @Override
            public Map<String, String> call() throws Exception {
                return cs.getControllerConfigurationSettings("");
            }
        });
    }

    @Test(expected = ConfigurationException.class)
    public void testControllerSetting_null() throws Exception {
        runTX(new Callable<Map<String, String>>() {
            @Override
            public Map<String, String> call() throws Exception {
                return cs.getControllerConfigurationSettings(null);
            }
        });
    }

    @Test
    public void testControllerSetting() throws Exception {
        createContorllerConfigSetting("controller1",
                ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(), "value");
        createContorllerConfigSetting("controller1", "instanceValueKey",
                "value");
        createContorllerConfigSetting("controller2", "key1", "value");
        createContorllerConfigSetting("controller2", "key2", "value");
        createContorllerConfigSetting("controller3", "key1", "value");
        Map<String, String> result = runTX(new Callable<Map<String, String>>() {
            @Override
            public Map<String, String> call() throws Exception {
                return cs.getControllerConfigurationSettings("controller1");
            }
        });
        assertNotNull(result);
        assertTrue(result.keySet().size() == 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreControllerSetting_null1() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    cs.storeControllerConfigurationSettings(null,
                            new HashMap<String, String>());
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreControllerSetting_null2() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    cs.storeControllerConfigurationSettings("ess.test", null);
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testStoreControllerOrganizations() throws Throwable {
        final HashMap<String, String> controllerOrganizations = new HashMap<String, String>();
        controllerOrganizations.put("c1", "value1");
        controllerOrganizations.put("c2", "value2");
        controllerOrganizations.put("c3", "value3");
        controllerOrganizations.put("c4", "value4");
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    cs.storeControllerOrganizations(controllerOrganizations);
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
        controllerOrganizations.clear();
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    controllerOrganizations.putAll(cs
                            .getControllerOrganizations());
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
        assertTrue(controllerOrganizations.size() == 4);
        assertEquals("value1", controllerOrganizations.get("c1"));
        assertEquals("value2", controllerOrganizations.get("c2"));
        assertEquals("value3", controllerOrganizations.get("c3"));
        assertEquals("value4", controllerOrganizations.get("c4"));
        controllerOrganizations.clear();
        controllerOrganizations.put("c1", "other");
        controllerOrganizations.put("c2", null);
        controllerOrganizations.put("c3", "");
        // c4 stays untouched
        controllerOrganizations.put("c5", "new");
        // will be ignored when adding
        controllerOrganizations.put("c12", null);
        controllerOrganizations.put("c13", "");
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    cs.storeControllerOrganizations(controllerOrganizations);
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
        controllerOrganizations.clear();
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    controllerOrganizations.putAll(cs
                            .getControllerOrganizations());
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
        assertTrue(controllerOrganizations.size() == 3);
        assertEquals("other", controllerOrganizations.get("c1"));
        assertEquals("value4", controllerOrganizations.get("c4"));
        assertEquals("new", controllerOrganizations.get("c5"));
    }

    @Test
    public void testStoreControllerSetting() throws Throwable {
        createContorllerConfigSetting("controller1", "instanceValue", "value");
        createContorllerConfigSetting("controller1",
                ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(), "value");
        createContorllerConfigSetting("controller1", "leave", "alone");
        createContorllerConfigSetting("controller1", "update", "old");
        createContorllerConfigSetting("controller1", "update_PWD",
                APPConfigurationServiceBean.CRYPT_PREFIX + "old_crypt");
        createContorllerConfigSetting("controller1", "delete", "old");
        createContorllerConfigSetting("controller2", "instanceValue",
                "value1_c2");
        createContorllerConfigSetting("controller2",
                ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(),
                "value2_c2");
        createContorllerConfigSetting("controller3", "instanceValue",
                "value1_c3");
        createContorllerConfigSetting("controller3",
                ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(),
                "value2_c3");
        final HashMap<String, String> result1 = new HashMap<String, String>();
        final HashMap<String, String> result2 = new HashMap<String, String>();
        final HashMap<String, String> result3 = new HashMap<String, String>();
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("update", "new");
                    map.put("update_PWD", "new_crypt");
                    map.put("create", "very_new");
                    map.put("create_PWD", "very_new_crypt");
                    map.put("delete", null);
                    cs.storeControllerConfigurationSettings("controller1", map);
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    result1.putAll(cs
                            .getControllerConfigurationSettings("controller1"));
                    result2.putAll(cs
                            .getControllerConfigurationSettings("controller2"));
                    result3.putAll(cs
                            .getControllerConfigurationSettings("controller3"));
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
        assertTrue(result1.keySet().size() == 7);
        assertEquals("alone", result1.get("leave"));
        assertEquals("new", result1.get("update"));
        assertEquals("new_crypt", result1.get("update_PWD"));
        assertEquals("very_new", result1.get("create"));
        assertEquals("very_new_crypt", result1.get("create_PWD"));
        assertFalse(result1.containsKey("delete"));

        assertTrue(result2.keySet().size() == 2);
        assertEquals("value1_c2", result2.get("instanceValue"));
        assertEquals("value2_c2",
                result2.get(ControllerConfigurationKey.BSS_ORGANIZATION_ID
                        .name()));
        assertTrue(result3.keySet().size() == 2);
        assertEquals("value1_c3", result3.get("instanceValue"));
        assertEquals("value2_c3",
                result3.get(ControllerConfigurationKey.BSS_ORGANIZATION_ID
                        .name()));
    }

    /**
     * Creates and persists a configuration setting.
     * 
     * @param key
     * @param value
     * @throws Exception
     */
    private void createConfigSetting(final String key, final String value)
            throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                String val = value == null ? "testValue" : value;
                ConfigurationSetting cs = new ConfigurationSetting();
                cs.setSettingKey(key);
                if (key.endsWith(APPConfigurationServiceBean.CRYPT_KEY_SUFFIX)
                        && !val.startsWith(APPConfigurationServiceBean.CRYPT_PREFIX)) {
                    val = APPConfigurationServiceBean.CRYPT_PREFIX + val;
                }
                cs.setSettingValue(val);
                em.persist(cs);
                return null;
            }
        });
    }

    /**
     * Creates and persists a controller specific configuration setting.
     * 
     * @param controllerId
     * @param settingKey
     * @param value
     * @throws Exception
     */
    private void createContorllerConfigSetting(final String controllerId,
            final String settingKey, final String value) throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ConfigurationSetting cs = new ConfigurationSetting();
                cs.setControllerId(controllerId);
                cs.setSettingKey(settingKey);
                cs.setSettingValue(value);
                em.persist(cs);
                return null;
            }
        });
    }

}
