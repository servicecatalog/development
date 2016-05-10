package com.fujitsu.bss.app.vmware.aes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple AES Encryption
 */
public class AESEncrypterVm {

    private final static Logger log = LoggerFactory
            .getLogger(AESEncrypterVm.class);

    /** The Constant ENCRYPTION_KEY - generated only once here. */
    private final static String ENCRYPTION_KEY = StringScramblerVm
            .decode(new long[] { 0xE657A7C4BA60BFB4L, 0x195E82CF1526EBDBL,
                    0x1641D5A7183FB595L, 0xA9F49ADE53D57A3CL })
            .toString();

    /**
     * The prefix used for encrypted keys for distinguishing from 10.1 encoding
     */
    private final static byte[] KEY_PREFIX = new String("__AES__").getBytes();

    /**
     * Encrypt a given string based on a shared secret.
     * 
     * @return the encrypted text as Base64
     */
    public static String encrypt(String text) throws Exception {
        return new String(encrypt(text.getBytes()));
    }

    /**
     * Encrypt a given byte array based on a shared secret.
     * 
     * @return the encrypted bytes as Base64
     */
    public static byte[] encrypt(byte[] bytes) throws Exception {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(
                    Base64.decodeBase64(ENCRYPTION_KEY.getBytes()), "AES");

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] encrypted = cipher.doFinal(bytes);
            byte[] encryptedPrefix = new byte[KEY_PREFIX.length
                    + encrypted.length];
            System.arraycopy(KEY_PREFIX, 0, encryptedPrefix, 0,
                    KEY_PREFIX.length);
            System.arraycopy(encrypted, 0, encryptedPrefix, KEY_PREFIX.length,
                    encrypted.length);

            return Base64.encodeBase64(encryptedPrefix);
        } catch (Exception e) {
            log.error("Encryption failed.", e);
            throw new Exception(e);
        }
    }

    /**
     * Decrypts a given text.
     * 
     * @param encrypted
     *            the encrypted
     * 
     * @return the string
     */
    public static String decrypt(String encrypted) throws Exception {
        return new String(decrypt(encrypted.getBytes()));
    }

    /**
     * Decrypts a given byte array.
     * 
     * @param encrypted
     *            the encrypted
     * 
     * @return the byte array
     */
    public static byte[] decrypt(byte[] encrypted) throws Exception {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(
                    Base64.decodeBase64(ENCRYPTION_KEY.getBytes()), "AES");

            byte[] decoded = Base64.decodeBase64(encrypted);

            boolean is103 = (decoded.length >= KEY_PREFIX.length);
            for (int i = 0; is103 && i < KEY_PREFIX.length; ++i) {
                if (decoded[i] != KEY_PREFIX[i]) {
                    is103 = false;
                }
            }
            if (!is103) {
                return decoded;
            }

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decodedWithoutPrefix = new byte[decoded.length
                    - KEY_PREFIX.length];
            System.arraycopy(decoded, KEY_PREFIX.length, decodedWithoutPrefix,
                    0, decoded.length - KEY_PREFIX.length);

            byte[] original = cipher.doFinal(decodedWithoutPrefix);
            return original;
        } catch (Exception e) {
            log.error("Decryption failed.", e);
            throw new Exception(e);
        }
    }

}
