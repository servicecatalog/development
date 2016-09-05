/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Peter Pock                                             
 *                                                                              
 *  Creation Date: 14.06.2010                                                      
 *                                                                              
 *  Completion Time: 15.06.2010                                             
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.oscm.converter.XMLSerializer;
import org.oscm.domobjects.converters.TPPNConverter;
import org.oscm.domobjects.handling.XmlStringCleaner;
import org.oscm.internal.types.enumtypes.*;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.types.enumtypes.TriggerProcessParameterName;

/**
 * JPA managed entity representing the trigger process parameter data.
 * 
 * @author pock
 * 
 */
@Embeddable
public class TriggerProcessParameterData extends DomainDataContainer {

    private static final long serialVersionUID = 8283145624102989170L;

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(TriggerProcessParameterData.class);

    /**
     * If all enumeration classes used by the serialized objects (only necessary
     * for XML serialization)
     */
    private static Class<?> enumArray[] = { EventType.class,
            OrganizationRoleType.class, ParameterType.class,
            ParameterValueType.class, PaymentType.class,
            PaymentCollectionType.class, PricingPeriod.class,
            ServiceAccessType.class, ServiceStatus.class, Salutation.class,
            SessionType.class, SettingType.class, SubscriptionStatus.class,
            TriggerType.class, TriggerProcessStatus.class,
            TriggerProcessParameterName.class, UserAccountStatus.class,
            UserRoleType.class, UdaConfigurationType.class,
            ParameterModificationType.class, OfferingType.class };

    /**
     * Creates an object from the xml string.
     * 
     * @param str
     *            the xml string representing the object to create.
     * @return the created object.
     */
    private static Object fromXml(String str) {
        XMLDecoder decoder = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(
                    str.getBytes("UTF-8"));
            decoder = new XMLDecoder(in);
            Object obj = decoder.readObject();
            return obj;
        } catch (Exception e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Object encoding failed", e);
            logger.logError(Log4jLogger.SYSTEM_LOG, se,
                    LogMessageIdentifier.ERROR_OBJECT_ENCODING_FAILED);
            throw se;
        } finally {
            if (decoder != null)
                decoder.close();
        }
    }

    /**
     * Serializes an object to a string.
     * 
     * @param source
     *            the object to serialize.
     * @return the string representing the object.
     */
    public static String getVOSerialization(Object source) {
        return XmlStringCleaner.cleanString(toXml(source));
    }

    private static String toXml(Object source) {
        String xml = null;
        try {
            xml = XMLSerializer.toXml(source,
                    TriggerProcessParameterData.enumArray);
        } catch (Exception e) {
            SaaSSystemException se = new SaaSSystemException(
                    "Object encoding failed", e);
            TriggerProcessParameterData.logger.logError(Log4jLogger.SYSTEM_LOG,
                    se, LogMessageIdentifier.ERROR_OBJECT_ENCODING_FAILED);
            throw se;
        }
        return xml;
    }

    /**
     * Creates an object from the given string.
     * 
     * @param clazz
     *            the class of the object to create.
     * @param str
     *            the string representing the object to create.
     * @return the created object.
     */
    public static <T> T getVOFromSerialization(Class<T> clazz, String str) {
        final Object obj;
        obj = fromXml(str);
        return clazz.cast(obj);
    }

    /**
     * The name of the parameter.
     */
    @Convert(converter = TPPNConverter.class)
    @Column(nullable = false)
    private TriggerProcessParameterName name;

    /**
     * The serialized parameter value.
     */
    private String serializedValue;
    private boolean serialized = false;

    public TriggerProcessParameterName getName() {
        return name;
    }

    public void setName(TriggerProcessParameterName name) {
        this.name = name;
    }

    public <T> T getValue(Class<T> clazz) {
        if (serializedValue != null) {
            if (serialized) {
                return clazz.cast(fromXml(serializedValue));
            } else {
                return clazz.cast(serializedValue);
            }

        }
        return null;
    }

    public void setValue(Object value) {
        if (value == null) {
            this.serializedValue = null;
        } else {
            this.serializedValue = XmlStringCleaner.cleanString(toXml(value));
            serialized = true;
        }
    }

    protected String getSerializedValue() {
        return serializedValue;
    }

    protected void setSerializedValue(String serializedValue) {
        this.serializedValue = serializedValue;
        serialized = false;
    }

}
