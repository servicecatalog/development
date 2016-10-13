/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 12.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.domain;

import java.io.Serializable;
import java.security.GeneralSecurityException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.security.AESEncrypter;

/**
 * A custom setting for the asynchronous provisioning proxy.
 * 
 * @author miethaner
 * 
 */
@Entity
@IdClass(CustomSetting.ScopedSettingKey.class)
@NamedQueries({
        @NamedQuery(name = "CustomSetting.getForOrg", query = "SELECT cs FROM CustomSetting cs WHERE cs.organizationId = :organizationId") })
public class CustomSetting {

    /**
     * Setting keys ending with this suffix will have their values stored
     * encrypted.
     */
    public static final String CRYPT_KEY_SUFFIX = "_PWD";

    /**
     * The key of the custom setting.
     */
    @Id
    private String settingKey;

    @Id
    private String organizationId;

    /**
     * The value of the custom setting.
     */
    @Column(nullable = false)
    private String settingValue;

    public String getSettingKey() {
        return settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDecryptedValue(String parameterValue)
            throws BadResultException {
        try {
            this.settingValue = isEncrypted()
                    ? AESEncrypter.encrypt(parameterValue) : parameterValue;
        } catch (GeneralSecurityException e) {
            throw new BadResultException(String.format(
                    "Parameter for key '%s' could not be encrypted",
                    getSettingKey()));
        }
    }

    public String getDecryptedValue() throws BadResultException {
        try {
            return isEncrypted() ? AESEncrypter.decrypt(settingValue)
                    : settingValue;
        } catch (GeneralSecurityException e) {
            throw new BadResultException(String.format(
                    "Parameter for key '%s' could not be decrypted",
                    getSettingKey()));
        }
    }

    public boolean isEncrypted() {
        return settingKey != null && settingKey.endsWith(CRYPT_KEY_SUFFIX);
    }

    public static class ScopedSettingKey implements Serializable {

        private static final long serialVersionUID = 6127964456252235253L;

        protected String settingKey;
        protected String organizationId;

        public ScopedSettingKey() {
        }

        public ScopedSettingKey(String settingKey, String organizationId) {
            this.settingKey = settingKey;
            this.organizationId = organizationId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            ScopedSettingKey other = (ScopedSettingKey) obj;
            return ((settingKey == null && other.settingKey == null)
                    || (settingKey != null
                            && settingKey.equals(other.settingKey)))
                    && ((organizationId == null && other.organizationId == null)
                            || (organizationId != null && organizationId
                                    .equals(other.organizationId)));
        }

        @Override
        public int hashCode() {
            int result = (settingKey != null ? (53 * settingKey.hashCode())
                    : 0);
            result = result + (organizationId != null
                    ? (13 * organizationId.hashCode()) : 0);
            return result;
        }
    }
}
