/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.converter.ResourceLoader;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.exception.SaaSSystemException;

public class KeyLoader {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(KeyLoader.class);

    public enum Algorithm {
        RSA
    };

    public static PrivateKey getPrivateKey(Class<?> clazz, String resource,
            Algorithm algorithm) {
        byte[] bytes = ResourceLoader.load(clazz, resource);
        return getPrivateKey(bytes, algorithm);
    }

    public static PrivateKey getPrivateKey(byte[] bytes, Algorithm algorithm) {
        try {
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory factory = KeyFactory.getInstance(algorithm.name());
            return factory.generatePrivate(privSpec);
        } catch (NoSuchAlgorithmException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Unsupported private key algorithm: " + e.getMessage(), e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_UNSUPPORTED_PRIVATE_KEY_ALGORITHM);
            throw se;
        } catch (InvalidKeySpecException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Invalid private key specification: " + e.getMessage(), e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_INVALID_PRIVATE_KEY_SPECIFICATION);
            throw se;
        }
    }

    public static PublicKey getPublicKey(Class<?> clazz, String path,
            Algorithm algorithm) {
        byte[] bytes = ResourceLoader.load(clazz, path);
        return getPublicKey(bytes, algorithm);
    }

    public static PublicKey getPublicKey(byte[] bytes, Algorithm algorithm) {
        try {
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
            KeyFactory factory = KeyFactory.getInstance(algorithm.name());
            return factory.generatePublic(pubSpec);
        } catch (NoSuchAlgorithmException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Unsupported public key algorithm: " + e.getMessage(), e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_UNSUPPORTED_PRIVATE_KEY_ALGORITHM);
            throw se;
        } catch (InvalidKeySpecException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Invalid public key specification " + e.getMessage(), e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_INVALID_PRIVATE_KEY_SPECIFICATION);
            throw se;
        }
    }

    public static X509Certificate getPublicCertificate(Class<?> clazz,
            String path) {
        return getPublicCertificate(ResourceLoader.getResourceAsStream(clazz,
                path));
    }

    public static X509Certificate getPublicCertificate(
            InputStream certificateStream) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(certificateStream);
        } catch (CertificateException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Unable to read input stream: " + e.getMessage(), e);
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_FIND_INPUT_STREAM_FAILED);
            throw se;
        } finally {
            try {
                if (certificateStream != null) {
                    certificateStream.close();
                }
            } catch (IOException e) {
                SaaSSystemException se = new SaaSSystemException(
                        "Unable to close input stream: " + e.getMessage(), e);
                logger.logError(Log4jLogger.SYSTEM_LOG, se,
                        LogMessageIdentifier.ERROR_CLOSE_INPUT_STREAM_FAILED);
                throw se;
            }
        }
    }

}
