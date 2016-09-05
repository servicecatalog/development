/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: weiser                                                      
 *                                                                              
 *  Creation Date: 13.10.2010                                                      
 *                                                                              
 *  Completion Time: 14.10.2010                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.oscm.domobjects.converters.UCTConverter;
import org.oscm.domobjects.converters.UTTConverter;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.types.enumtypes.UdaTargetType;

/**
 * @author weiser
 * 
 */
@Embeddable
public class UdaDefinitionData extends DomainDataContainer {

    private static final long serialVersionUID = -1304062239383112626L;

    /**
     * The identifier of the UDA.
     */
    @Column(nullable = false)
    private String udaId;

    private String defaultValue;

    /**
     * The target type of the UDA.
     */
    @Column(nullable = false, updatable = false)
    @Convert(converter = UTTConverter.class)
    private UdaTargetType targetType;

    /**
     * The configuration type of the UDA.
     */
    @Column(nullable = false)
    @Convert(converter = UCTConverter.class)
    private UdaConfigurationType configurationType;

    public String getUdaId() {
        return udaId;
    }

    public void setUdaId(String udaId) {
        this.udaId = udaId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public UdaTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(UdaTargetType targetType) {
        this.targetType = targetType;
    }

    public void setConfigurationType(UdaConfigurationType configurationType) {
        this.configurationType = configurationType;
    }

    public UdaConfigurationType getConfigurationType() {
        return configurationType;
    }
}
