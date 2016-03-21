/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.saml.api;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * Provides utility methods for encoding SAML messages
 * 
 * @author barzu
 */
public class SamlEncoder {

    static final String ENCODING = "UTF-8";

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(SamlEncoder.class);

    public static String encodeBase64(String s) {
        try {
            byte[] xmlBytes = s.getBytes(ENCODING);
            Base64 base64Encoder = new Base64();
            byte[] base64EncodedByteArray = base64Encoder.encode(xmlBytes);
            return new String(base64EncodedByteArray);
        } catch (UnsupportedEncodingException e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Unsupported encoding: " + ENCODING, e);
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_UNSUPPORTED_ENCODING);
            throw (se);
        }
    }

}
