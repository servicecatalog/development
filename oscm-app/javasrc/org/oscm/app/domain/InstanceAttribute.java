/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 12.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.domain;

import java.security.GeneralSecurityException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.security.AESEncrypter;

/**
 * Represents a attributes setting for a service instance.
 * 
 * @author miethaner
 * 
 */
@NamedQueries({
        @NamedQuery(name = "InstanceAttribute.getAllForInstanceId", query = "SELECT ia FROM InstanceAttribute ia WHERE ia.serviceInstance.instanceId = :sid and ia.serviceInstance.controllerId = :cid") })
@Entity
public class InstanceAttribute {

    /**
     * Setting keys ending with this suffix will have their values stored
     * encrypted.
     */
    public static final String CRYPT_KEY_SUFFIX = "_PWD";

    /**
     * The technical key of the entity.
     */
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "do_seq")
    @SequenceGenerator(name = "do_seq", allocationSize = 1000)
    private long tkey;

    /**
     * Reference to the service instance containing this parameter.
     */
    @ManyToOne(optional = false)
    private ServiceInstance serviceInstance;

    /**
     * The value of the instance parameter.
     */
    @Column(nullable = false)
    private String attributeValue;

    /**
     * The key of the parameter.
     */
    private String attributeKey;

    public long getTkey() {
        return tkey;
    }

    public void setTkey(long tkey) {
        this.tkey = tkey;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getDecryptedValue() throws BadResultException {
        try {
            return isEncrypted() ? AESEncrypter.decrypt(attributeValue)
                    : attributeValue;
        } catch (GeneralSecurityException e) {
            throw new BadResultException(String.format(
                    "Attribute for key '%s' could not be decrypted",
                    getAttributeKey()));
        }
    }

    public void setAttributeValue(String parameterValue) {
        this.attributeValue = parameterValue;
    }

    public void setDecryptedValue(String parameterValue)
            throws BadResultException {
        try {
            this.attributeValue = isEncrypted()
                    ? AESEncrypter.encrypt(parameterValue) : parameterValue;
        } catch (GeneralSecurityException e) {
            throw new BadResultException(String.format(
                    "Attribute for key '%s' could not be encrypted",
                    getAttributeKey()));
        }
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String parameterKey) {
        this.attributeKey = parameterKey;
    }

    public boolean isEncrypted() {
        return attributeKey != null && attributeKey.endsWith(CRYPT_KEY_SUFFIX);
    }
}
