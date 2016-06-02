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
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.oscm.types.enumtypes.UdaTargetType;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;

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

    @Field(analyzer = @Analyzer(definition = "customanalyzer"))
    private String defaultValue;

    /**
     * The target type of the UDA.
     */
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private UdaTargetType targetType;

    /**
     * The configuration type of the UDA.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
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
