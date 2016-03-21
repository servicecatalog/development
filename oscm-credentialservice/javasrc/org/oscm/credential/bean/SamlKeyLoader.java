/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.credential.bean;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.converter.ResourceLoader;
import org.oscm.saml.api.KeyLoader;
import org.oscm.saml.api.KeyLoader.Algorithm;
import org.oscm.types.constants.Configuration;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.SaaSSystemException;

/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2009 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: Apr 20, 2011                                                      
 *                                                                              
 *******************************************************************************/

/**
 * Locates and loads the private key and the public certificate for signing SAMl
 * responses.
 * 
 * @author barzu
 */
class SamlKeyLoader {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(SamlKeyLoader.class);

    private ConfigurationServiceLocal configService;

    SamlKeyLoader(ConfigurationServiceLocal configService) {
        this.configService = configService;
    }

    PrivateKey getPrivateKey() {

        String privateKeyPath = configService.getConfigurationSetting(
                ConfigurationKey.IDP_PRIVATE_KEY_FILE_PATH,
                Configuration.GLOBAL_CONTEXT).getValue();
        if (privateKeyPath == null) {
            SaaSSystemException se = new SaaSSystemException(
                    "Mandatory property ' "
                            + ConfigurationKey.IDP_PRIVATE_KEY_FILE_PATH.name()
                            + "' not set!");
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_MANDATORY_PROPERTY_NOT_SET,
                    ConfigurationKey.IDP_PRIVATE_KEY_FILE_PATH.name());
            throw se;
        }

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(
                    privateKeyPath));
            byte[] bytes = ResourceLoader.load(in);
            return KeyLoader.getPrivateKey(bytes, Algorithm.RSA);
        } catch (FileNotFoundException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Unable to read file: " + privateKeyPath + ". "
                            + e.getMessage(), e);
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_READ_FILE_FAILED, privateKeyPath);
            throw se;
        }
    }

    X509Certificate getPublicCertificate() {
        String privateKeyPath = configService.getConfigurationSetting(
                ConfigurationKey.IDP_PUBLIC_CERTIFICATE_FILE_PATH,
                Configuration.GLOBAL_CONTEXT).getValue();
        if (privateKeyPath == null) {
            SaaSSystemException se = new SaaSSystemException(
                    "Mandatory property ' "
                            + ConfigurationKey.IDP_PUBLIC_CERTIFICATE_FILE_PATH
                                    .name() + "' not set!");
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_MANDATORY_PROPERTY_NOT_SET,
                    String.valueOf(ConfigurationKey.IDP_PUBLIC_CERTIFICATE_FILE_PATH));
            throw se;
        }

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(
                    privateKeyPath));
            return KeyLoader.getPublicCertificate(in);
        } catch (FileNotFoundException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Unable to read file: " + privateKeyPath + ". "
                            + e.getMessage(), e);
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_READ_FILE_FAILED, privateKeyPath);
            throw se;
        }
    }

}
