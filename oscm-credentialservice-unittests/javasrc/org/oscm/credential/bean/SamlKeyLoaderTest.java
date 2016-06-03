/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: Apr 20, 2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.credential.bean;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.junit.Test;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * @author barzu
 * 
 */
public class SamlKeyLoaderTest {

    private abstract class ConfigurationServiceLocalStub implements
            ConfigurationServiceLocal {

        abstract String getPath();

        @Override
        @SuppressWarnings("serial")
        public ConfigurationSetting getConfigurationSetting(
                ConfigurationKey informationId, String contextId) {
            return new ConfigurationSetting() {
                @Override
                public String getValue() {
                    return getPath();
                }
            };
        }

        @Override
        public long getLongConfigurationSetting(ConfigurationKey informationId,
                String contextId) {
            return 0;
        }

        @Override
        public void setConfigurationSetting(ConfigurationSetting configSetting) {

        }

        @Override
        public String getNodeName() {
            return null;
        }

        @Override
        public List<ConfigurationSetting> getAllConfigurationSettings() {
            return null;
        }

        @Override
        public boolean isCustomerSelfRegistrationEnabled() {
            return true;
        }

        @Override
        public long getBillingRunOffsetInMs() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getBillingRunStartTimeInMs() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getBaseURL() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean isPaymentInfoAvailable() {
            return true;
        }
    }

    private String getFilePath(String fileName) {
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(fileName);
        String path = url.getPath();
        System.out.println(url.getPath());
        assertTrue("The path should contain the file name",
                path.contains(fileName));
        return url.getPath();
    }

    @Test(expected = SaaSSystemException.class)
    public void testPrivateKeyNullPath() {
        ConfigurationServiceLocal configurationService = new ConfigurationServiceLocalStub() {

            @Override
            String getPath() {
                return null;
            }

            @Override
            public boolean isServiceProvider() {

                return false;
            }
        };
        new SamlKeyLoader(configurationService).getPrivateKey();

    }

    @Test
    public void testPrivateKeyInvalidPath() {
        ConfigurationServiceLocal configurationService = new ConfigurationServiceLocalStub() {

            @Override
            String getPath() {
                return "invalid/path";
            }

            @Override
            public boolean isServiceProvider() {

                return false;
            }
        };
        try {
            new SamlKeyLoader(configurationService).getPrivateKey();
            fail("The private key file should not be found, as the path is invalid");
        } catch (SaaSSystemException e) {
            assertTrue(
                    "The private key file should not be found, as the path is invalid",
                    e.getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    public void testPrivateKeyCorrectPath() {
        ConfigurationServiceLocal configurationService = new ConfigurationServiceLocalStub() {

            @Override
            String getPath() {
                return getFilePath("wrong_cakey.der");
            }

            @Override
            public boolean isServiceProvider() {

                return false;
            }
        };
        PrivateKey privateKey = new SamlKeyLoader(configurationService)
                .getPrivateKey();
        assertNotNull("The private key file was not found", privateKey);
    }

    @Test(expected = SaaSSystemException.class)
    public void testPublicCertificateNullPath() {
        ConfigurationServiceLocal configurationService = new ConfigurationServiceLocalStub() {

            @Override
            String getPath() {
                return null;
            }

            @Override
            public boolean isServiceProvider() {

                return false;
            }
        };
        new SamlKeyLoader(configurationService).getPublicCertificate();

    }

    @Test
    public void testPublicCertificateInvalidPath() {
        ConfigurationServiceLocal configurationService = new ConfigurationServiceLocalStub() {

            @Override
            String getPath() {
                return "invalid/path";
            }

            @Override
            public boolean isServiceProvider() {

                return false;
            }
        };
        try {
            new SamlKeyLoader(configurationService).getPublicCertificate();
            fail("The public certificate file should not be found, as the path is invalid");
        } catch (SaaSSystemException e) {
            assertTrue(
                    "The public certificate file should not be found, as the path is invalid",
                    e.getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    public void testPublicCertificateCorrectPath() {
        ConfigurationServiceLocal configurationService = new ConfigurationServiceLocalStub() {

            @Override
            String getPath() {
                return getFilePath("wrong_cacert.der");
            }

            @Override
            public boolean isServiceProvider() {

                return false;
            }
        };
        X509Certificate privateKey = new SamlKeyLoader(configurationService)
                .getPublicCertificate();
        assertNotNull("The public certificate file was not found", privateKey);
    }

}
