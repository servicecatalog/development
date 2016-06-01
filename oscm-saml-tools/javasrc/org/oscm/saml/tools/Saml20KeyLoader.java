/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.tools;

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
 * Locates and loads the private key and the public certificate for signing SAML
 * responses.
 * 
 * @author barzu
 */
public class Saml20KeyLoader {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(Saml20KeyLoader.class);

    private ConfigurationServiceLocal configService;

    public Saml20KeyLoader(ConfigurationServiceLocal configService) {
        this.configService = configService;
    }

    public PrivateKey getPrivateKey() {

        String privateKeyPath = configService.getConfigurationSetting(
                getPrivateKeyFilePath(),
                Configuration.GLOBAL_CONTEXT).getValue();
        if (privateKeyPath == null) {
            SaaSSystemException se = new SaaSSystemException(
                    "Mandatory property ' "
                            + getPrivateKeyFilePath().name()
                            + "' not set!");
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_MANDATORY_PROPERTY_NOT_SET,
                    getPrivateKeyFilePath().name());
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

    protected ConfigurationKey getPrivateKeyFilePath() {
        return ConfigurationKey.SP_PRIVATE_KEY_FILE_PATH;
    }

    public X509Certificate getPublicCertificate() {
        String privateKeyPath = configService.getConfigurationSetting(
                getPublicCertificateFilePath(),
                Configuration.GLOBAL_CONTEXT).getValue();
        if (privateKeyPath == null) {
            SaaSSystemException se = new SaaSSystemException(
                    "Mandatory property ' "
                            + getPublicCertificateFilePath()
                                    .name() + "' not set!");
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_MANDATORY_PROPERTY_NOT_SET,
                    String.valueOf(getPublicCertificateFilePath()));
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

    protected ConfigurationKey getPublicCertificateFilePath() {
        return ConfigurationKey.SP_PUBLIC_CERTIFICATE_FILE_PATH;
    }

}
