/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-10-10
 *
 *******************************************************************************/
package org.oscm.encrypter;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;

/**
 * Created by BadziakP on 2016-10-10.
 */
public class AESEncrypter {

    private static final Log4jLogger LOG = LoggerFactory
            .getLogger(AESEncrypter.class);

    public static final int KEY_BYTES = 16;

    private static SecretKey key;

    public static byte[] getKey() {
        return key.getEncoded();
    }

    public static void setKey(byte[] key) {
        AESEncrypter.key = new SecretKeySpec(key, "AES");
    }

    public static void generateKey() {

        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(KEY_BYTES * 8);
            key = generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            // ignore
        }
    }

    /**
     * Encrypts a given string based on a shared secret.
     *
     * @param text
     *            the text to encrypt
     * @return the encrypted text as Base64
     * @throws GeneralSecurityException
     *             on any problem during encryption
     */
    public static String encrypt(String text) throws GeneralSecurityException {
        return new String(encrypt(text.getBytes()));
    }

    /**
     * Encrypts a given byte array based on a secret from file given as
     * parameter
     *
     * @param bytes
     *            the bytes to encrypt
     * @return the encrypted bytes as Base64
     * @throws GeneralSecurityException
     *             on any problem during encryption
     */
    private static byte[] encrypt(byte[] bytes)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrypted = cipher.doFinal(bytes);
        return Base64.encodeBase64(encrypted);
    }

    /**
     * Decrypts a given text.
     *
     * @param encrypted
     *            the encrypted text
     * @return the string
     */
    public static String decrypt(String encrypted)
            throws GeneralSecurityException {
        return new String(decrypt(encrypted.getBytes()));
    }

    /**
     * Decrypts a given byte array.
     *
     * @param encrypted
     *            the encrypted text
     * @return the byte array
     */
    private static byte[] decrypt(byte[] encrypted)
            throws GeneralSecurityException {

        byte[] decoded = Base64.decodeBase64(encrypted);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        try {
            return cipher.doFinal(decoded);
        } catch (BadPaddingException exc) {
            LOG.logError(Log4jLogger.SYSTEM_LOG, exc,
                    LogMessageIdentifier.ERROR_BAD_PASSWORD);
            return "".getBytes();
        }
    }
}