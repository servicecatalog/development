/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2015 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 04.09.2015                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.aes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author kulle
 *
 */
public class AesTest {

    @Test
    public void vm() throws Exception {
        // given
        String password = "e$_w=x268WEI3vfr7";
        String vmEncrypted = AESEncrypterVm.encrypt(password);

        // when
        String vmDecrypted = AESEncrypterVm.decrypt(vmEncrypted);

        // then
        assertEquals(password, vmDecrypted);
    }

    @Test
    public void ctmg() throws Exception {
        // given
        String password = "e$_w=x268WEI3vfr7";
        String encrypted = AESEncrypter.encrypt(password);

        // when
        String decrypted = AESEncrypter.decrypt(encrypted);

        // then
        assertEquals(password, decrypted);
    }

    @Test
    public void ctmg_vm() throws Exception {
        // given
        String password = "e$_w=x268WEI3vfr7";
        String encrypted = AESEncrypter.encrypt(password);

        // when
        String decrypt = AESEncrypterVm.decrypt(encrypted);

        // then
        assertTrue("e$_w=x268WEI3vfr7".equals(decrypt));
    }

    @Test
    public void decryptCtmgPassword() throws Exception {
        // given
        String encrypted = "+ULbWgH1mTXCs79dgOqTNw==";

        // when
        String password = AESEncrypter.decrypt(encrypted);

        // then
        assertEquals("secret", password);
    }

    @Test
    public void umlaute_vm() throws Exception {
        // given
        String password = "e$_w=x268WEI3vf7ä";
        String vmEncrypted = AESEncrypterVm.encrypt(password);

        // when
        String vmDecrypted = AESEncrypterVm.decrypt(vmEncrypted);

        // then
        assertEquals(password, vmDecrypted);
    }

    @Test
    public void umlaute() throws Exception {
        // given
        String password = "e$_w=x268WEI3vf7ä";
        String encrypted = AESEncrypter.encrypt(password);

        // when
        String decrypted = AESEncrypter.decrypt(encrypted);

        // then
        assertEquals(password, decrypted);
    }

    @Test
    public void uwe() throws Exception {
        // given
        String utf8_encoded = "k95HaoH8WYFxAwdKEdaZq+nx3oyR0hqM9Y3m85U+Lf8=";

        // then
        String utfMinus8_decoded = AESEncrypter.decrypt(utf8_encoded);

        // then
        assertEquals("e$_w=x268WEI3vf7ä", utfMinus8_decoded);
    }
}
