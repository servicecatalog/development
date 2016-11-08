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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.security.AESEncrypter;

/**
 * A custom attribute for the asynchronous provisioning proxy.
 * 
 * @author miethaner
 * 
 */
@Entity
@IdClass(CustomAttribute.ScopedSettingKey.class)
@NamedQueries({
        @NamedQuery(name = "CustomAttribute.getForOrg", query = "SELECT ca FROM CustomAttribute ca WHERE ca.organizationId = :organizationId"),
        @NamedQuery(name = "CustomAttribute.deleteForOrg", query = "DELETE FROM CustomAttribute ca WHERE ca.organizationId = :organizationId") })
public class CustomAttribute {

    /**
     * Setting keys ending with this suffix will have their values stored
     * encrypted.
     */
    public static final String CRYPT_KEY_SUFFIX = "_PWD";

    /**
     * The key of the custom setting.
     */
    @Id
    private String attributeKey;

    @Id
    private String organizationId;

    /**
     * The value of the custom setting.
     */
    private String attributeValue;

    public String getAttributeKey() {
        return attributeKey;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeKey(String settingKey) {
        this.attributeKey = settingKey;
    }

    public void setAttributeValue(String settingValue) {
        this.attributeValue = settingValue;
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
            this.attributeValue = isEncrypted()
                    ? AESEncrypter.encrypt(parameterValue) : parameterValue;
        } catch (GeneralSecurityException e) {
            throw new BadResultException(String.format(
                    "Parameter for key '%s' could not be encrypted",
                    getAttributeKey()));
        }
    }

    public String getDecryptedValue() throws BadResultException {
        try {
            return isEncrypted() ? AESEncrypter.decrypt(attributeValue)
                    : attributeValue;
        } catch (GeneralSecurityException e) {
            throw new BadResultException(String.format(
                    "Parameter for key '%s' could not be decrypted",
                    getAttributeKey()));
        }
    }

    public boolean isEncrypted() {
        return attributeKey != null && attributeKey.endsWith(CRYPT_KEY_SUFFIX);
    }

    public static class ScopedSettingKey implements Serializable {

        private static final long serialVersionUID = 6127964456252235253L;

        protected String attributeKey;
        protected String organizationId;

        public ScopedSettingKey() {
        }

        public ScopedSettingKey(String attributeKey, String organizationId) {
            this.attributeKey = attributeKey;
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
            return ((attributeKey == null && other.attributeKey == null)
                    || (attributeKey != null
                            && attributeKey.equals(other.attributeKey)))
                    && ((organizationId == null && other.organizationId == null)
                            || (organizationId != null && organizationId
                                    .equals(other.organizationId)));
        }

        @Override
        public int hashCode() {
            int result = (attributeKey != null ? (53 * attributeKey.hashCode())
                    : 0);
            result = result + (organizationId != null
                    ? (13 * organizationId.hashCode()) : 0);
            return result;
        }
    }
}
