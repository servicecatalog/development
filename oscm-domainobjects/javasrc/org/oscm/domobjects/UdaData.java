/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: weiser                                            
 *                                                                              
 *  Creation Date: 13.10.2010                                                      
 *                                                                              
 *  Completion Time: 13.10.2010                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;

/**
 * The data container for a uda instance - holds the information about the value
 * and the key of the object the instance is attached on.
 * 
 * @author weiser
 * 
 */
@Embeddable
public class UdaData extends DomainDataContainer {

    private static final long serialVersionUID = 6763669079651899136L;

    @Column(nullable = false)
    private long targetObjectKey;

    @Field(analyzer = @Analyzer(definition = "customanalyzer"))
    private String udaValue;

    public long getTargetObjectKey() {
        return targetObjectKey;
    }

    public void setTargetObjectKey(long targetObjectKey) {
        this.targetObjectKey = targetObjectKey;
    }

    public String getUdaValue() {
        return udaValue;
    }

    public void setUdaValue(String udaValue) {
        this.udaValue = udaValue;
    }
}
